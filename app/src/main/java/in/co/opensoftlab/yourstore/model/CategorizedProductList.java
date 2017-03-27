package in.co.opensoftlab.yourstore.model;

import java.util.List;

/**
 * Created by dewangankisslove on 09-12-2016.
 */

public class CategorizedProductList {
    String categoryName;
    List<CarItem> filterProductList;

    public CategorizedProductList(String categoryName, List<CarItem> filterProductList) {
        this.categoryName = categoryName;
        this.filterProductList = filterProductList;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<CarItem> getFilterProductList() {
        return filterProductList;
    }

    public void setFilterProductList(List<CarItem> filterProductList) {
        this.filterProductList = filterProductList;
    }
}
