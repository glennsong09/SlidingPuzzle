package com.muninn.sliding_puzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class SelectScreen extends AppCompatActivity {

    private Spinner spinner;
    private static final String[] paths = {"2x2", "3x3", "4x4", "5x5", "6x6", "7x7", "8x8", "9x9", "10x10"};
    int pieceNum = 0;
    ImageView screen;
    ArrayList<Integer> allImages = new ArrayList<>();
    int currentPic = 0;
    public static final int IMAGE_GALLERY_REQUEST = 20;
    public static final int CAMERA_REQUEST_CODE = 228;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_screen);
        allImages.add(R.drawable.sketch0);
        allImages.add(R.drawable.sketch1);
        allImages.add(R.drawable.sketch2);
        allImages.add(R.drawable.sketch3);
        allImages.add(R.drawable.sketch4);
        allImages.add(R.drawable.sketch5);
        allImages.add(R.drawable.sketch6);
        allImages.add(R.drawable.sketch7);
        allImages.add(R.drawable.sketch8);
        allImages.add(R.drawable.sketch9);
        allImages.add(R.drawable.sketch10);
        allImages.add(R.drawable.sketch11);
        allImages.add(R.drawable.sketch12);
        allImages.add(R.drawable.sketch13);
        allImages.add(R.drawable.sketch14);
        allImages.add(R.drawable.sketch15);
        allImages.add(R.drawable.sketch16);
        allImages.add(R.drawable.sketch17);
        allImages.add(R.drawable.sketch18);
        allImages.add(R.drawable.sketch19);
        allImages.add(R.drawable.sketch20);
        allImages.add(R.drawable.sketch21);
        allImages.add(R.drawable.sketch22);
        allImages.add(R.drawable.sketch23);
        allImages.add(R.drawable.sketch24);
        allImages.add(R.drawable.sketch25);

        screen = (ImageView) findViewById(R.id.pictureDisplay);
        screen.setImageResource(allImages.get(0));
        spinner = (Spinner)findViewById(R.id.rowColSelectSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectScreen.this,
                android.R.layout.simple_spinner_item, paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                switch (position) {
                    case 0:
                        pieceNum = 4;
                        break;
                    case 1:
                        pieceNum = 9;
                        break;
                    case 2:
                        pieceNum = 16;
                        break;
                    case 3:
                        pieceNum = 25;
                        break;
                    case 4:
                        pieceNum = 36;
                        break;
                    case 5:
                        pieceNum = 49;
                        break;
                    case 6:
                        pieceNum = 64;
                        break;
                    case 7:
                        pieceNum = 81;
                        break;
                    case 8:
                        pieceNum = 100;
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                pieceNum = 25;
            }
        });

        configureNextButton();
        configureBackButton();
        configureSelectButton();
        configureUploadButton();
    }

    public void configureUploadButton() {
        Button buttonUpload = findViewById(R.id.uploadButton);
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String pictureDirectoryPath = pictureDirectory.getPath();
                Uri data = Uri.parse(pictureDirectoryPath);
                photoPickerIntent.setDataAndType(data, "image/*");
                startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);
            } });
    }

    public void configureNextButton() {
        Button buttonNext = findViewById(R.id.forwardButton);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (currentPic == allImages.size() - 1) {
                    Context context = getApplicationContext();
                    CharSequence text = "No more pictures!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else if (currentPic < allImages.size() - 1) {
                    currentPic++;
                    screen.setImageResource(allImages.get(currentPic));
                }
            } });
    }

    public void configureBackButton() {
        Button buttonBack = findViewById(R.id.backButton);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (currentPic == 0) {
                    Context context = getApplicationContext();
                    CharSequence text = "Can't go back!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else if (currentPic > 0) {
                    currentPic--;
                    screen.setImageResource(allImages.get(currentPic));
                }
            } });
    }

    public void configureSelectButton() {
        Button buttonSelect= findViewById(R.id.selectButton);
        buttonSelect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                drawableToMove();
            } });
    }

    public void drawableToMove() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), allImages.get(currentPic));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();

        String fileName = "ToMove.png";
        try {
            FileOutputStream fileOutStream = openFileOutput(fileName, MODE_PRIVATE);
            fileOutStream.write(b);
            fileOutStream.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        Intent intent = new Intent(this, GameScreen.class);
        intent.putExtra("movedPic", fileName);
        intent.putExtra("rowColNumber", pieceNum);
        startActivity(intent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                Toast.makeText(this, "Image Saved.", Toast.LENGTH_LONG).show();
            }
            if (requestCode == IMAGE_GALLERY_REQUEST) {
                Uri imageUri = data.getData();
                InputStream inputStream;
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] b = baos.toByteArray();

                    String fileName = "ToMove.png";
                    try {
                        FileOutputStream fileOutStream = openFileOutput(fileName, MODE_PRIVATE);
                        fileOutStream.write(b);
                        fileOutStream.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }

                    setContentView(R.layout.activity_select_screen);
                    screen.setImageBitmap(bitmap);

                    Intent intent = new Intent(this, GameScreen.class);
                    intent.putExtra("rowColNumber", pieceNum);
                    intent.putExtra("movedPic", fileName);

                    startActivity(intent);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            Uri imageUri = getIntent().getData();
            screen.setImageURI(imageUri);
        }
    }
}
