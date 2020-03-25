package com.example.projetimagemobile.view;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.projetimagemobile.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class MainActivity extends AppCompatActivity {

    private static int ALL_PERMISSIONS = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_LOAD_IMAGE = 2;
    private String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private String filePath = "no path";
    private Uri photoURI = null;

    private ImageView imageView;
    private Button searchButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        searchButton = findViewById(R.id.search);

        hasPermissions(this, permissions);
        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, ALL_PERMISSIONS);
        }
    }

    public void takePictureIntent(View view) {
        if (!checkCameraPermission()){
            Toast.makeText(getApplicationContext(),
            "Vous devez accepter l'autorisation d'accès à la caméra pour utiliser cette fonctionnalité", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
        } else {
            try {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = createImageFile();
                if (photoFile != null) {
                    //Log.d("LOG_TAG", "Photofile not null");
                    photoURI = FileProvider.getUriForFile(this,
                            "com.example.projetimagemobile.fileprovider",
                            photoFile);

                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            } catch (IOException ex) {
                Log.d("LOG_TAG", "Exception while creating file: " + ex.toString());
            }
        }

    }

    public void libraryPictureIntent(View view) {
        if(!checkStoragePermission()){
            Toast.makeText(getApplicationContext(),
            "Vous devez accepter l'autorisation d'accès à la galerie pour utiliser cette fonctionnalité", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_LOAD_IMAGE);
        } else {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, REQUEST_LOAD_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case REQUEST_IMAGE_CAPTURE :
                    try {
                        final InputStream imageStream = getContentResolver().openInputStream(photoURI);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        filePath = photoURI.getPath();

                        Matrix matrix = new Matrix();
                        matrix.postRotate(90.0f);
                        Bitmap toTranform =
                                Bitmap.createBitmap(selectedImage, 0, 0, selectedImage.getWidth(), selectedImage.getHeight(), matrix, true);
                        Glide.with(this).load(toTranform).into(imageView);
                        searchButton.setEnabled(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case REQUEST_LOAD_IMAGE :
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        filePath = getRealPathFromURI(imageUri);
                        Glide.with(this).load(selectedImage).into(imageView);
                        searchButton.setEnabled(true);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Une erreur est survenue", Toast.LENGTH_LONG).show();
                    }
                    break;
                default:
                    break;
            }
        } else {
            Toast.makeText(this, "Vous n'avez pas choisi d'image", Toast.LENGTH_SHORT).show();
        }
    }

    public void search(View view) {
        if (filePath != "no path") {
            Intent i = new Intent(this, ResultsViewActivity.class);
            i.putExtra("filePath", filePath);
            startActivity(i);
        } else {
            searchButton.setEnabled(false);
            Toast.makeText(this, "Veuillez sélectionner une image avant d'effectuer une recherche", Toast.LENGTH_SHORT).show();
        }

    }



    // -------------- UTILS PRIVATE FUNCTIONS ----------------- //

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";

        // Create folders
        File clothesDir = new File(Environment.getExternalStorageDirectory(), "Clothes Detector");
        File pictureDir = new File(Environment.getExternalStorageDirectory(), "Clothes Detector/savedPictures");

        if(!clothesDir.exists()) {
            boolean isClothesDirCreated = clothesDir.mkdirs();
            Log.d("LOG_TAG", "Create Clothes Detector Directory : "+isClothesDirCreated);
            if (!pictureDir.exists()) {
                boolean isPictureDirCreated = pictureDir.mkdirs();
                Log.d("LOG_TAG", "Create savedPictures Directory : "+isPictureDirCreated);
            }
        } else {
            if (!pictureDir.exists()) {
                boolean isPictureDirCreated = pictureDir.mkdirs();
                Log.d("LOG_TAG", "Create savedPictures Directory : "+isPictureDirCreated);
            }
        }

        // Create an image to save
        File image = new File(pictureDir, imageFileName);
        return image;
    }
    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    private boolean checkCameraPermission()
    {
        String permission = "android.permission.CAMERA";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
    private boolean checkStoragePermission()
    {
        String permission = "android.permission.READ_EXTERNAL_STORAGE";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

}

