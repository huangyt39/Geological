package com.example.eaf.coresampleimgprocess;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import java.io.IOException;

public class splashActivity extends Activity {

    private ImageView welcomeImg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        welcomeImg = this.findViewById(R.id.cover);
        AlphaAnimation anima = new AlphaAnimation(0,1);
        anima.setDuration(2000);// 设置动画显示时间
        anima.setAnimationListener(new AnimationImpl());
        //第一个入场动画
        welcomeImg.startAnimation(anima);


    }

    private class AnimationImpl implements Animation.AnimationListener
    {

        @Override
        public void onAnimationStart(Animation animation)
        {
            welcomeImg.setBackgroundResource(R.drawable.sysu);
        }

        @Override
        public void onAnimationEnd(Animation animation)
        {
            AlphaAnimation anima2 = new AlphaAnimation(1, 0);
            anima2.setDuration(1000);
            welcomeImg.startAnimation(anima2);
            anima2.setFillAfter(true);
            anima2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }
                //第二个入场动画
                @Override
                public void onAnimationEnd(Animation animation) {
                    skip();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });


        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

    }

    private void skip()
    {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
