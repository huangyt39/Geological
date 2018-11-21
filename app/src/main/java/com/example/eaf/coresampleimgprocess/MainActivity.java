package com.example.eaf.coresampleimgprocess;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Authenticator;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;


public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener, ImageDetailFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";

    private DrawerLayout drawerLayout;

    private Uri imageUri;
    private static final int TAKE_PHOTO = 1;
    private static final int UPLOAD_PHOTO = 2;
    private static final int CHOOSE_PHOTO = 3;
    private static final int REGISTER_AND_LOGIN = 4;

    private CircleImageView circleImageView;
    public static boolean loginStatus = false;
    protected static String currentUsername = "";
    public static TextView usernameTextView;

    protected static OkHttpClient okHttpClientWithCookie;
    protected List<Cookie> cookiesOfHttpClient = new ArrayList<>();

    private MainFragment mainFragment = null;
    private ImageDetailFragment imageDetailFragment = null;

    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBar actionBar = getSupportActionBar();//这个actionBar实际上是由toolBar来完成的，这里获得的实际上是toolBar
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_main_item);//设置默认选中的Item
        mainFragment = new MainFragment();
        imageDetailFragment = new ImageDetailFragment();
        replaceFragment(mainFragment);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_main_item:
                        if (mainFragment == null) {
                            mainFragment = new MainFragment();
                            Log.d(TAG, "onNavigationItemSelected: initial mainFragment reference");
                            replaceFragment(mainFragment);
                        }
                        else {
                            replaceFragment(mainFragment);
                        }
                        break;
                    case R.id.logout:
                        loginStatus = false;
                        usernameTextView.setText("anoynomus");
                        Toast.makeText(MainActivity.this, "logout successfully", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_image_detail:
                        if (imageDetailFragment == null) {
                            imageDetailFragment = new ImageDetailFragment();
                            Log.d(TAG, "onNavigationItemSelected: initial imageDetailFragment ");
                            replaceFragment(imageDetailFragment);
                        } else {
                            replaceFragment(imageDetailFragment);
                        }
                        break;
                    default:
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "FAB clicked", Toast.LENGTH_SHORT).show();
            }
        });

        View headerLayout = navigationView.getHeaderView(0);
        circleImageView = (CircleImageView) headerLayout.findViewById(R.id.icon_image);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, REGISTER_AND_LOGIN);
            }
        });
        usernameTextView = headerLayout.findViewById(R.id.username);

        okHttpClientWithCookie = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookiesOfHttpClient = cookies;
                        Log.d(TAG, "saveFromResponse: url is " + url);
                        for (Cookie cookie : cookiesOfHttpClient) {
                            Log.d(TAG, "saveFromResponse: a cookie is " + cookie);
                        }
                        Log.d(TAG, "saveFromResponse: cookies end");
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        return cookiesOfHttpClient;
                    }
                }).build();
        if(!loginStatus){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, REGISTER_AND_LOGIN);
            Toast.makeText(getApplicationContext(),"Please Login First.",Toast.LENGTH_SHORT).show();
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_frame_layout, fragment);
        transaction.commit();
    }

    protected void switchToImageDetailFragment() {
        replaceFragment(imageDetailFragment);
        navigationView.getMenu().getItem(0).setChecked(false);
        navigationView.getMenu().getItem(1).setChecked(true);
//        NavigationView navigationView = findViewById(R.id.nav_view);
//        navigationView.callOnClick();
//        MenuItem menuItem = findViewById(R.id.nav_image_detail);
//        menuItem.setChecked(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        usernameTextView.setText(currentUsername);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.take_photo_item:
                Toast.makeText(this, "take photo ", Toast.LENGTH_SHORT).show();
                File outputImage = new File(getExternalCacheDir(), "outputimage.jpg");

                Log.d(TAG, "onOptionsItemSelected: the param in File : " + getExternalCacheDir());
                try {
                    if(outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT>=24) {
                    if(outputImage==null) Log.d(TAG, "onOptionsItemSelected: position z");
                    imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.eaf.coresampleimgprocess", outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }
                Log.d(TAG, "onOptionsItemSelected: position 3");
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                Log.d(TAG, "onOptionsItemSelected: geeeeeeee" + imageUri.toString());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
                break;
            case R.id.photo_album_item:
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
                } else {
                    openAlbum();
                }
                break;
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case TAKE_PHOTO:
                Toast.makeText(this, "on take photo result", Toast.LENGTH_SHORT).show();
                if(resultCode == RESULT_OK) {
                    Intent intent = new Intent(this, UploadPictureActivity.class);
                    intent.putExtra("imageUri", imageUri.toString());
                    startActivityForResult(intent, UPLOAD_PHOTO);
                }
                break;
            case UPLOAD_PHOTO:
//                Toast.makeText(this, "on upload photo result", Toast.LENGTH_SHORT).show();
                //mainFragment.testFunction();
                mainFragment.loadPictureFromServer();
                break;
            case CHOOSE_PHOTO:
                if(resultCode==RESULT_OK) {
                    if(Build.VERSION.SDK_INT>=19) {
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            case REGISTER_AND_LOGIN:
                if(resultCode==RESULT_OK) {
                    loginStatus = true;
                    usernameTextView.setText(currentUsername);
                    Toast.makeText(this, "Register or login done", Toast.LENGTH_SHORT);
                } else {
                    loginStatus = false;
                    Toast.makeText(this, "Register or login error", Toast.LENGTH_SHORT);
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Toast.makeText(getApplicationContext(),"load picture from: "+uri,Toast.LENGTH_SHORT ).show();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if(cursor!=null) {
            if(cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if(imagePath != null) {
            Intent intent = new Intent(MainActivity.this, UploadPictureActivity.class);
            intent.putExtra("imagePath", imagePath);
            startActivityForResult(intent, UPLOAD_PHOTO);
        }
    }

    public void onFragmentInteraction(Uri uri) {

    }
}
