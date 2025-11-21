package hedral_backend.hederaproof.config;

import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class HederaClientConfig {

    @Autowired
    private Environment env;

    @Bean
    public Client hederaClient() {
        String hederaNetwork = env.getProperty("hedera.network");
        String operatorId = env.getProperty("hedera.operator.id");
        String operatorKey = env.getProperty("hedera.operator.key");

        if (hederaNetwork == null || operatorId == null || operatorKey == null) {
            throw new IllegalArgumentException("Hedera network, operator ID, or operator key not configured.");
        }

        Client client;
        switch (hederaNetwork.toLowerCase()) {
            case "mainnet":
                client = Client.forMainnet();
                break;
            case "testnet":
                client = Client.forTestnet();
                break;
            case "previewnet":
                client = Client.forPreviewnet();
                break;
            default:
                throw new IllegalArgumentException("Unknown Hedera network: " + hederaNetwork);
        }

        // Configure the client with the operator account ID and private key
        AccountId operatorAccountId = AccountId.fromString(operatorId);
        PrivateKey operatorPrivateKey = PrivateKey.fromString(operatorKey);
        client.setOperator(operatorAccountId, operatorPrivateKey);
        client.setMaxTransactionFee(new Hbar(10)); // Set a reasonable max transaction fee
        client.setDefaultMaxTransactionFee(new Hbar(10)); // Set a reasonable default max transaction fee

        return client;
    }
}