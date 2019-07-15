package com.muninn.sliding_puzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
                startActivity(new Intent(MainActivity.this, EasyBoard.class));
            } });
    }

    public void configureUploadButton() {
        Button buttonUpload = findViewById(R.id.uploadButton);
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Needs to be able to select picture and use that to start game
            } });
    }
}
