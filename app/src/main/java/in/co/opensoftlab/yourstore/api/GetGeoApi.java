package in.co.opensoftlab.yourstore.api;

import in.co.opensoftlab.yourstore.model.GeocodeResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by dewangankisslove on 21-12-2016.
 */

public interface GetGeoApi {
    @GET("/maps/api/geocode/json?sensor=false")
    Call<GeocodeResult> getGeoCoordinates(@Query("address") String address, @Query("key") String apiKey);

    @GET("/maps/api/geocode/json?sensor=false")
    Call<GeocodeResult> getFormattedAddress(@Query("latlng") String latlng, @Query("key") String apiKey);
}
