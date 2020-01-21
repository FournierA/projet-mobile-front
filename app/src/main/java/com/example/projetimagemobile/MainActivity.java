package com.example.projetimagemobile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static int RESULT_LOAD_IMAGE = 2;
    private ImageView imageView;
    private Button capture, library;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        capture = findViewById(R.id.capture);
        library = findViewById(R.id.library);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, 1);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);

    }

    public void libraryPictureIntent(View view) {
        if(!checkStoragePermission()){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        } else {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
        }
    }

    public void dispatchTakePictureIntent(View view) {
        if (!checkCameraPermission()){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    capture.setEnabled(true);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Toast.makeText(getApplicationContext(),
                    "Vous devez accepter l'autorisation d'accès à la caméra pour utiliser cette fonctionnalité", Toast.LENGTH_SHORT).show();

                }
                return;
            }
            case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    library.setEnabled(true);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Toast.makeText(getApplicationContext(),
                    "Vous devez accepter l'autorisation d'accès à la galerie pour utiliser cette fonctionnalité", Toast.LENGTH_SHORT).show();

                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public boolean checkCameraPermission()
    {
        String permission = "android.permission.CAMERA";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public boolean checkStoragePermission()
    {
        String permission = "android.permission.READ_EXTERNAL_STORAGE";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Une erreur est survenue", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "Vous n'avez pas choisi une Image",Toast.LENGTH_LONG).show();
        }

    }

}

