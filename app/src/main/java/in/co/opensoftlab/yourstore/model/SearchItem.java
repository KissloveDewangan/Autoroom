package in.co.opensoftlab.yourstore.model;

/**
 * Created by dewangankisslove on 08-01-2017.
 */
public class SearchItem {
    String id;
    String name;

    public SearchItem() {
    }

    public SearchItem(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
