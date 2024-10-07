package com.example.e_waste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class registeration extends AppCompatActivity {
    Button registerbtn;
    EditText username;
    EditText useremail;
    EditText userpassword;
    EditText useraddress;
    EditText usermobile;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    FirebaseAuth mauth;
    FirebaseUser muser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);
        username=findViewById(R.id.name);
        useremail=findViewById(R.id.loginemail);
        useraddress=findViewById(R.id.address);
        usermobile=findViewById(R.id.mobile);
        userpassword=findViewById(R.id.password);
        registerbtn=findViewById(R.id.registerhere);
        progressDialog=new ProgressDialog(this);
        mauth= FirebaseAuth.getInstance();
        muser=mauth.getCurrentUser();
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performauth();
                Intent intent=new Intent(registeration.this, userlogin.class);
                startActivity(intent);
            }
            private void performauth() {
                String email=useremail.getText().toString();
                String password=userpassword.getText().toString();


                if(!email.matches(emailPattern)){
                    useremail.setError("Enter Correct Email");
                }
                else if(password.isEmpty()||password.length()<6){
                    userpassword.setError("Enter correct password");
                }
                else{
                    progressDialog.setMessage("We are Registering you ....");
                    progressDialog.setTitle("Registered!");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    mauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String name = username.getText().toString();
                                String address = useraddress.getText().toString();
                                String mobile = usermobile.getText().toString();
                                progressDialog.dismiss();
                                uploadUserData(name,address,mobile);
                                sendusertonextActivity();
                                Toast.makeText(registeration.this, "Registration successfull", Toast.LENGTH_SHORT).show();
                            }else{
                                progressDialog.dismiss();;
                                Toast.makeText(registeration.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }

    private void uploadUserData(String name, String mobileNo, String address) {
        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("user");

        // Get the current user UID from Firebase Authentication
        String userId =FirebaseAuth.getInstance().getCurrentUser().getUid();  // Unique ID for the user

        // Create a new user with name, mobile, and address
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("mobile", mobileNo);
        user.put("address", address);

        // Save user data to Firebase under the user's UID
        usersRef.child(userId).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Firebase", "User data saved successfully");
                        } else {
                            Log.w("Firebase", "Error saving user data", task.getException());
                        }
                    }
                });
    }


    private void sendusertonextActivity() {
        Intent intent=new Intent(registeration.this,userlogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}