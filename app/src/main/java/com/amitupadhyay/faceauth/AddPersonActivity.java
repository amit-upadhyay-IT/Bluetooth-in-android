package com.amitupadhyay.faceauth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.amitupadhyay.faceauth.cropwork.BaseActivity;
import com.amitupadhyay.faceauth.cropwork.SampleActivity;
import com.amitupadhyay.faceauth.helper.ImageHelper;

public class AddPersonActivity extends BaseActivity {

    // Flag to indicate which task is to be performed.
    private static final int REQUEST_SELECT_IMAGE = 0;

    // The image selected to detect.
    private Bitmap mBitmap;

    boolean isImageAdded = false;

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);
        sharedPreferences = getSharedPreferences("person_image", Context.MODE_PRIVATE);

    }



    // Called when the "Select Image" button is clicked.
    public void selectImage(View view) {
        Intent intent = new Intent(this, SampleActivity.class);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    // Called when image selection is done.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case REQUEST_SELECT_IMAGE:
                if(resultCode == RESULT_OK) {

                    // If image is selected successfully, set the image URI and bitmap.
                    Uri imageUri = data.getData();
                    mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                            imageUri, getContentResolver());
                    if (mBitmap != null) {
                        // Show the image on screen.
                        ImageView imageView = (ImageView) findViewById(R.id.image);
                        imageView.setImageBitmap(mBitmap);
                        isImageAdded = true;
                        sharedPreferences.edit().putString("IS_PERSON_ADDED",
                                "true").apply();
                    }

                }
                break;
            default:
                break;
        }
    }

    public void savePersonImage(View view) {

        if (!isImageAdded)
        {
            Toast.makeText(this, "Kindly add a image", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Saving Image on Server...");
        progressDialog.setCancelable(false);

        progressDialog.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete
                        progressDialog.dismiss();
                        finish();
                        Toast.makeText(AddPersonActivity.this, "Image Training succeeded", Toast.LENGTH_SHORT).show();

                    }
                }, 6000);
    }
}
