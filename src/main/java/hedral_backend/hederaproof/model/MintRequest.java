package hedral_backend.hederaproof.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MintRequest {
    private String item;
    private long amount;

    private String userWalletAddress;
    private String appName;
    private String actionType;

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


    public String getUserWalletAddress() {
        return userWalletAddress;
    }

    public void setUserWalletAddress(String userWalletAddress) {
        this.userWalletAddress = userWalletAddress;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

}