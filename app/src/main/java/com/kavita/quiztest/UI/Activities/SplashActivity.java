package com.kavita.quiztest.UI.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.kavita.quiztest.R;

public class SplashActivity extends AppCompatActivity {

    ImageView logo;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);


        logo = findViewById(R.id.logo);
        tv = findViewById(R.id.tv);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.transistion);
        tv.startAnimation(animation);
        logo.startAnimation(animation);
        final Intent i = new Intent(this,LoginActivity.class);

        Thread timer = new Thread(){
            public void run(){
                try {
                    sleep(3000);

                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    startActivity(i);
                    finish();

                }
            }
        };
        timer.start();
    }
}