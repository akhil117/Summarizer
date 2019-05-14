package com.example.akhilbatchu.summarizer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    private TextView tv;
    private ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tv = (TextView)findViewById(R.id.tv);
        iv = (ImageView)findViewById(R.id.iv);
        Animation myanim = AnimationUtils.loadAnimation(this,R.anim.mytransition);
        tv.startAnimation(myanim);
        iv.startAnimation(myanim);
        final Intent intent = new Intent(this,MainActivity.class);
        Thread timer = new Thread()
        {
            @Override
            public void run() {
                try
                {
                    sleep(5000);
                }catch (Exception e)
                {
                        e.printStackTrace();
                }
                finally {
                    startActivity(intent);
                    finish();
                }
            }
        };

        timer.start();

    }
}
