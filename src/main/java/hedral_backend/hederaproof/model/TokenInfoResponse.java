package hedral_backend.hederaproof.model;

import lombok.Data;

@Data
public class TokenInfoResponse {
    private String tokenId;
    private String name;
    private String symbol;
    private long totalSupply;
    private long maxSupply;
    private boolean supplyKeySet;
    private String treasuryAccountId;

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public long getTotalSupply() {
        return totalSupply;
    }

    public void setTotalSupply(long totalSupply) {
        this.totalSupply = totalSupply;
    }

    public long getMaxSupply() {
        return maxSupply;
    }

    public void setMaxSupply(long maxSupply) {
        this.maxSupply = maxSupply;
    }

    public boolean isSupplyKeySet() {
        return supplyKeySet;
    }

    public void setSupplyKeySet(boolean supplyKeySet) {
        this.supplyKeySet = supplyKeySet;
    }

    public String getTreasuryAccountId() {
        return treasuryAccountId;
    }

    public void setTreasuryAccountId(String treasuryAccountId) {
        this.treasuryAccountId = treasuryAccountId;
    }
}