package com.iniesta.quiztest.UI.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.iniesta.quiztest.R;

public class SplashActivity extends AppCompatActivity {

    ImageView logo,logo1;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);


        logo = findViewById(R.id.logo);
        logo1= findViewById(R.id.logo1);
        tv = findViewById(R.id.tv);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Roboto-Regular.ttf");
        tv.setTypeface(custom_font);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.transistion);
        tv.startAnimation(animation);
        logo.startAnimation(animation);
        logo1.startAnimation(animation);

        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        final String email = sharedPreferences.getString("email", "");
        final String name = sharedPreferences.getString("name", "");

        final Intent loginIntent = new Intent(this,LoginActivity.class);
        final Intent mainIntent = new Intent(this,MainActivity.class);

        Thread timer = new Thread(){
            public void run(){
                try {
                    sleep(3000);

                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    if (!email.isEmpty() && !name.isEmpty()){
                        startActivity(mainIntent);
                    }else {
                        startActivity(loginIntent);
                    }
                    finish();

                }
            }
        };
        timer.start();
    }
}