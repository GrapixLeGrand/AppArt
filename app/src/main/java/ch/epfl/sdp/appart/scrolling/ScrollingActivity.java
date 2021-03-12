package ch.epfl.sdp.appart.scrolling;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.R;
import ch.epfl.sdp.appart.scrolling.card.CardAdapter;

public class ScrollingActivity extends AppCompatActivity {

    private ScrollingViewModel mViewModel;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        mViewModel = new ViewModelProvider(this).get(ScrollingViewModel.class);

        mViewModel.initHome();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(new CardAdapter(this, new ArrayList<>()));
        recyclerView.setHasFixedSize(true); //use for performance if card dims does not change
        mViewModel.getCards().observe(this, this::updateList);

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("Scroll", "stopping");
    }

    private void updateList(List<Card> ls) {
        recyclerView.setAdapter(new CardAdapter(this, ls));
    }

}