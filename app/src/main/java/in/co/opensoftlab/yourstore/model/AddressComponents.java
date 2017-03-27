package in.co.opensoftlab.yourstore.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dewangankisslove on 06-03-2017.
 */

public class AddressComponents {
    @SerializedName("long_name")
    @Expose
    private String long_name;
    @SerializedName("types")
    @Expose
    private List<String> types = null;

    public AddressComponents() {
    }

    public String getLong_name() {
        return long_name;
    }

    public void setLong_name(String long_name) {
        this.long_name = long_name;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }
}
