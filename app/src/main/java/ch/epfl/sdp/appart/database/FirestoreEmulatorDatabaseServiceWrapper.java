package ch.epfl.sdp.appart.database;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import ch.epfl.sdp.appart.ad.Ad;
import ch.epfl.sdp.appart.glide.visitor.GlideBitmapLoaderVisitor;
import ch.epfl.sdp.appart.glide.visitor.GlideLoaderListenerVisitor;
import ch.epfl.sdp.appart.glide.visitor.GlideLoaderVisitor;
import ch.epfl.sdp.appart.scrolling.card.Card;
import ch.epfl.sdp.appart.user.User;

public class FirestoreEmulatorDatabaseServiceWrapper implements DatabaseService {

    private final FirestoreDatabaseService db;
    private static final String LOCALHOST = "10.0.2.2";
    private static final int FIRESTORE_SERVICE_PORT = 8080;

    public FirestoreEmulatorDatabaseServiceWrapper(@NonNull FirestoreDatabaseService databaseService) {
        if (databaseService == null) {
            throw new IllegalArgumentException();
        }

        this.db = databaseService;
        this.db.useEmulator(LOCALHOST, FIRESTORE_SERVICE_PORT);
    }

    @NotNull
    @NonNull
    @Override
    public CompletableFuture<List<Card>> getCards() {
        return this.db.getCards();
    }

    @NonNull
    @Override
    public CompletableFuture<List<Card>> getCardsFilter(@NonNull String location) {
       return this.db.getCardsFilter(location);
    }

    @NonNull
    @Override
    public CompletableFuture<List<Card>> getCardsFilterPrice(int min, int max) {
        return this.db.getCardsFilterPrice(min, max);
    }

    @NonNull
    @Override
    public CompletableFuture<List<Card>> getCardsById(@NonNull List<String> ids) {
        return this.db.getCardsById(ids);
    }

    @NotNull
    @NonNull
    @Override
    public CompletableFuture<Boolean> updateCard(@NotNull @NonNull Card card) {
        return db.updateCard(card);
    }

    @NotNull
    @NonNull
    @Override
    public CompletableFuture<User> getUser(String userId) {
        return db.getUser(userId);
    }

    @NotNull
    @NonNull
    @Override
    public CompletableFuture<Boolean> putUser(User user) {
        return db.putUser(user);
    }

    @NotNull
    @NonNull
    @Override
    public CompletableFuture<Boolean> updateUser(User user) {
        return db.updateUser(user);
    }

    @NotNull
    @NonNull
    @Override
    public CompletableFuture<Ad> getAd(String id) {
        return db.getAd(id);
    }

    @NotNull
    @NonNull
    @Override
    public CompletableFuture<String> putAd(Ad ad, List<Uri> picturesUris, List<Uri> panoramasUris) {
        return db.putAd(ad, picturesUris, panoramasUris);
    }

    @NonNull
    @Override
    public CompletableFuture<Boolean> deleteAd(String adId, String cardId) {
        return db.deleteAd(adId, cardId);
    }

    @NonNull
    @Override
    public CompletableFuture<Boolean> putImage(Uri uri, String imagePathAndName) {
        return db.putImage(uri, imagePathAndName);
    }

    @NonNull
    @Override
    public CompletableFuture<Boolean> deleteImage(String imagePathAndName) {
        return db.deleteImage(imagePathAndName);
    }

    @Override
    public CompletableFuture<Void> clearCache() {
        return db.clearCache();
    }

    public void removeFromStorage(StorageReference ref) {
        db.removeFromStorage(ref);
    }

    public StorageReference getStorageReference(String path){
        return db.getStorageReference(path);
    }

    @Override
    public void accept(GlideLoaderVisitor visitor) {
        db.accept(visitor);
    }

    @Override
    public void accept(GlideBitmapLoaderVisitor visitor) {
        db.accept(visitor);
    }

    @Override
    public void accept(GlideLoaderListenerVisitor visitor) {
        db.accept(visitor);
    }
}
