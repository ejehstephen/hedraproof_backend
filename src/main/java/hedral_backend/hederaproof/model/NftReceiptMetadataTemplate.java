package hedral_backend.hederaproof.model;

import lombok.Data;

@Data
public class NftReceiptMetadataTemplate {
    private String appName;
    private String actionType;
    private String itemName;
    private String qrCodeIpfsCid;

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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getQrCodeIpfsCid() {
        return qrCodeIpfsCid;
    }

    public void setQrCodeIpfsCid(String qrCodeIpfsCid) {
        this.qrCodeIpfsCid = qrCodeIpfsCid;
    }
}