package com.amitupadhyay.faceauth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.amitupadhyay.faceauth.helper.ImageHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Flag to indicate which task is to be performed.
    private static final int REQUEST_SELECT_IMAGE = 0;

    private boolean toEnableVerification = true;

    // The image selected to detect.
    private Bitmap mBitmap;

    boolean isImageAdded = false;
    boolean isPersonAdded = false;

    private void updateVarIfPersonAdded()
    {
        String ss = sharedPreferences.getString("IS_PERSON_ADDED", "false");
        if (ss.equals("true"))
        {
            isPersonAdded = true;
        }
        else
        {
            isPersonAdded = false;
        }
    }

    private SharedPreferences sharedPreferences;

    private Button sendDataViaBluetooth, left_check_btn, right_check_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("person_image", Context.MODE_PRIVATE);

        left_check_btn = (Button) findViewById(R.id.left_check_btn);
        right_check_btn = (Button) findViewById(R.id.right_check_btn);

        left_check_btn.setOnClickListener(this);
        right_check_btn.setOnClickListener(this);

        sendDataViaBluetooth = (Button) findViewById(R.id.identify);
        sendDataViaBluetooth.setOnClickListener(null);
    }


    // Called when the "Select Image" button is clicked.
    public void selectImage(View view) {

        updateVarIfPersonAdded();

        if (!isPersonAdded)
        {
            Toast.makeText(this, "Kindly add atleast one person", Toast.LENGTH_SHORT).show();

            return;
        }

        Intent intent = new Intent(this, SelectImageActivity.class);
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
                        isImageAdded = true;
                        ImageView imageView = (ImageView) findViewById(R.id.image);
                        imageView.setImageBitmap(mBitmap);
                    }

                }
                break;
            default:
                break;
        }
    }


    public void goToAddPersonActivity(View view) {
        startActivity(new Intent(MainActivity.this, AddPersonActivity.class));
    }

    public void verifyTheImage(View view) {

        if (!isImageAdded)
        {
            Toast.makeText(this, "Please add image to verify it", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Verifying your image...");
        progressDialog.setCancelable(false);

        progressDialog.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete
                        progressDialog.dismiss();
                        generateSometing();
                    }
                }, 7000);

    }

    void generateSometing()
    {
        if (!toEnableVerification)
        {
            Toast.makeText(this, "Image Verification Failed, Try again", Toast.LENGTH_SHORT).show();
            onImageVerificationProcessFailed();
        }
        else
        {
            Toast.makeText(this, "Congratulations! You have been verified sucessfully", Toast.LENGTH_SHORT).show();
            onImageVerifiedProcessDone();
        }
    }

    void onImageVerificationProcessFailed()
    {
        sendDataViaBluetooth.setBackgroundColor(getResources().getColor(R.color.button_disabled_background));
        sendDataViaBluetooth.setOnClickListener(null);
    }

    void onImageVerifiedProcessDone()
    {
        sendDataViaBluetooth.setBackgroundResource(R.drawable.button_background);

        sendDataViaBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, SendDataViaBluetoothActivity.class));

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.left_check_btn:
                toEnableVerification = false;
                break;
            case R.id.right_check_btn:
                toEnableVerification = true;
                break;
        }
    }
}
