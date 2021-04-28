package ch.epfl.sdp.appart;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.scrolling.ScrollingViewModel;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.scrolling.card.CardAdapter;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * This class manages the UI for Scrolling into the add.
 */
@AndroidEntryPoint
public class ScrollingActivity extends ToolbarActivity {

    @Inject
    DatabaseService database;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        Toolbar toolbar = findViewById(R.id.login_Scrolling_toolbar);
        setSupportActionBar(toolbar);

        ScrollingViewModel mViewModel = new ViewModelProvider(this).get(ScrollingViewModel.class);

        mViewModel.initHome();

        recyclerView = findViewById(R.id.recycler_Scrolling_recyclerView);
        recyclerView.setAdapter(new CardAdapter(this, database, new ArrayList<>()));
        recyclerView.setHasFixedSize(true); //use for performance if card dims does not change
        mViewModel.getCards().observe(this, this::updateList);

        // init floating action button
        FloatingActionButton fab = findViewById(R.id.newAd_Scrolling_floatingActionButton);
        fab.setOnClickListener((View view) -> onFloatingButtonAction());

        //search bar
        mViewModel.getCardsFilter().observe(this, this::updateList);

        EditText searchText = (EditText) findViewById(R.id.search_bar_Scrolling_EditText);

        searchText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                mViewModel.filter(
                    ((EditText) findViewById(R.id.search_bar_Scrolling_EditText)).getText().toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

    }

    /**
     * Initialize the contents of the Activity's standard options menu.
     *
     * @param menu The options menu in which you place your items.
     * @return boolean return true for the menu to be displayed
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions_toolbar, menu);
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("Scroll", "stopping");
    }

    /**
     * Update the list of cards.
     *
     * @param ls a list of card.
     */
    private void updateList(List<Card> ls) {
        recyclerView.setAdapter(new CardAdapter(this, database, ls));
    }

    /**
     * Opens the Ad creation activity.
     */
    private void onFloatingButtonAction() {
        Intent intent = new Intent(this, AdCreationActivity.class);
        startActivity(intent);
    }

}