package com.example.eaf.coresampleimgprocess;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.StringUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class terrainPredictionActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener, ImageDetailFragment.OnFragmentInteractionListener{

    private static final int CHOOSE_FILE_CODE = 1;
    private static final String TAG1 = "FileChoose";
    private static final String TAG = "UploadPictureActivity";
    private TextView hint;
    private static final String BASE_URL = "http://10.0.2.2:5000";
    private TPFragment TPFragment = null;
    private ImageDetailFragment imageDetailFragment = null;
    private DrawerLayout drawerLayoutForTP;
    private NavigationView navigationView;
    private boolean menusState = false;
    private ActionBarDrawerToggle mDrawerToggle;

    List<String> file_List=new ArrayList<>();
    private filePathAdapter myAdapter;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terrain_prediction);

        TPFragment = new TPFragment();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_forTP);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();//这个actionBar实际上是由toolBar来完成的，这里获得的实际上是toolBar
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setTitle("Terrain Prediction");
        }



        drawerLayoutForTP=findViewById(R.id.drawer_layout_forTP);
        navigationView = findViewById(R.id.nav_view);
        hint= findViewById(R.id.hint);

        //选择完file之后呈现出来
        listView=findViewById(R.id.files_list);
        myAdapter=new filePathAdapter(this,file_List){};
        listView.setAdapter(myAdapter);
        //listView点击事件——点击与长按
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(terrainPredictionActivity.this);
                builder.setTitle("File Path");
                builder.setMessage(file_List.get(position));
                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //no action
                    }
                });
                builder.show();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                //弹出是否删除对话框
                AlertDialog.Builder builder=new AlertDialog.Builder(terrainPredictionActivity.this);
                builder.setTitle("Delete or not?");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //no action
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        file_List.remove(position);
                        myAdapter.notifyDataSetChanged();
                    }
                });
                builder.show();
                return true;
            }
        });
        //修改当前登录用户
        View headerLayout = navigationView.getHeaderView(0);
        TextView username=headerLayout.findViewById(R.id.username);
        username.setText(MainActivity.currentUsername);

        navigationView.setCheckedItem(R.id.nav_main_item);//设置默认选中的Item

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayoutForTP,
                R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                menusState = true;
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                menusState = false;
            }

        };
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (menusState) {
                    drawerLayoutForTP.closeDrawer(Gravity.LEFT);
                } else {
                    drawerLayoutForTP.openDrawer(Gravity.LEFT);
                }
            }
        });


        final FloatingActionButton uploadButton = (FloatingActionButton) findViewById(R.id.confirmButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                testServer();
//                Toast.makeText(UploadPictureActivity.this, "upload button clicked", Toast.LENGTH_SHORT).show();
                if(file_List.size()==0) {
                    Toast.makeText(terrainPredictionActivity.this, "You haven`t select files to upload", Toast.LENGTH_SHORT).show();
                } else if (!MainActivity.loginStatus) {
                    Toast.makeText(terrainPredictionActivity.this,"please login before upload image", Toast.LENGTH_SHORT).show();
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
                    //传输TP所需文件
                    if(file_List.size()==10){
                        for(int j=0;j<file_List.size();j++){
                            File file=new File(file_List.get(j));
                            file.getName();
                            String []temp=null;
                            Uri imageUri=null;
                            if (Build.VERSION.SDK_INT>=24) {
                                if(file==null) Log.d(TAG, "onOptionsItemSelected: position z");
                                imageUri = FileProvider.getUriForFile(terrainPredictionActivity.this, "com.example.eaf.coresampleimgprocess", file);
                            } else {
                                imageUri = Uri.fromFile(file);
                            }
                            temp=imageUri.toString().split("/");
                            String UriString="";
                            UriString+=temp[0]+"/";
                            for(int i=1;i<temp.length-1;i++){
                                UriString+=temp[i]+"/";
                            }
                            UriString+=file.getName();
                            uploadFile(UriString);
                        }
                        TPFragment.loadTPResultFromServer();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Please provide enough files to do terrain prediction.",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CHOOSE_FILE_CODE) {
                //If it is a file selection mode, you need to get the path collection of all the files selected
                //List<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);//Constant.RESULT_INFO == "paths"
                List<String> list = data.getStringArrayListExtra("paths");
                Toast.makeText(getApplicationContext(), "selected " + list.size() , Toast.LENGTH_SHORT).show();
                if(list.size()!=0){
                    hint.setText("The files you select:");
                    for(int i=0;i<list.size();i++){
                        file_List.add(list.get(i));
                    }
                    myAdapter.notifyDataSetChanged();
                }
            }
        }
        else {
            Log.e(TAG1, "onActivityResult() error, resultCode: " + resultCode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }





    //toolbar 相关事件
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_for_tp, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.select_filesForTP:
                Toast.makeText(getApplicationContext(),"Select Files to upload for TP.",Toast.LENGTH_SHORT).show();
                //可看到的文件大小限制：10M
                new LFilePicker()
                        .withActivity(terrainPredictionActivity.this)
                        .withRequestCode(CHOOSE_FILE_CODE)
                        .withStartPath("/storage/emulated/0")
                        .withIsGreater(false)
                        .withFileSize(10000 * 1024)
                        .withTitle("Files")
                        .start();
            default:
                break;
        }
        return true;
    }



    private void uploadFile(String filePath) {
        new terrainPredictionActivity.NetworkTask().execute(filePath);
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
                Toast.makeText(terrainPredictionActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(terrainPredictionActivity.this, "上传失败，请重新登录后再试", Toast.LENGTH_SHORT).show();
            }
        }
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

    private String doPost(String filePath) {

        File tempUploadfile = new File(getExternalCacheDir(), "temp.jpg");
        OutputStream outputTempFileStream = null;
        try {
            if(tempUploadfile.exists()) {
                tempUploadfile.delete();
            }
            tempUploadfile.createNewFile();
            outputTempFileStream = new FileOutputStream(tempUploadfile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Uri imageUri = Uri.parse(filePath);
        try {
            InputStream fileInputStream = getContentResolver().openInputStream(imageUri);
            try {
                try {
                    byte[] buffer = new byte[4*1024];
                    int read;
                    while((read=fileInputStream.read(buffer))!=-1) {
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

        Log.d(TAG, "doPost: "+filePath);
//        builder.addFormDataPart("image", filePath,
//                RequestBody.create(MediaType.parse("image/jpeg"), tempUploadfile));
//
//        RequestBody requestBody = builder.build();
//        Request.Builder reqBuilder = new Request.Builder();
//        Request request = reqBuilder
//                .url("http://www.baidu.com" + "/uploadimage")
//                .post(requestBody)
//                .build();
        RequestBody filebody = RequestBody.create(MediaType.parse("application/octet-stream; charset=utf-8"), tempUploadfile);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", filePath, filebody)
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + "/uploadfiles")
                .post(requestBody)
                .build();

        Log.d(TAG, "doPost: 请求地址 : " + BASE_URL + "/uploadfiles");
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






    public void onFragmentInteraction(Uri uri) {
    }


}
