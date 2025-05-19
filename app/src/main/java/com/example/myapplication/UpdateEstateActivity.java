package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UpdateEstateActivity extends AppCompatActivity {

    private EditText addressET, baseAreaET, descriptionET, phoneNumberET, priceET, roomsET;
    private ImageView imageView;
    private Button pickImageBtn, updateBtn;
    private String estateId;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Uri imageUri = null;
    private String imageUrl = null; // Ez lesz elmentve Firestore-ba

    private static final int PICK_IMAGE_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_estate);

        addressET = findViewById(R.id.et_address);
        baseAreaET = findViewById(R.id.et_basearea);
        descriptionET = findViewById(R.id.et_description);
        phoneNumberET = findViewById(R.id.et_phonenumber);
        priceET = findViewById(R.id.et_price);
        roomsET = findViewById(R.id.et_rooms);
        imageView = findViewById(R.id.iv_image);
        pickImageBtn = findViewById(R.id.btn_pick_image);
        updateBtn = findViewById(R.id.btn_update);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        estateId = getIntent().getStringExtra("estateId");
        if (estateId != null) {
            loadEstateData(estateId);
        }

        pickImageBtn.setOnClickListener(v -> pickImage());

        updateBtn.setOnClickListener(v -> {
            if (imageUri != null) {
                // Előbb töltsük fel a képet a Storage-ba, utána frissítsük az ingatlant
                uploadImageAndUpdateEstate();
            } else {
                // Ha nem választott új képet, csak a többi adatot frissítsük
                updateEstate();
            }
        });
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void loadEstateData(String id) {
        db.collection("Items").document(id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        addressET.setText(documentSnapshot.getString("address"));
                        baseAreaET.setText(documentSnapshot.getString("baseArea"));
                        descriptionET.setText(documentSnapshot.getString("description"));
                        phoneNumberET.setText(documentSnapshot.getString("phoneNumber"));
                        priceET.setText(documentSnapshot.getString("price"));
                        roomsET.setText(documentSnapshot.getString("rooms"));

                        // Kép betöltése Glide-dal
                        imageUrl = documentSnapshot.getString("imageUrl");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.baseline_house_24)
                                    .into(imageView);
                        } else {
                            imageView.setImageResource(R.drawable.baseline_house_24);
                        }
                    }
                });
    }

    private void uploadImageAndUpdateEstate() {
        StorageReference storageRef = storage.getReference().child("estate_images/" + System.currentTimeMillis() + ".jpg");
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    imageUrl = uri.toString();
                    updateEstate();
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Képfeltöltés hiba: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateEstate() {
        String address = addressET.getText().toString();
        String baseArea = baseAreaET.getText().toString();
        String description = descriptionET.getText().toString();
        String phoneNumber = phoneNumberET.getText().toString();
        String price = priceET.getText().toString();
        String rooms = roomsET.getText().toString();

        DocumentReference estateRef = db.collection("Items").document(estateId);
        estateRef.update(
                "address", address,
                "baseArea", baseArea,
                "description", description,
                "phoneNumber", phoneNumber,
                "price", price,
                "rooms", rooms,
                "imageUrl", imageUrl // CSERÉLD le ezzel!
        ).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Sikeres módosítás!", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Hiba: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }
}