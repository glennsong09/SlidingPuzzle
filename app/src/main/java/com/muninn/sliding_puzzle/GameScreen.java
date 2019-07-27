package com.muninn.sliding_puzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class GameScreen extends AppCompatActivity {

    ArrayList<Integer> correctOrientation;
    ArrayList<Integer> boardOrientation;
    ArrayList<ImageView> allPieces;
    ImageView[][] board;
    TableLayout table;
    int pieceNum;
    int rowColLimit;
    int emptyRowNum;
    int emptyColNum;
    int tableWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);
        Intent intent = getIntent();
        pieceNum = intent.getIntExtra("RowColNumber", 0);
        rowColLimit = (int) Math.sqrt(pieceNum);
        allPieces = new ArrayList<>();
        table = (TableLayout) findViewById(R.id.tableLO);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        tableWidth = displayMetrics.widthPixels - 160;

        Bundle extras = getIntent().getExtras();
        String fileName = extras.getString("picname");
        File filePath = getFileStreamPath(fileName);
        Bitmap bmp = BitmapFactory.decodeFile(filePath.toString());
        bmp = centerCrop(bmp);
        configureToMainMenuButton();
        splitImage(bmp, pieceNum);
    }

    public void configureToMainMenuButton() {
        Button buttonStart = findViewById(R.id.gameMenuButton);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(GameScreen.this, MainActivity.class));
            }
        });
    }

    private void splitImage(Bitmap bitmap, int chunkNumbers) {
        int rows, cols;
        int chunkHeight, chunkWidth;
        ArrayList<Bitmap> chunkedImages = new ArrayList<Bitmap>(chunkNumbers);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
        rows = cols = (int) Math.sqrt(chunkNumbers);
        chunkHeight = bitmap.getHeight() / rows;
        chunkWidth = bitmap.getWidth() / cols;
        int yCoord = 0;
        for (int x = 0; x < rows; x++) {
            int xCoord = 0;
            for (int y = 0; y < cols; y++) {
                Bitmap tmpImage = Bitmap.createBitmap(scaledBitmap, xCoord, yCoord, chunkWidth, chunkHeight);
                tmpImage = Bitmap.createScaledBitmap(tmpImage, tableWidth / rowColLimit, tableWidth / rowColLimit, true);
                chunkedImages.add(tmpImage);
                xCoord += chunkWidth;
            }
            yCoord += chunkHeight;
        }
        for (int i = 0; i < chunkedImages.size(); i++) {
            ImageView mImg = new ImageView(this);
            mImg.setImageBitmap(chunkedImages.get(i));
            allPieces.add(mImg);
        }
        Collections.shuffle(allPieces); //this scrambles the board
        showAllPieces();
        makeInteractable();

    }

    private void makeInteractable() {
        int count = 0;
        board = new ImageView[rowColLimit][rowColLimit];
        for (int i = 0; i < rowColLimit; i++) {
            for (int j = 0; j < rowColLimit; j++) {
                board[i][j] = allPieces.get(count);
                count++;
            }
        }

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j <board[0].length; j++) {
                board[i][j].isClickable();
                board[i][j].setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        int row = 0;
                        int col = 0;
                        for (int i = 0; i < board.length; i++) {
                            for (int j = 0; j < board[0].length; j++) {
                                if (board[i][j] == v) {
                                    row = i;
                                    col = j;
                                }
                            }
                        }
                        if (checkIfNextToEmptySpace(row, col)) {
                            movePiece(v, row, col);
                        } else {
                            Context context = getApplicationContext();
                            CharSequence text = "Can't move this piece!";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }
                });
            }
        }
        board[rowColLimit - 1][rowColLimit - 1].setVisibility(View.INVISIBLE); //set to empty somehow
        emptyRowNum = rowColLimit - 1;
        emptyColNum = rowColLimit - 1;
    }

    private void movePiece(View v, int row, int col) {
        Bitmap bm = loadBitmapFromView(v);
        board[emptyRowNum][emptyColNum].setImageBitmap(bm);
        board[emptyRowNum][emptyColNum].setVisibility(View.VISIBLE);
        emptyRowNum = row;
        emptyColNum = col;
        board[emptyRowNum][emptyColNum].setVisibility(View.INVISIBLE);
    }

    private boolean checkIfNextToEmptySpace(int row, int col) {
        if (((row == emptyRowNum - 1) && (col == emptyColNum))
                || ((row == emptyRowNum + 1) && (col == emptyColNum))
                || ((row == emptyRowNum) && (col == emptyColNum - 1))
                || ((row == emptyRowNum) && (col == emptyColNum + 1))) {
            return true;
        }
        return false;
    }

    private void showAllPieces() {
        int count = 0;
        TableLayout table = (TableLayout) findViewById(R.id.tableLO);
        for (int i = 0; i < rowColLimit; i++) {
            TableRow tr = new TableRow(this);
            for (int j = 0; j < rowColLimit; j++) {
                ImageView view = allPieces.get(count);
                view.setAdjustViewBounds(false);
                view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                tr.addView(view);
                count++;
            }
            table.addView(tr);
        }
    }

    private Bitmap centerCrop(Bitmap srcBmp) {
        Bitmap dstBmp = null;
        if (srcBmp.getWidth() >= srcBmp.getHeight()){
            dstBmp = Bitmap.createBitmap(srcBmp, srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                    0, srcBmp.getHeight(), srcBmp.getHeight());
        } else {
            dstBmp = Bitmap.createBitmap(srcBmp, 0, srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                    srcBmp.getWidth(), srcBmp.getWidth());
        }
        return dstBmp;
    }

    public static Bitmap loadBitmapFromView(View v) {
        int width = v.getWidth();
        int height = v.getHeight();
        Bitmap b = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }
}
