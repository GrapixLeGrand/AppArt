package ch.epfl.sdp.appart;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.location.Location;
import ch.epfl.sdp.appart.location.place.address.Address;
import ch.epfl.sdp.appart.location.place.address.AddressFactory;
import ch.epfl.sdp.appart.place.PlaceAdapter;
import ch.epfl.sdp.appart.place.PlaceOfInterest;
import ch.epfl.sdp.appart.place.PlaceService;
import ch.epfl.sdp.appart.utils.ActivityCommunicationLayout;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PlaceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Inject
    public PlaceService placeService;

    private final static int DEFAULT_POSITION = 0;
    private int currentSelectedIndex = DEFAULT_POSITION;
    private final static String SELECT_ONE = "select one";

    private static final HashMap<String, String> TYPE_TO_KEYWORDS =
            new HashMap<String, String>() {
                {
                    put("bakery", "bakery");
                    put("super market", "migros+denner+coop");
                    put("library", "library");
                    put("university", "university+EPFL+UNIL");
                    put("drugstore", "drugstore");
                    put("fun", "cinema+party+dancing");
                    put("gym", "gym+fitness");
                    put("restaurant", "restaurant");
                }
            };

    private static final List<String> FIELDS =
            new ArrayList<>(TYPE_TO_KEYWORDS.keySet());

    static {
        String tmp = FIELDS.get(0);
        FIELDS.set(0, SELECT_ONE);
        FIELDS.add(tmp);
    }

    private final List<Pair<PlaceOfInterest, Float>> currentSelectedPlaces =
            new ArrayList<>();
    private final HashMap<String, List<Pair<PlaceOfInterest, Float>>> selectionCache =
            new HashMap<>();
    private Address adAddress;
    private final PlaceAdapter placeAdapter = new PlaceAdapter(this,
            currentSelectedPlaces);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String addressStr = getIntent().getStringExtra(ActivityCommunicationLayout.AD_ADDRESS);
        this.adAddress = AddressFactory.makeAddress(addressStr);

        setContentView(R.layout.activity_place);
        Spinner spinner = (Spinner) findViewById(R.id.spinner_place_activity);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, FIELDS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        RecyclerView recyclerView =
                findViewById(R.id.places_Place_recyclerView);

        recyclerView.setAdapter(placeAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //here we will get the user location from the intents
        //userLocation = new Location(6.635510, 46.781170);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position
            , long id) {
        currentSelectedIndex = position;
        if (position == DEFAULT_POSITION) return;

        String type = FIELDS.get(position);
        //here get the type in the list
        if (selectionCache.containsKey(type)) {
            currentSelectedPlaces.clear();
            currentSelectedPlaces.addAll(selectionCache.get(type));
            this.runOnUiThread(placeAdapter::notifyDataSetChanged);

            //currentSelectedPlaces = selectionCache.get(type);
        }

        CompletableFuture<List<Pair<PlaceOfInterest, Float>>> queriedPlaces =
                placeService.getNearbyPlacesWithDistances(adAddress, type,
                        5);

        queriedPlaces
                .thenAccept(pairs -> {
                    selectionCache.put(FIELDS.get(currentSelectedIndex), pairs);
                    currentSelectedPlaces.clear();
                    currentSelectedPlaces.addAll(pairs);
                    this.runOnUiThread(placeAdapter::notifyDataSetChanged);

                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}