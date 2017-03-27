package in.co.opensoftlab.yourstore.model;

/**
 * Created by dewangankisslove on 09-01-2017.
 */
public class SelectedFilterItem {
    String filterName;

    public SelectedFilterItem(String filterName) {
        this.filterName = filterName;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }
}
