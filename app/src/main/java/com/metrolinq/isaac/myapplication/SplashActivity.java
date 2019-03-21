package com.metrolinq.isaac.myapplication;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView mcs_logo = findViewById(R.id.mcs_logo);
        ImageView line1 = findViewById(R.id.line_center_splash);




        Animation animation = AnimationUtils.loadAnimation(this, R.anim.blink);
        Animation animation_line = AnimationUtils.loadAnimation(this, R.anim.anim_left2right);

        mcs_logo.setAnimation(animation);
        line1.setAnimation(animation_line);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, StartPageActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}
