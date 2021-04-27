package ch.epfl.sdp.appart;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import javax.inject.Inject;

import ch.epfl.sdp.appart.login.LoginService;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * Abstract class representing an activity that should have a toolbar.
 * This gives the default menu for the toolbar.
 * For now, the two main options of the toolbar are the logout button and the account button.
 */
@AndroidEntryPoint
public abstract class ToolbarActivity extends AppCompatActivity {


    @Inject
    LoginService loginService;

    /**
     * Sets the toolbar as the main menu
     *
     * @param menu the menu that was just created
     * @return true if no exception was thrown
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions_toolbar, menu);
        return true;
    }

    /**
     * Method called when an item of the overflow button of the toolbar is pushed.
     * Tells the app what to do when a given button is pushed
     *
     * @param item the option that was pressed in the overflow menu of the toolbar
     * @return true iff the method was able to find what to do when item was pushed
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            loginService.signOut();
            Intent intentLogout = new Intent(this, LoginActivity.class);
            startActivity(intentLogout);
            return true;
        }
        if (item.getItemId() == R.id.action_account) {
            Intent intentAccount = new Intent(this, UserProfileActivity.class);
            startActivity(intentAccount);
            return true;
        }
        if (item.getItemId() == R.id.action_settings) {
            return true;
        }
        if (item.getItemId() == R.id.action_favorite) {
            Intent intentFavorite = new Intent(this, FavoriteActivity.class);
            startActivity(intentFavorite);
            return true;
        }
        // If we got here, the user's action was not recognized.
        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }

}