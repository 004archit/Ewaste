package com.example.e_waste;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class userdashboard extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private ImageCapture imageCapture;
    private Uri imageUri;
    private StorageReference storageReference;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userdashboard);

        Button captureButton = findViewById(R.id.captureButton);
        Button uploadButton = findViewById(R.id.uploadButton);
        TextView motivaion=findViewById(R.id.username);
        motivaion.setText("Dear user\nJoin us in the mission to create a cleaner, greener planet! Your old gadgets may seem like clutter, but they hold the potential for transformation.By simply uploading a picture of your e-waste, you’re taking a vital step towards responsible recycling and environmental stewardship. Every device you share brings us closer to reducing waste and promoting sustainability.Let’s turn your tech trash into a valuable resource for a brighter future—together, we can make a difference!");
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        captureButton.setOnClickListener(v -> {captureImage();
        captureButton.setVisibility(View.GONE);});
        uploadButton.setOnClickListener(v -> uploadImage());
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Setting up the Preview
                Preview preview = new Preview.Builder().build();

                // Get the surface provider from PreviewView (assuming you have a PreviewView in your layout)
                PreviewView previewView = findViewById(R.id.previewView);  // Ensure this matches your layout
                preview.setSurfaceProvider(previewView.getSurfaceProvider()); // Correct way to get the surface provider

                // Set up ImageCapture
                imageCapture = new ImageCapture.Builder().build();

                // Choose a camera (back camera in this case)
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                // Bind the lifecycle of cameras to the lifecycle owner
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (ExecutionException | InterruptedException e) {
                Log.e("CameraX", "Error starting camera: " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void captureImage() {
        File file = new File(getExternalFilesDir(null), "photo.jpg");
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(file).build();

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                imageUri = Uri.fromFile(file);
                Toast.makeText(userdashboard.this, "Image saved: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e("CameraX", "Photo capture failed: " + exception.getMessage(), exception);
            }
        });
    }

    private void uploadImage() {
        if (imageUri != null) {

            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");
            mDatabase = FirebaseDatabase.getInstance().getReference();
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(userdashboard.this, "Upload successful", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(userdashboard.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Firebase Storage", "Upload error: " + e.getMessage());
                    });
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String userId = currentUser.getUid();
             mDatabase.child("user").child(userId).child("url").setValue(imageUri.toString());
            sendusertonextintent();
        } else {
            Toast.makeText(this, "No image captured", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    private void sendusertonextintent() {
        Intent intent=new Intent(this,Done.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
