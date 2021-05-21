package ch.epfl.sdp.appart;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.local.LocalDatabaseService;
import ch.epfl.sdp.appart.favorites.FavoriteViewModel;
import ch.epfl.sdp.appart.login.LoginService;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.scrolling.card.CardAdapter;
import dagger.hilt.android.AndroidEntryPoint;

import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

@AndroidEntryPoint
public class FavoriteActivity extends ToolbarActivity {

    @Inject
    DatabaseService database;
    @Inject
    LoginService login;
    @Inject
    LocalDatabaseService localdb;
    private RecyclerView recyclerView;
    private FavoriteViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        Toolbar toolbar = findViewById(R.id.Favorites_toolbar);
        setSupportActionBar(toolbar);

        mViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);

        CompletableFuture<Void> initRes = mViewModel.initHome();
        initRes.exceptionally(e -> {
            // when initHome completes exceptionally, the exception message is the string to
            // show to user (see favoriteVM)
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        });
        initRes.thenAccept(res -> {
            recyclerView = findViewById(R.id.recycler_favorites);
            recyclerView.setAdapter(new CardAdapter(this, database, new ArrayList<>()));
            recyclerView.setHasFixedSize(true); //use for performance if card dims does
            // not change
            mViewModel.getFavorites().observe(this, this::updateList);
        });
    }

    /**
     * Update the list of cards.
     *
     * @param ls a list of card.
     */
    private void updateList(List<Card> ls) {
        recyclerView.setAdapter(new CardAdapter(this, database, ls));
    }

}