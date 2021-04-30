package ch.epfl.sdp.appart.database.firestoreservicehelpers;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import ch.epfl.sdp.appart.database.firebaselayout.FirebaseLayout;

public class FirestoreImageHelper {

    private final FirebaseStorage storage;

    public FirestoreImageHelper() {
        storage = FirebaseStorage.getInstance();
    }

    @NotNull
    @NonNull
    public CompletableFuture<Boolean> putImage(Uri uri, String imagePathAndName) {
        if (uri == null || imagePathAndName == null) {
            throw new IllegalArgumentException("parameters cannot be null");
        }
        CompletableFuture<Boolean> isFinishedFuture = new CompletableFuture<>();
        StorageReference fileReference = storage.getReference().child(imagePathAndName);
        fileReference.putFile(uri).addOnCompleteListener(
                task -> isFinishedFuture.complete(task.isSuccessful()));
        return isFinishedFuture;
    }

    /**
     *  deletes an image from the Firebase Storage
     * @param imagePathAndName this the complete path of the image (e.g. users/default/photo.jpeg)
     */
    @NotNull
    @NonNull
    public CompletableFuture<Boolean> deleteImage(String imagePathAndName) {
        if (imagePathAndName == null) {
            throw new IllegalArgumentException("deleteImage: parameters cannot be null");
        }

        CompletableFuture<Boolean> isFinishedFuture = new CompletableFuture<>();

         /* The reason for this check is when a user has DEFAULT user icon (thus no image in storage)
            The activity asks to delete previous profile image, which is not present, thus returns true directly */
        if (imagePathAndName.contains("users/default/")) {
            isFinishedFuture.complete(true);
            return isFinishedFuture;
        }

        StorageReference storeRef = storage.getReference();
        StorageReference desertRef = storeRef.child(imagePathAndName);

        desertRef.delete().addOnCompleteListener(
                task -> isFinishedFuture.complete(task.isSuccessful()));
        return isFinishedFuture;
    }
}