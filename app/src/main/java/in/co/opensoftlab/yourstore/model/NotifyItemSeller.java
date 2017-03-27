package in.co.opensoftlab.yourstore.model;

/**
 * Created by dewangankisslove on 27-01-2017.
 */
public class NotifyItemSeller {
    String requestId;
    String requestDate;
    String buyerName;
    String buyerId;
    String requestType;
    String buyerUrl;
    String msg;
    String status;

    public NotifyItemSeller() {
    }

    public NotifyItemSeller(String requestId, String requestDate, String buyerName, String buyerId, String requestType, String buyerUrl, String msg, String status) {
        this.requestId = requestId;
        this.requestDate = requestDate;
        this.buyerName = buyerName;
        this.buyerId = buyerId;
        this.requestType = requestType;
        this.buyerUrl = buyerUrl;
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

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getBuyerUrl() {
        return buyerUrl;
    }

    public void setBuyerUrl(String buyerUrl) {
        this.buyerUrl = buyerUrl;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
