package ch.epfl.sdp.appart;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import javax.inject.Inject;

import ch.epfl.sdp.appart.ad.ResizableImageView;
import ch.epfl.sdp.appart.database.DatabaseService;
import ch.epfl.sdp.appart.glide.visitor.GlideImageViewLoader;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FullScreenImageActivity extends AppCompatActivity {

    @Inject
    DatabaseService db;

    @Override
    protected void onCreate(Bundle savedInstances) {
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_fullscreen_image);
        ResizableImageView photo = findViewById(R.id.image_FullScreenImage_imageView);
        String fullRef = getIntent().getStringExtra("imageId");
        db.accept(new GlideImageViewLoader(this, photo, fullRef));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}

