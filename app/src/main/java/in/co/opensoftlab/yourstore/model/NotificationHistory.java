package in.co.opensoftlab.yourstore.model;

/**
 * Created by dewangankisslove on 27-01-2017.
 */

public class NotificationHistory {
    String orderId;
    String productId;
    String timeOrder;

    public NotificationHistory() {
    }

    public NotificationHistory(String orderId, String productId, String timeOrder) {
        this.orderId = orderId;
        this.productId = productId;
        this.timeOrder = timeOrder;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTimeOrder() {
        return timeOrder;
    }

    public void setTimeOrder(String timeOrder) {
        this.timeOrder = timeOrder;
    }
}
