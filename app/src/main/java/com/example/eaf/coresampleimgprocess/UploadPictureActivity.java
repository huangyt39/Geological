package com.example.eaf.coresampleimgprocess;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadPictureActivity extends AppCompatActivity{

    private Uri imageUri = null;
    private String imageUriString = null;

    private ImageView imageView;

    private static final String TAG = "UploadPictureActivity";

    private static final String BASE_URL = "http://10.0.2.2:5000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_picture);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        //get the picture selected
        Intent intent = getIntent();
        imageUriString = intent.getStringExtra("imageUri");
        if(imageUriString!=null) {
            Log.d(TAG, "onCreate: geeeeeeeeee image uri string not null");
            imageUri = Uri.parse(imageUriString);
            imageView = (ImageView) findViewById(R.id.upload_imageview);
            imageView.setImageURI(imageUri);
        }
        String imagePath = intent.getStringExtra("imagePath");
        if(imagePath!=null) {
            imageView = (ImageView) findViewById(R.id.upload_imageview);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);
            imageUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null,null));
            imageUriString=imageUri.toString();
        }
        final FloatingActionButton uploadButton = (FloatingActionButton) findViewById(R.id.upload_button);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                testServer();
//                Toast.makeText(UploadPictureActivity.this, "upload button clicked", Toast.LENGTH_SHORT).show();
                if(imageUri==null) {
                    Toast.makeText(UploadPictureActivity.this, "image uri is null", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onClick: image url is null");
                } else if (!MainActivity.loginStatus) {
                    Toast.makeText(UploadPictureActivity.this,"please login before upload image", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onClick: no login before upload");
                } else {
//                    String filePath = imageUri.getEncodedPath();
//                    Log.d(TAG, "onClick: hhhhhhhhhhhhh "+imageUri.toString());
//                    final String imagePath = imageUri.decode(filePath);
//                    Log.d(TAG, "onClick: hhhhhhhhhhhhh "+imagePath.toString());
//                    final String imagePath = imageUri.toString();
//                    uploadImage(imagePath);
//                    Log.d(TAG, "onClick: tttttttttttt " + imageUriString);
//                    Log.d(TAG, "onClick: ttttttttt2 " + UriToPathOnKitKat(imageUri));
                    uploadImage(imageUriString);
                }
            }
        });
        FloatingActionButton cancelUploadButton = (FloatingActionButton) findViewById(R.id.cancel_upload_button);
        cancelUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UploadPictureActivity.this, "cancel upload button clicked", Toast.LENGTH_SHORT).show();
                finish();
            }
        });




    }

    private void uploadImage(String imagePath) {
        new NetworkTask().execute(imagePath);
    }


    private String doGet(String url) {
        Log.d(TAG, "doGet: begin");
        Request.Builder reqBuilder = new Request.Builder();
        Request request = reqBuilder.url(url).get().build();
        Log.d(TAG, "doGet: try");
        try {
            Response response = MainActivity.okHttpClientWithCookie.newCall(request).execute();
            Log.d(TAG, "doGet: reponse code is " + String.valueOf(response.code()));
            if (response.isSuccessful()) {
                Log.d(TAG, "doGet: get body");
                String resultValue = response.body().string();
                Log.d(TAG, "doGet: succ");
                return resultValue;
            }
            return "error";
        } catch (IOException e) {
            Log.d(TAG, "doGet: here is error");
            e.printStackTrace();
            Log.d(TAG, "doGet: doGet error");
            return "error";
        }
    }

    class TestServerNetworkTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            return doGet(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (!"error".equals(s)) {
                Log.d(TAG, "onPostExecute: the content of doGet" + s);
            }
        }
    }

    class NetworkTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return doPost(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if(!"error".equals(result)) {
                Log.i(TAG, "the address of picture " + result);
                Toast.makeText(UploadPictureActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                UploadPictureActivity.this.finish();
            } else {
                Toast.makeText(UploadPictureActivity.this, "上传失败，请重新登录后再试", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String doPost(String imagePath) {

        File imageTempUploadfile = new File(getExternalCacheDir(), "temp.jpg");
        OutputStream outputTempFileStream = null;
        try {
            if(imageTempUploadfile.exists()) {
                imageTempUploadfile.delete();
            }
            imageTempUploadfile.createNewFile();
            outputTempFileStream = new FileOutputStream(imageTempUploadfile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Uri imageUri = Uri.parse(imagePath);
        try {
            InputStream imageInputStream = getContentResolver().openInputStream(imageUri);
            try {
                try {
                    byte[] buffer = new byte[4*1024];
                    int read;
                    while((read=imageInputStream.read(buffer))!=-1) {
                        outputTempFileStream.write(buffer, 0, read);
                    }
                    outputTempFileStream.flush();
                } finally {
                    outputTempFileStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String result = "error";
        MultipartBody.Builder builder = new MultipartBody.Builder();

        Log.d(TAG, "doPost: "+imagePath);
//        builder.addFormDataPart("image", imagePath,
//                RequestBody.create(MediaType.parse("image/jpeg"), imageTempUploadfile));
//
//        RequestBody requestBody = builder.build();
//        Request.Builder reqBuilder = new Request.Builder();
//        Request request = reqBuilder
//                .url("http://www.baidu.com" + "/uploadimage")
//                .post(requestBody)
//                .build();

        RequestBody filebody = RequestBody.create(MediaType.parse("image/jpeg"), imageTempUploadfile);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", imagePath, filebody)
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + "/uploadimage")
                .post(requestBody)
                .build();

        Log.d(TAG, "doPost: 请求地址 : " + BASE_URL + "/uploadimage");
        try {
            Response response = MainActivity.okHttpClientWithCookie.newCall(request).execute();
            Log.d(TAG, "doPost: 响应码" + response.code());
            if(response.isSuccessful()) {
                String resultValue = response.body().string();
                Log.d(TAG, "doPost: 响应体" + resultValue);
                return resultValue;
            }
        } catch (Exception e) {
            Log.d(TAG, "doPost: error is ");
            e.printStackTrace();
            Log.d(TAG, "doPost: error done");
        }
        return result;
    }





}
