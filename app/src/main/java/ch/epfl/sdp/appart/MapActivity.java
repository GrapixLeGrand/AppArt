package ch.epfl.sdp.appart;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.SupportMapFragment;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.location.LocationService;
import ch.epfl.sdp.appart.map.ApartmentInfoWindow;
import ch.epfl.sdp.appart.map.MapService;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.utils.PermissionRequest;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapActivity extends AppCompatActivity {

    @Inject
    DatabaseService databaseService;

    @Inject
    LocationService locationService;

    @Inject
    MapService mapService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getView().setContentDescription("WAITING");

        mapService.setActivity(this);


        Runnable onMapReadyCallback;

        String address =
                getIntent().getStringExtra(getString(R.string.intentLocationForMap));

        if (address != null) {
            mapService.setMapFragment(mapFragment);
            onMapReadyCallback = () -> {
                locationService.getLocationFromName(address).thenAccept(location -> {
                    mapService.addMarker(location, null, true, null);
                }).exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
            };
        } else {
            mapService.setInfoWindow(new ApartmentInfoWindow(this,
                    databaseService));
            mapService.setMapFragment(mapFragment);
            onMapReadyCallback = () -> {
                CompletableFuture<List<Card>> futureCards = databaseService
                        .getCards();
                futureCards.exceptionally(e -> {
                    Log.d("EXCEPTION_DB", e.getMessage());
                    return null;
                });

                futureCards.thenAccept(cards -> {
                    for (Card card : cards) {
                        locationService.getLocationFromName(card.getCity()).thenAccept(location -> {
                            mapService.addMarker(location, card, false,
                                    card.getCity());
                        }).exceptionally(e -> {
                            e.printStackTrace();
                            return null;
                        });

                    }
                });
            };
        }

        mapService.setOnReadyCallback(onMapReadyCallback);

        PermissionRequest.askForLocationPermission(this, () -> {
            Log.d("PERMISSION", "Location permission granted");
            mapFragment.getMapAsync(mapService);
        }, () -> {
            Log.d("PERMISSION", "Refused");
            finish();
        }, () -> Log.d("PERMISSION", "Popup"));

    }


    /*
        To implement the feature :
            - Set map center on user location
            - Get location from all the ads (or all the ads in the country),
            see if it is possible to get ad only
                if they satisfy a condition.
                It would be great if I could get all the ads (location only
                because if we have millions of ad this is going to be huge,
                or restrict by country for instance)
                , then transform the address into latitude and longitude,
                then maybe it is possible to ask the map object if a
                   specific location is on the map. If it is display it.
     */


}