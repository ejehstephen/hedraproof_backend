package hedral_backend.hedral_proof;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = hedral_backend.hederaproof.HederaProofApplication.class)
@TestPropertySource(properties = {
    "hedera.network=testnet",
    "hedera.operator.id=0.0.12345",
    "hedera.operator.key=302e020100300506032b657004220420b9c3ebac81a72aafa5490cc78111643d016d311e60869436fbb91c73307ed35a"
})
class HederaProofApplicationTests {

	@Test
	void contextLoads() {
	}

}
