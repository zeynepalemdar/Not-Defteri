package com.example.notdefterirevize;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class Deneme extends AppCompatActivity {
    private static final int SELECT_PHOTO = 100;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deneme);
        imageView =  findViewById(R.id.image_camera);

    }

    public void openCamera(View view) {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SELECT_PHOTO){
            if(resultCode == RESULT_OK){



            Uri selectImage = data.getData();
            InputStream inputStream = null;

            try {
                assert selectImage != null;
                inputStream = getContentResolver().openInputStream(selectImage);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }

            BitmapFactory.decodeStream(inputStream);
            imageView.setImageURI(selectImage);
            }
        }

    }
}