package in.co.opensoftlab.yourstore.model;

/**
 * Created by dewangankisslove on 27-01-2017.
 */
public class NotifyItemBuyer {
    String requestId;
    String requestDate;
    String sellerName;
    String sellerId;
    String requestType;
    String sellerUrl;
    String msg;
    String status;

    public NotifyItemBuyer() {
    }

    public NotifyItemBuyer(String requestId, String requestDate, String sellerName, String sellerId, String requestType, String sellerUrl, String msg, String status) {
        this.requestId = requestId;
        this.requestDate = requestDate;
        this.sellerName = sellerName;
        this.sellerId = sellerId;
        this.requestType = requestType;
        this.sellerUrl = sellerUrl;
        this.msg = msg;
        this.status = status;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getSellerUrl() {
        return sellerUrl;
    }

    public void setSellerUrl(String sellerUrl) {
        this.sellerUrl = sellerUrl;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
