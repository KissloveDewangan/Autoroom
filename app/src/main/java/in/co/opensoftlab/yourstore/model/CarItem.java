package in.co.opensoftlab.yourstore.model;

/**
 * Created by dewangankisslove on 09-12-2016.
 */

public class CarItem {
    String id;
    String productType;
    String productUrl;
    String productName;
    int productPrice;
    String info;

    public CarItem() {
    }

    public CarItem(String id, String productType, String productUrl, String productName, int productPrice, String info) {
        this.id = id;
        this.productType = productType;
        this.productUrl = productUrl;
        this.productName = productName;
        this.productPrice = productPrice;
        this.info = info;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
