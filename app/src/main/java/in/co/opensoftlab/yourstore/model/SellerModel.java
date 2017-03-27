package in.co.opensoftlab.yourstore.model;

/**
 * Created by dewangankisslove on 20-01-2017.
 */

public class SellerModel {
    String name;
    String location;
    String geoLocation;

    public SellerModel() {
    }

    public SellerModel(String name, String location, String geoLocation) {
        this.name = name;
        this.location = location;
        this.geoLocation = geoLocation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(String geoLocation) {
        this.geoLocation = geoLocation;
    }
}
