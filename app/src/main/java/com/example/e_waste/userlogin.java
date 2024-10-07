package com.example.e_waste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class userlogin extends AppCompatActivity {

    Button loginuser;
    EditText username,useremail,userpassword,useraddress,usermobile;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    FirebaseAuth mauth;
    FirebaseUser muser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlogin);
        useremail=findViewById(R.id.userloginid);
        userpassword=findViewById(R.id.userpassword);
        progressDialog=new ProgressDialog(this);
        mauth= FirebaseAuth.getInstance();
        muser=mauth.getCurrentUser();
        loginuser=findViewById(R.id.userloginbtn);
        loginuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                performlogin();
            }
        });
    }

    private void performlogin() {
        String email=useremail.getText().toString();
        String password=userpassword.getText().toString();

        if(!email.matches(emailPattern)){
            useremail.setError("Enter Correct Email");
        }
        else if(password.isEmpty()||password.length()<6){
            userpassword.setError("Enter correct password");
        }
        else{
            progressDialog.setMessage("Logging you in....");
            progressDialog.setTitle("login!");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            mauth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        sendusertonextintent();
                        Toast.makeText(userlogin.this, "Login successfull", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        progressDialog.dismiss();;
                        Toast.makeText(userlogin.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private void sendusertonextintent() {
        Intent intent=new Intent(this,userdashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}