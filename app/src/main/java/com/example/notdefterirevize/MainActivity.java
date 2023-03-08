package com.example.notdefterirevize;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;


import com.example.notdefterirevize.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    public String sourceUri, destinationUri;
    DrawerLayout drawer;
    Uri resultUri;
    NavigationView navigationView;
    RoundedImageView roundedImageView;
    SharedPreferences sharedPref;

//    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
//        @Override
//        public void onReceive(Context ctxt, Intent intent) {
//            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
//            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
//            int batteryPct = level * 100 / (int)scale;
//            System.out.println("%"+batteryPct);
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        destinationUri = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        drawer = binding.drawerLayout;
        navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(this);
        registerReceiver(new CheckedBattery(), new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        roundedImageView = navigationView.getHeaderView(0).findViewById(R.id.imageView);
        decode();

        roundedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click();
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        System.out.println("onCreateOptionsMenu");
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        System.out.println("girdi");
        int id = item.getItemId();

        switch (id) {

            case R.id.nav_home:
                Toast.makeText(getApplicationContext(), "Oldu", Toast.LENGTH_SHORT).show();
                System.out.println("olduuuu");
                break;
            case R.id.nav_gallery:
                Toast.makeText(getApplicationContext(), "Olduu", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_slideshow:
                Toast.makeText(getApplicationContext(), "Olduuu", Toast.LENGTH_SHORT).show();
                break;
            case R.id.email:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"zeynepalemdar557@gmail.com"});
                startActivity(Intent.createChooser(intent, "Send Email"));
                break;
            default:
                Toast.makeText(getApplicationContext(), "default", Toast.LENGTH_SHORT).show();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        System.out.println("onSupportNavigateUp");
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();

    }

    public void image(View view) {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        someActivityResultLauncher.launch(photoPickerIntent);

    }


    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        crop(imageUri, destinationUri);
//                        try {
//                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
//                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                            roundedImageView.setImageBitmap(bitmap);
//                            encode(bitmap);
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        }
                    }
                }
            });

    public void click() {
        roundedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image(roundedImageView);
                decode();

            }
        });
    }

    public void encode(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String input = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.edit().putString("stringValue", input).apply();
    }

    public void decode() {
        String savedString = PreferenceManager.getDefaultSharedPreferences(this).getString("stringValue", "");
        if (!savedString.isEmpty()) {
            byte[] bytes = Base64.decode(savedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            roundedImageView.setImageBitmap(bitmap);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            resultUri = UCrop.getOutput(data);
                try {
                    InputStream inputStream = getContentResolver().openInputStream(resultUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    roundedImageView.setImageBitmap(bitmap);
                    encode(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }

    public void crop(Uri uri , String stringBuilder){

        UCrop.Options options = new UCrop.Options();

        UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), stringBuilder)))
                .withOptions(options)
//                .withAspectRatio(16,9)
                .withMaxResultSize(2000,2000)
                .start(this);

    }



    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }
    }
}