package com.martin.taxi.activity;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.martin.taxi.R;

public class SplashActivity extends BaseActivity {

    private ImageView imageLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        imageLogo = (ImageView) findViewById(R.id.view_logo);
        Drawable background = imageLogo.getDrawable();
        if (background instanceof Animatable){
            Animatable animatable =  ((Animatable) background);
            animatable.start();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,HomeActivity.class));
                finish();
            }
        },3000);
    }
}
