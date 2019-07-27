package com.muninn.sliding_puzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configureStartButton();
        configureUploadButton();
    }

    public void configureStartButton() {
        Button buttonStart = findViewById(R.id.startButton);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SelectScreen.class));
            } });
    }

    public void configureUploadButton() {
        Button buttonUpload = findViewById(R.id.uploadButton);
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
            } });
    }

    /*
    public void drawableToMove() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), allImages.get(currentPic));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();

        String fileName = "SomeName.png";
        try {
            FileOutputStream fileOutStream = openFileOutput(fileName, MODE_PRIVATE);
            fileOutStream.write(b);
            fileOutStream.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        Intent intent = new Intent(this, GameScreen.class);
        intent.putExtra("picname", fileName);

        startActivity(intent);
    }
    */
}
