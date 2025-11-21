package hedral_backend.hederaproof.model;

import lombok.Data;

@Data
public class MintResponse {
    private boolean success;
    private String tokenId;
    private long serial;
    private String timestamp;
    private String qrCodeIpfsCid;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getQrCodeIpfsCid() {
        return qrCodeIpfsCid;
    }

    public void setQrCodeIpfsCid(String qrCodeIpfsCid) {
        this.qrCodeIpfsCid = qrCodeIpfsCid;
    }
}