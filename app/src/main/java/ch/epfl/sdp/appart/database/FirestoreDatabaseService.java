package ch.epfl.sdp.appart.database;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.ad.ContactInfo;
import ch.epfl.sdp.appart.database.exceptions.DatabaseServiceException;
import ch.epfl.sdp.appart.database.firebaselayout.AdLayout;
import ch.epfl.sdp.appart.database.firebaselayout.CardLayout;
import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;
import ch.epfl.sdp.appart.database.firestoreservicehelpers.FirestoreAdHelper;
import ch.epfl.sdp.appart.database.firestoreservicehelpers.FirestoreCardHelper;
import ch.epfl.sdp.appart.database.firestoreservicehelpers.FirestoreImageHelper;
import ch.epfl.sdp.appart.database.firestoreservicehelpers.FirestoreUserHelper;
import ch.epfl.sdp.appart.glide.visitor.GlideBitmapLoaderVisitor;
import ch.epfl.sdp.appart.glide.visitor.GlideLoaderListenerVisitor;
import ch.epfl.sdp.appart.glide.visitor.GlideLoaderVisitor;
import ch.epfl.sdp.appart.ad.PricePeriod;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.User;
import ch.epfl.sdp.appart.utils.serializers.AdSerializer;
import ch.epfl.sdp.appart.utils.serializers.CardSerializer;
import ch.epfl.sdp.appart.utils.serializers.UserSerializer;

/**
 * Implementation of the DatabaseService with Firestore from Firebase.
 */
@Singleton
public class FirestoreDatabaseService implements DatabaseService {

    private final static String STORAGE_URL = "gs://appart-ec344.appspot.com/";
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;
    private final FirestoreAdHelper adHelper;
    private final FirestoreImageHelper imageHelper;
    private final FirestoreUserHelper userHelper;
    private final FirestoreCardHelper cardHelper;

    @Inject
    public FirestoreDatabaseService() {
        db = FirebaseFirestore.getInstance();
        /*
            Do we use this or not ?
            If we do, this "overrides" or local DB system. However, we know that the data will
            have the same version that our local db. So maybe, we can think of the local db
            as a backup for data that can't be cached by firestore.

            db.clearPersistence();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false).build();
            db.setFirestoreSettings(settings);
        */
        storage = FirebaseStorage.getInstance();
        adHelper = new FirestoreAdHelper();
        imageHelper = new FirestoreImageHelper();
        userHelper = new FirestoreUserHelper();
        cardHelper = new FirestoreCardHelper();
    }

    @NotNull
    @Override
    @NonNull
    public CompletableFuture<List<Card>> getCards() {
        return cardHelper.getCards();
    }

    @NotNull
    @Override
    @NonNull
    public CompletableFuture<List<Card>> getCardsFilter(String location) {
        return cardHelper.getCardsFilter(location);
    }
    @NotNull
    @Override
    @NonNull
    public CompletableFuture<List<Card>> getCardsFilterPrice(int min, int max) {
        return cardHelper.getCardsFilterPrice(min, max);
    }
    @NotNull
    @Override
    @NonNull
    public CompletableFuture<List<Card>> getCardsById(List<String> ids) {
        return cardHelper.getCardsById(ids);
    }


    @NotNull
    @Override
    @NonNull
    public CompletableFuture<Boolean> updateCard(@NotNull @NonNull Card card) {
        return cardHelper.updateCard(card);
    }

    @NotNull
    @Override
    @NonNull
    public CompletableFuture<User> getUser(@NonNull String userId) {
        return userHelper.getUser(userId);
    }

    @NotNull
    @Override
    @NonNull
    public CompletableFuture<Boolean> putUser(@NonNull User user) {
        return userHelper.putUser(user);
    }

    @NotNull
    @Override
    @NonNull
    public CompletableFuture<Boolean> updateUser(@NonNull User user) {
        return userHelper.updateUser(user);
    }

    @NotNull
    @Override
    @NonNull
    public CompletableFuture<Ad> getAd(String adId) {
        return adHelper.getAd(adId);
    }

    @NotNull
    @Override
    @NonNull
    public CompletableFuture<String> putAd(Ad ad, List<Uri> picturesUris, List<Uri> panoramasUris) {
        return adHelper.putAd(ad, picturesUris, panoramasUris);
    }

    @NotNull
    @NonNull
    @Override
    public CompletableFuture<Boolean> deleteAd(String adId, String cardId) {
        return adHelper.deleteAd(adId, cardId);
    }

    @NotNull
    @Override
    @NonNull
    public CompletableFuture<Boolean> putImage(Uri uri, String imagePathAndName) {
        return imageHelper.putImage(uri, imagePathAndName);
    }

    @NonNull
    @Override
    @NotNull
    public CompletableFuture<Boolean> deleteImage(String imagePathAndName) {
        return imageHelper.deleteImage(imagePathAndName);
    }

    @Override
    public CompletableFuture<Void> clearCache() {
        CompletableFuture<Void> futureClear = new CompletableFuture<>();
        db.clearPersistence().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                futureClear.complete(null);
            } else {
                futureClear.completeExceptionally(new DatabaseServiceException(task.getException().getMessage()));
            }
        });
        return futureClear;
    }

    @Override
    public void accept(GlideLoaderVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(GlideBitmapLoaderVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(GlideLoaderListenerVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Returns the storage reference of a stored firebase object
     *
     * @param storageUrl the url in the storage like Cards/img.jpeg would return an image from
     *                   the the
     *                   Cards folder named img.jpeg
     * @return the StorageReference of the object.
     */
    public StorageReference getStorageReference(String storageUrl) {
        return storage.getReferenceFromUrl(STORAGE_URL + storageUrl);
    }

    /**
     * Utility function to clean up storage database
     *
     * @param ref reference to the folder/file to delete
     */
    public void removeFromStorage(StorageReference ref) {
        ref.delete();
    }

    /**
     * Sets up the use of an emulator for the Firebase authentication service.
     *
     * @param ip   the ip of the emulator
     * @param port the port that corresponds to the authentication service emulation
     */
    public void useEmulator(String ip, int port) {
        if (ip == null) throw new IllegalArgumentException();
        db.useEmulator(ip, port);
    }

}