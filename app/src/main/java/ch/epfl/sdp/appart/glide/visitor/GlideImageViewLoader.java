package ch.epfl.sdp.appart.glide.visitor;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import ch.epfl.sdp.appart.database.FirestoreDatabaseService;
import ch.epfl.sdp.appart.database.MockDatabaseService;

/**
 * Implementation of the Visitor that helps to select the right reference
 * in function of the bound database.
 * This class is encapsulating the loading with Glide
 */
public final class GlideImageViewLoader extends GlideVisitor implements GlideLoaderVisitor {

    private final ImageView view;
    private final String imageReference;

    public GlideImageViewLoader(Context context, ImageView view, String imageReference) {
        super(context);

        if (view == null) {
            throw new IllegalArgumentException("imageView cannot be null");
        }

        if (imageReference == null) {
            throw new IllegalArgumentException("imageReference cannot be null");
        }

        this.view = view;
        this.imageReference = imageReference;
    }

    @Override
    public void visit(FirestoreDatabaseService database) {
        Glide.with(context)
                .load(database.getStorageReference(imageReference))
                .into(view);
    }

    @Override
    public void visit(MockDatabaseService database) {
        Glide.with(context)
                .load(imageReference)
                .into(view);
    }

}