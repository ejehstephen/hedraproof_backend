package hedral_backend.hederaproof.service;

import com.hedera.hashgraph.sdk.*;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;
import hedral_backend.hederaproof.model.MintRequest;
import hedral_backend.hederaproof.model.MintResponse;
import hedral_backend.hederaproof.model.VerifyRequest;
import hedral_backend.hederaproof.model.VerifyResponse;
import hedral_backend.hederaproof.model.ReceiptRecord;
import hedral_backend.hederaproof.model.NftReceiptMetadataTemplate;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class HederaService {

    private final Client client;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(HederaService.class);

    private final List<ReceiptRecord> receiptRecords =
            Collections.synchronizedList(new java.util.ArrayList<>());

    private final OkHttpClient httpClient;
    private final String pinataApiKey;
    private final String pinataSecretApiKey;

    // ✅ FIXED CONSTRUCTOR
    public HederaService(
            Client client,
            @Value("${pinata.api.key}") String pinataApiKey,
            @Value("${pinata.secret.api.key}") String pinataSecretApiKey
    ) {
        this.client = client;
        this.pinataApiKey = pinataApiKey;
        this.pinataSecretApiKey = pinataSecretApiKey;

        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    private byte[] generateQrCodeImage(String content) throws Exception {
        int width = 200;
        int height = 200;

        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix = new QRCodeWriter().encode(
                content, BarcodeFormat.QR_CODE, width, height, hints
        );

        try (ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, "png", pngOutputStream);
            return pngOutputStream.toByteArray();
        }
    }

    private String uploadToPinata(byte[] data) throws IOException {

        String pinataUrl = "https://api.pinata.cloud/pinning/pinFileToIPFS";
        String fileName = "qrcode_" + System.currentTimeMillis() + ".png";

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                        "file",
                        fileName,
                        RequestBody.create(MediaType.parse("application/octet-stream"), data)
                )
                .build();

        Request request = new Request.Builder()
                .url(pinataUrl)
                .post(body)
                .header("pinata_api_key", pinataApiKey)
                .header("pinata_secret_api_key", pinataSecretApiKey)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                log.error("Pinata upload failed: {} - {}", response.code(), Objects.requireNonNull(response.body()).string());
                throw new IOException("Error: " + response);
            }
            String res = Objects.requireNonNull(response.body()).string();
            log.info("Pinata upload successful. Response: {}", res);
            return new ObjectMapper().readTree(res).get("IpfsHash").asText();
        }
    }

    private String uploadJsonToPinata(String jsonContent) throws IOException {
        String pinataUrl = "https://api.pinata.cloud/pinning/pinFileToIPFS";
        String fileName = "metadata_" + System.currentTimeMillis() + ".json";

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                        "file",
                        fileName,
                        RequestBody.create(MediaType.parse("application/json"), jsonContent.getBytes())
                )
                .build();

        Request request = new Request.Builder()
                .url(pinataUrl)
                .post(body)
                .header("pinata_api_key", pinataApiKey)
                .header("pinata_secret_api_key", pinataSecretApiKey)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error uploading JSON to Pinata: " + response);
            }
            String res = Objects.requireNonNull(response.body()).string();
            return new ObjectMapper().readTree(res).get("IpfsHash").asText();
        }
    }

    private String uploadToIpfs(byte[] data) throws IOException {
        return uploadToPinata(data);
    }

    private String downloadFromIpfs(String ipfsCid) throws IOException {
        String gatewayUrl = "https://gateway.pinata.cloud/ipfs/" + ipfsCid;

        Request request = new Request.Builder()
                .url(gatewayUrl)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error downloading from IPFS: " + response);
            }
            return Objects.requireNonNull(response.body()).string();
        }
    }

    public MintResponse mintNftReceipt(MintRequest request) throws Exception {

        NftReceiptMetadataTemplate metadataTemplate = new NftReceiptMetadataTemplate();
        metadataTemplate.setAppName(request.getAppName());
        metadataTemplate.setActionType(request.getActionType());
        metadataTemplate.setItemName(request.getItem());

        String qrCid = null;
        // Construct QR code content from MintRequest fields
        String qrCodeContent = String.format(
                "Item: %s, Amount: %d, Wallet: %s, App: %s, Action: %s",
                request.getItem(),
                request.getAmount(),
                request.getUserWalletAddress(),
                request.getAppName(),
                request.getActionType()
        );
        qrCid = uploadToIpfs(generateQrCodeImage(qrCodeContent));
        metadataTemplate.setQrCodeIpfsCid(qrCid);

        String metadataJson = objectMapper.writeValueAsString(metadataTemplate);
        String metadataCid = uploadJsonToPinata(metadataJson);

        TokenCreateTransaction createTx = new TokenCreateTransaction()
                .setTokenName("HederaProofReceipt")
                .setTokenSymbol("HPR")
                .setTokenType(TokenType.NON_FUNGIBLE_UNIQUE)
                .setDecimals(0)
                .setInitialSupply(0)
                .setTreasuryAccountId(client.getOperatorAccountId())
                .setSupplyType(TokenSupplyType.FINITE)
                .setMaxSupply(1)
                .setSupplyKey(client.getOperatorPublicKey());

        TransactionResponse createResponse = createTx.execute(client);
        TokenId tokenId = createResponse.getReceipt(client).tokenId;

        TokenMintTransaction mintTx = new TokenMintTransaction()
                .setTokenId(tokenId)
                .addMetadata(metadataCid.getBytes());

        TransactionResponse mintResponse = mintTx.execute(client);
        long serial = mintResponse.getReceipt(client).serials.get(0);

        // Save record in memory
        receiptRecords.add(
                new ReceiptRecord(
                        request.getItem(),
                        request.getAmount(),
                        "MINTED",
                        Instant.now().toString(),
                        tokenId.toString(),
                        serial,
                        request.getUserWalletAddress()
                )
        );

        MintResponse response = new MintResponse();
        response.setSuccess(true);
        response.setTokenId(tokenId.toString());
        response.setSerial(serial);
        response.setTimestamp(Instant.now().toString());
        response.setQrCodeIpfsCid(qrCid);

        return response;
    }

    public List<ReceiptRecord> getAllReceiptRecords() {
        return Collections.unmodifiableList(receiptRecords);
    }

    public hedral_backend.hederaproof.model.TokenInfoResponse getTokenInfo(String tokenId) throws Exception {
        TokenInfo hederaTokenInfo = new TokenInfoQuery()
                .setTokenId(TokenId.fromString(tokenId))
                .execute(client);

        hedral_backend.hederaproof.model.TokenInfoResponse response = new hedral_backend.hederaproof.model.TokenInfoResponse();
        response.setTokenId(hederaTokenInfo.tokenId.toString());
        response.setName(hederaTokenInfo.name);
        response.setSymbol(hederaTokenInfo.symbol);
        response.setTotalSupply(hederaTokenInfo.totalSupply);
        response.setMaxSupply(hederaTokenInfo.maxSupply);
        response.setSupplyKeySet(hederaTokenInfo.supplyKey != null);
        response.setTreasuryAccountId(hederaTokenInfo.treasuryAccountId.toString());

        return response;
    }

    public VerifyResponse verifyNftReceipt(VerifyRequest request) throws Exception {
        VerifyResponse response = new VerifyResponse();
        response.setVerified(false); // Default to false

        try {
            TokenId tokenId = TokenId.fromString(request.getTokenId());
            NftId nftId = new NftId(tokenId, request.getSerial());

            List<TokenNftInfo> nftInfos = new TokenNftInfoQuery()
                    .setNftId(nftId)
                    .execute(client);

            if (nftInfos.isEmpty()) {
                log.warn("No NFT info found for tokenId: {} serial: {}", request.getTokenId(), request.getSerial());
                return response;
            }

            TokenNftInfo nftInfo = nftInfos.get(0);

            // The on-chain metadata now stores the IPFS CID of the actual metadata JSON
            String metadataCid = new String(nftInfo.metadata);
            String metadataJson = downloadFromIpfs(metadataCid);
            Map<String, Object> metadata = objectMapper.readValue(metadataJson, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});

            response.setVerified(true);
            response.setMetadata(metadata);
            response.setOwner(nftInfo.accountId.toString());
            response.setTimestamp(nftInfo.creationTime.toString());

        } catch (Exception e) {
            log.error("Error verifying NFT receipt: {}", e.getMessage());
            // Optionally, set a more specific error message in the response
            response.setVerified(false);
        }
        return response;
    }

    public VerifyResponse getReceiptMetadata(String tokenId, long serial) throws Exception {
        VerifyResponse response = new VerifyResponse();
        response.setVerified(false);

        try {
            TokenId hederaTokenId = TokenId.fromString(tokenId);
            NftId nftId = new NftId(hederaTokenId, serial);

            List<TokenNftInfo> nftInfos = new TokenNftInfoQuery()
                    .setNftId(nftId)
                    .execute(client);

            if (nftInfos.isEmpty()) {
                log.warn("No NFT info found for tokenId: {} serial: {}", tokenId, serial);
                return response;
            }

            TokenNftInfo nftInfo = nftInfos.get(0);

            String metadataCid = new String(nftInfo.metadata);
            String metadataJson = downloadFromIpfs(metadataCid);
            Map<String, Object> metadata = objectMapper.readValue(metadataJson, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});

            response.setVerified(true);
            response.setMetadata(metadata);
            response.setOwner(nftInfo.accountId.toString());
            response.setTimestamp(nftInfo.creationTime.toString());

        } catch (Exception e) {
            log.error("Error retrieving NFT metadata: {}", e.getMessage());
            response.setVerified(false);
        }
        return response;
    }

    public List<ReceiptRecord> getOwnerReceipts(String accountId) {
        return receiptRecords.stream()
                .filter(record -> record.getOwnerAccountId().equals(accountId))
                .collect(java.util.stream.Collectors.toList());
    }
}
