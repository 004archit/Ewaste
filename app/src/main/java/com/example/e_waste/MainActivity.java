package com.example.e_waste;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {

    TextView txtsologan,txtname;
    RelativeLayout relmain;
    LottieAnimationView lottie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lottie =findViewById(R.id.lottieAnimationView);
        txtsologan=findViewById(R.id.txtsologan);
        txtname=findViewById(R.id.txtname);
        relmain=findViewById(R.id.relmain);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                relmain.setVisibility(View.VISIBLE);
            }
        },600);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent =new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);
    }
}