package in.co.opensoftlab.yourstore.model;

/**
 * Created by dewangankisslove on 30-01-2017.
 */

public class ProductViewModel {
    String productName;
    int noViews;

    public ProductViewModel() {
    }

    public ProductViewModel(String productName, int noViews) {
        this.productName = productName;
        this.noViews = noViews;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getNoViews() {
        return noViews;
    }

    public void setNoViews(int noViews) {
        this.noViews = noViews;
    }
}
