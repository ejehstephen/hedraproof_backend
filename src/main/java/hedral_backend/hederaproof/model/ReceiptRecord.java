package hedral_backend.hederaproof.model;

public class ReceiptRecord {
    private String item;
    private long amount;
    private String status;
    private String date;
    private String tokenId;
    private long serial;
    private String ownerAccountId;

    public ReceiptRecord(String item, long amount, String status, String date, String tokenId, long serial, String ownerAccountId) {
        this.item = item;
        this.amount = amount;
        this.status = status;
        this.date = date;
        this.tokenId = tokenId;
        this.serial = serial;
        this.ownerAccountId = ownerAccountId;
    }

    public String getOwnerAccountId() {
        return ownerAccountId;
    }

    public void setOwnerAccountId(String ownerAccountId) {
        this.ownerAccountId = ownerAccountId;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
}