package in.co.opensoftlab.yourstore.model;

/**
 * Created by dewangankisslove on 27-12-2016.
 */

public class SpecificationItem {
    String name;
    String value;

    public SpecificationItem(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
