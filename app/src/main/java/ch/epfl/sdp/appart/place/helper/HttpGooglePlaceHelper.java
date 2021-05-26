package ch.epfl.sdp.appart.place.helper;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.libraries.places.api.Places;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.location.Location;

import static ch.epfl.sdp.appart.place.helper.HttpGooglePlaceHelper.NearbySearchPlaceURLBuilder.makeNearbyPlaceByDistanceURL;
import static ch.epfl.sdp.appart.place.helper.HttpGooglePlaceHelper.NearbySearchPlaceURLBuilder.makeNearbyPlaceByRadiusURL;

/**
 * Allows to make a query to Google API by using an http request and returns
 * a Json string.
 */
public class HttpGooglePlaceHelper implements PlaceHelper {

    private final String apiKey;
    private final Context context;

    public HttpGooglePlaceHelper(Context context) {
        this.apiKey = context.getResources().getString(R.string.maps_api_key);
        Places.initialize(context.getApplicationContext(), apiKey);
        this.context = context;
    }

    @Override
    public CompletableFuture<String> query(Location location, int radius,
                                           String type) {
        CompletableFuture<String> result = new CompletableFuture<>();
        URL url = makeNearbyPlaceByRadiusURL(this.apiKey, location, radius,
                type);
        makeHttpRequest(url, result);
        return result;
    }

    @Override
    public CompletableFuture<String> query(Location location, String type) {
        CompletableFuture<String> result = new CompletableFuture<>();
        URL url = makeNearbyPlaceByDistanceURL(apiKey, location, type);
        makeHttpRequest(url, result);
        return result;
    }

    /**
     * Makes the http request described by the URL.
     *
     * @param url
     * @param result
     */
    private void makeHttpRequest(URL url, CompletableFuture<String> result) {
        StringRequest nearbyPlacesRequest =
                new StringRequest(Request.Method.GET, url.toString(),
                        result::complete,
                        error -> result.completeExceptionally(error.getCause()));
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(nearbyPlacesRequest);
    }

    /**
     * Builder that encapsulate the creation of the query URL
     */
    static class NearbySearchPlaceURLBuilder {

        private final static String TEXT_SEARCH_BASE_URL = "https://maps" +
                ".googleapis.com/maps/api/place/nearbysearch/";


        /**
         * Builds a URL for a request of nearby places ranked by distance
         * from the location. Therefore, this doesn't require a radius.
         *
         * @param apiKey   the Google API key
         * @param location the location around which we want the nearby
         *                 locations
         * @param type     the type of location to search
         * @return the URL
         */
        public static URL makeNearbyPlaceByDistanceURL(String apiKey,
                                                       Location location,
                                                       String type) {
            StringBuilder sb = makeURLNearbyPlaceBase(apiKey, location, type);
            sb.append("rankby=distance");
            try {

                return new URL(sb.toString());
            } catch (MalformedURLException e) {
                throw new IllegalStateException("the built url is malformed !");
            }


        }

        /**
         * Builds a URL for a request of nearby places within a circle
         * centered at location and with radius radius.
         *
         * @param apiKey   the Google API key
         * @param location the location around which we want the nearby
         *                 locations
         * @param radius the radius
         * @param type     the type of location to search
         * @return the URL
         */
        public static URL makeNearbyPlaceByRadiusURL(String apiKey,
                                                     Location location,
                                                     int radius, String type) {


            StringBuilder sb = makeURLNearbyPlaceBase(apiKey, location, type);
            sb.append("radius=").append(radius);
            try {
                return new URL(sb.toString());
            } catch (MalformedURLException e) {
                throw new IllegalStateException("the built url is malformed !");
            }
        }

        private static StringBuilder makeURLNearbyPlaceBase(String apiKey,
                                                            Location location
                , String type) {
            StringBuilder sb = new StringBuilder();
            sb.append(TEXT_SEARCH_BASE_URL).append("json?");
            sb.append("location=").append(location.latitude).append(",").append(location.longitude).append("&");
            sb.append("type=").append(type.trim()).append("&");
            sb.append("key=").append(apiKey).append("&");
            return sb;
        }

    }
}
