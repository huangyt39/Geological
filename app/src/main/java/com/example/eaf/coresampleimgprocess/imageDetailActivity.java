package com.example.eaf.coresampleimgprocess;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class imageDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        ImageView imageView =findViewById(R.id.imageDetail);
        Intent intent=getIntent();
        if(intent !=null)
        {
            byte [] bis=intent.getByteArrayExtra("bitmap");
            Bitmap bitmap=BitmapFactory.decodeByteArray(bis, 0, bis.length);
            imageView.setImageBitmap(bitmap);
        }
    }
}
