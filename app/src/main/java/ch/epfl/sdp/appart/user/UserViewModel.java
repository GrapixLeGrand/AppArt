package ch.epfl.sdp.appart.user;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;
import ch.epfl.sdp.appart.login.LoginService;
import dagger.hilt.android.lifecycle.HiltViewModel;


@HiltViewModel
public class UserViewModel extends ViewModel {

    private final MutableLiveData<Boolean> mPutUserConfirmed = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mUpdateUserConfirmed = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mUpdateImageConfirmed = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mDeleteImageConfirmed = new MutableLiveData<>();
    private final MutableLiveData<User> mUser = new MutableLiveData<>();

    private Uri profileImageUri;

    final DatabaseService db;
    final LoginService ls;

    @Inject
    public UserViewModel(DatabaseService database, LoginService loginService) {

        this.db = database;
        this.ls = loginService;
    }

    // TODO is this needed?
    /**
     * Puts the user in the database and updates the LiveData
     *
     * @param user the user to store in database
     */
    public void putUser(User user) {
        CompletableFuture<Boolean> putUser = db.putUser(user);
        putUser.exceptionally(e -> {
            Log.d("PUT USER", "DATABASE FAIL");
            return null;
        });
        putUser.thenAccept(mPutUserConfirmed::setValue);
    }

    /**
     * Update the user in the database and updates the LiveData
     *
     * @param user the user to update in database
     */
    public void updateUser(User user) {
        // TODO show toast if update failed
        CompletableFuture<Boolean> updateUser = db.updateUser(user);
        updateUser.exceptionally(e -> {
            Log.d("UPDATE USER", "DATABASE FAIL");
            return null;
        });
        // TODO save locally if update completed
        updateUser.thenAccept(mUpdateUserConfirmed::setValue);
    }

    /**
     * Update the user image the database and updates the LiveData
     * the uri for the image is stored in profileImageUri attribute above
     *
     * @param userId the id of the user
     */
    public void updateImage(String userId){
        StringBuilder imagePathAndName = new StringBuilder();
        // TODO use pathbuilder
        imagePathAndName
                .append(FirebaseLayout.USERS_DIRECTORY)
                .append(FirebaseLayout.SEPARATOR)
                .append(userId)
                .append(FirebaseLayout.SEPARATOR)
                .append(FirebaseLayout.PROFILE_IMAGE_NAME)
                .append(System.currentTimeMillis())
                .append(FirebaseLayout.JPEG);

        CompletableFuture<Boolean> updateImage = db.putImage(profileImageUri, imagePathAndName.toString());
        // TODO show toast if update failed
        updateImage.exceptionally(e -> {
            Log.d("UPDATE IMAGE", "DATABASE FAIL");
            return null;
        });
        // TODO save image locally if update completed
        updateImage.thenAccept(mUpdateImageConfirmed::setValue);
    }

    /**
     * Deletes the user image the database and updates the LiveData
     * !! USE - user.getProfileImage() when calling this method
     *
     * @param profilePicture this is the complete path for the user's image: user.getProfileImage()
     */
    public void deleteImage(String profilePicture){
        // TODO show toast if failed
        CompletableFuture<Boolean> deleteImage = db.deleteImage(profilePicture);
        deleteImage.exceptionally(e -> {
            Log.d("DELETE IMAGE", "DATABASE FAIL");
            return null;
        });
        // TODO update localdb if completed
        deleteImage.thenAccept(mDeleteImageConfirmed::setValue);
    }

    /**
     * Get the user from the database and updates the LiveData
     *
     * @param userId the unique Id of the user to retrieve from database
     */
    public void getUser(String userId) {
        // TODO get user locally, then try to fetch from db
        CompletableFuture<User> getUser = db.getUser(userId);
        getUser.exceptionally(e -> {
            Log.d("GET USER", "DATABASE FAIL");
            return null;
        });
        getUser.thenAccept(mUser::setValue);
    }

    /**
     * Get the current user from the database and updates the LiveData
     */
    public void getCurrentUser() {
        // TODO get locally, then try to fetch from db
        CompletableFuture<User> getCurrentUser = db.getUser(ls.getCurrentUser().getUserId());
        getCurrentUser.exceptionally(e -> {
            Log.d("GET USER", "DATABASE FAIL");
            return null;
        });
        getCurrentUser.thenAccept(mUser::setValue);
    }

    /*
     * Getters for MutableLiveData instances declared above
     */
    public MutableLiveData<Boolean> getPutUserConfirmed() {
        return mPutUserConfirmed;
    }

    public MutableLiveData<Boolean> getUpdateUserConfirmed() {
        return mUpdateUserConfirmed;
    }

    public MutableLiveData<Boolean> getUpdateImageConfirmed() {
        return mUpdateImageConfirmed;
    }

    public MutableLiveData<Boolean> getDeleteImageConfirmed() {
        return mDeleteImageConfirmed;
    }

    public MutableLiveData<User> getUser() {
        return mUser;
    }

    /*
     * Setters
     */
    public void setUri(Uri uri) {
        profileImageUri = uri;
    }

    /*
     * Getters
     */
    public Uri getUri() {
        return profileImageUri;
    }
    

}
