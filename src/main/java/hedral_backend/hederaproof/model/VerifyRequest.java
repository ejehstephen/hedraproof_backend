package hedral_backend.hederaproof.model;

public class VerifyRequest {
    private String tokenId;
    private long serial;

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public long getSerial() {
        return serial;
    }

    public void setSerial(long serial) {
        this.serial = serial;
    }
}