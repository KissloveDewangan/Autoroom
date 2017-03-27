
package in.co.opensoftlab.yourstore.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeocodeResult {

    @SerializedName("results")
    @Expose
    private List<Result> results = new ArrayList<>();
    @SerializedName("status")
    @Expose
    private String status;

    public GeocodeResult() {
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
