package com.example.e_waste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Done extends AppCompatActivity {

    ImageButton gmail,youtube,insta,facebook,linkdein;
    TextView owner,owneraddress;
    private DatabaseReference mDatabase;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String userId = currentUser.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        owner=findViewById(R.id.tv_owner_name);
        owneraddress=findViewById(R.id.tv_pickup_address);
        gmail=findViewById(R.id.gmaillogo);
        youtube=findViewById(R.id.youtubelogo);
        insta=findViewById(R.id.instslogo);
        facebook=findViewById(R.id.facebooklogo);
        linkdein=findViewById(R.id.phonelogo);
        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                golink("https://mail.google.com/mail/u/0/#inbox");
            }
        });
        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                golink("https://www.youtube.com/@kunalvermaa");
            }
        });
        insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                golink("https://www.instagram.com/004_arc__hit/");
            }
        });
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                golink("https://m.facebook.com/100005584097759/");
            }
        });
        linkdein.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                golink("https://www.linkedin.com/in/archit-raj-8a2043209");
            }

            // Call retrieveName function to get name from Firebase
        });
        retrieveUserData(userId);
        retrieveImageUrl();
    }
    private void retrieveImageUrl() {
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("user").child(userId);

            userRef.child("url").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String imageUrl = dataSnapshot.getValue(String.class);
                        displayImage(imageUrl);
                    } else {
                        Toast.makeText(Done.this, "No image found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Done.this, "Error retrieving image URL", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayImage(String imageUrl) {
        ImageView imageView = findViewById(R.id.fetched); // Ensure you have an ImageView in your layout
        Glide.with(this)
                .load(imageUrl)
                .into(imageView);
    }
    // Function to retrieve userId, address, and mobile number data
    private void retrieveUserData(String userId) {
        // Retrieve the entire user node for the given userId
        mDatabase.child("user").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve individual fields from the user node
                    String retrievedUserId = dataSnapshot.child("name").getValue(String.class);
                    String address = dataSnapshot.child("address").getValue(String.class);
                    String mobileNo = dataSnapshot.child("mobile").getValue(String.class);
                     owner.setText(retrievedUserId);
                     owneraddress.setText(address+", Contact Number:"+mobileNo);

                    // Log the data for debugging
                    Log.d("FirebaseData", "User ID: " + retrievedUserId);
                    Log.d("FirebaseData", "Address: " + address);
                    Log.d("FirebaseData", "Mobile No: " + mobileNo);
                } else {
                    Log.e("FirebaseData", "User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Failed to retrieve data", databaseError.toException());
            }
        });
    }
    private void golink(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }
}