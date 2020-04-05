package com.example.projetimagemobile.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.projetimagemobile.R;
import com.google.android.material.snackbar.Snackbar;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    private Button searchButton;
    private CropImageView mCropView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchButton = findViewById(R.id.search);
        mCropView = findViewById(R.id.cropImageView);
        mCropView.setCropMode(CropImageView.CropMode.FREE);
        mCropView.setOutputMaxSize(224, 224);

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
                File photoFile = createImageFile(false);
                if (photoFile != null) {
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
            Snackbar mySnackbar;
            switch (requestCode){
                case REQUEST_IMAGE_CAPTURE :
                    if (photoURI != null) {
                        mCropView.load(photoURI).execute(mLoadCallback);
                        mySnackbar = Snackbar.make(findViewById(R.id.cropImageView), R.string.cropInfo, 2000);
                        mySnackbar.show();
                    } else {
                        Log.d("LOG_TAG", "Photo URI is NULL");
                    }
                    break;
                case REQUEST_LOAD_IMAGE :
                    photoURI = data.getData();
                    mCropView.load(photoURI).execute(mLoadCallback);
                    mySnackbar = Snackbar.make(findViewById(R.id.cropImageView), R.string.cropInfo, 2000);
                    mySnackbar.show();
                    break;
                default:
                    break;
            }
        } else {
            Toast.makeText(this, "Vous n'avez pas choisi d'image", Toast.LENGTH_SHORT).show();
        }
    }

    public void search(View view) {
        // TODO Ajouter une round progress bar pour montrer l'état de chargement
        if (photoURI != null) {
            searchButton.setEnabled(false);
            mCropView.crop(photoURI).execute(mCropCallback);
        } else {
            Toast.makeText(MainActivity.this, "Veuillez sélectionner une image avant d'effectuer une recherche", Toast.LENGTH_SHORT).show();
        }
    }
    public void rotate(View view) {
        if (photoURI != null) {
            mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D); // rotate clockwise
        } else {
            Toast.makeText(this, "Vous n'avez pas choisi d'image", Toast.LENGTH_SHORT).show();
            Log.d("LOG_TAG", "PhotoURI is NULL");
        }
    }

    // -------------------------------------------------------- //
    // --------------- UTILS PRIVATE FUNCTIONS ---------------- //
    // -------------------------------------------------------- //

    private File createImageFile(boolean cropped) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp;
        if (cropped) {
            imageFileName = "CROP_" + imageFileName + timeStamp;
        }
        imageFileName += ".jpg";

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
    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions)
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {return false;}
        }
        return true;
    }
    private boolean checkCameraPermission() {
        String permission = "android.permission.CAMERA";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
    private boolean checkStoragePermission() {
        String permission = "android.permission.READ_EXTERNAL_STORAGE";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    // ------------------------------------------------ //
    // ----------------- CROP CALLBACKS --------------- //
    // ------------------------------------------------ //

    private final LoadCallback mLoadCallback = new LoadCallback() {
        @Override
        public void onSuccess() {}
        @Override
        public void onError(Throwable e) {Log.d("LOG_TAG", "LoadCallBack Error : "+e.getMessage());}
    };
    private final CropCallback mCropCallback = new CropCallback() {
        @Override
        public void onSuccess(Bitmap cropped) {
            try {
                Uri fileUri = Uri.fromFile(createImageFile(true));
                mCropView.save(cropped).execute(fileUri, mSaveCallback);
            } catch (IOException e) {e.printStackTrace();}
        }
        @Override
        public void onError(Throwable e) {Log.d("LOG_TAG", "CropCallBack Error : "+e.getMessage());}
    };
    private final SaveCallback mSaveCallback = new SaveCallback() {
        @Override
        public void onSuccess(final Uri outputUri) {
            filePath = outputUri.getPath();
            if (filePath != "no path") {
                Intent i = new Intent(MainActivity.this, ResultsViewActivity.class);
                Log.d("LOG_TAg", filePath);
                i.putExtra("filePath", filePath);
                startActivity(i);
                searchButton.setEnabled(true);
            } else {
                searchButton.setEnabled(false);
                Toast.makeText(MainActivity.this, "Veuillez sélectionner une image avant d'effectuer une recherche", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onError(Throwable e) {Log.d("LOG_TAG", "SaveCallBack Error : "+e.getMessage());}
    };

}

