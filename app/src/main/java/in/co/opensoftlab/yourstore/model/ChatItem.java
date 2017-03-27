package in.co.opensoftlab.yourstore.model;

/**
 * Created by dewangankisslove on 03-01-2017.
 */
public class ChatItem {
    private String id;
    private String sellerId;
    private String seller;
    private String sellerUrl;
    private String storeName;
    private int unreads;

    public ChatItem() {
    }

    public ChatItem(String id, String seller, String sellerUrl, String storeName, int unreads, String sellerId) {
        this.id = id;
        this.seller = seller;
        this.sellerUrl = sellerUrl;
        this.storeName = storeName;
        this.unreads = unreads;
        this.sellerId = sellerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getSellerUrl() {
        return sellerUrl;
    }

    public void setSellerUrl(String sellerUrl) {
        this.sellerUrl = sellerUrl;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public int getUnreads() {
        return unreads;
    }

    public void setUnreads(int unreads) {
        this.unreads = unreads;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }
}
