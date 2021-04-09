package ch.epfl.sdp.appart.user;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.DatabaseService;
import dagger.hilt.android.lifecycle.HiltViewModel;


@HiltViewModel
public class UserViewModel extends ViewModel {

    private final MutableLiveData<Boolean> mPutCardConfirmed = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mUpdateCardConfirmed = new MutableLiveData<>();
    private final MutableLiveData<User> mUser = new MutableLiveData<>();

    final DatabaseService db;

    @Inject
    public UserViewModel(DatabaseService database) {
        this.db = database;
    }

    /**
     * Puts the user in the database and updates the LiveData
     *
     * @param user the user to store in database
     */
    public void putUser(User user) {
        CompletableFuture<Boolean> putUser = db.putUser(user);
        putUser.thenAccept(mPutCardConfirmed::setValue);
    }

    /**
     * Update the user in the database and updates the LiveData
     *
     * @param user the user to update in database
     */
    public void updateUser(User user) {
        CompletableFuture<Boolean> updateUser = db.updateUser(user);
        updateUser.thenAccept(mUpdateCardConfirmed::setValue);
    }

    /**
     * Get the user from the database and updates the LiveData
     *
     * @param userId the unique Id of the user to retrieve from database
     */
    public void getUser(String userId) {
        CompletableFuture<User> getUser = db.getUser(userId);
        getUser.thenAccept(mUser::setValue);
    }

    /*
     * Getters for MutableLiveData instances declared above
     */
    public MutableLiveData<Boolean> getPutCardConfirmed() {
        return mPutCardConfirmed;
    }

    public MutableLiveData<Boolean> getUpdateCardConfirmed() {
        return mUpdateCardConfirmed;
    }

    public MutableLiveData<User> getUser() {
        return mUser;
    }

}
