package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class AddEstateActivity extends AppCompatActivity {

    private EditText addressET, baseAreaET, descriptionET, phoneNumberET, priceET, roomsET;
    private ImageView imageView;
    private Uri imageUri = null;

    private FirebaseFirestore db;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_estate);

        addressET = findViewById(R.id.et_address);
        baseAreaET = findViewById(R.id.et_basearea);
        descriptionET = findViewById(R.id.et_description);
        phoneNumberET = findViewById(R.id.et_phonenumber);
        priceET = findViewById(R.id.et_price);
        roomsET = findViewById(R.id.et_rooms);
        imageView = findViewById(R.id.iv_image);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        findViewById(R.id.btn_pick_image).setOnClickListener(v -> pickImage());
        findViewById(R.id.btn_take_photo).setOnClickListener(v -> takePhoto());
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void takePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
        } else {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraLauncher.launch(intent);
        }
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    imageView.setImageURI(imageUri);
                }
            });

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    imageView.setImageURI(imageUri);
                }
            });

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            takePhoto();
        }
    }

    public void onSaveClicked(View view) {
        Log.d("AddEstateActivity", "imageUri: " + imageUri);
        if (imageUri == null) {
            Toast.makeText(this, "Válassz vagy készíts képet!", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = "estate_images/" + UUID.randomUUID() + ".jpg";
        StorageReference fileRef = storageRef.child(fileName);

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Csak ekkor mentsd Firestore-ba!
                    saveEstate(uri.toString());
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Kép feltöltési hiba: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveEstate(String imageUrl) {
        String address = addressET.getText().toString();
        String baseArea = baseAreaET.getText().toString();
        String description = descriptionET.getText().toString();
        String phoneNumber = phoneNumberET.getText().toString();
        String price = priceET.getText().toString();
        String rooms = roomsET.getText().toString();

        RealEstateItem newItem = new RealEstateItem(address, baseArea, description, price, rooms, phoneNumber, imageUrl);

        db.collection("Items").add(newItem)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Sikeres mentés!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Hiba: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}