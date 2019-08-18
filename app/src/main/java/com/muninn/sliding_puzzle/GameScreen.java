package com.muninn.sliding_puzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
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

import static android.graphics.Bitmap.Config.ARGB_8888;

public class GameScreen extends AppCompatActivity {

    ArrayList<Bitmap> chunkedImages;
    ArrayList<Integer> rowNums;
    ArrayList<Integer> colNums;
    ArrayList<ImageView> allPieces;
    int[][] scrambledBoard;
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
        pieceNum = intent.getIntExtra("rowColNumber", 0);
        rowColLimit = (int) Math.sqrt(pieceNum);
        allPieces = new ArrayList<>();
        rowNums = new ArrayList<>();
        colNums = new ArrayList<>();
        scrambledBoard = new int[rowColLimit][rowColLimit];
        table = (TableLayout) findViewById(R.id.tableLO);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        tableWidth = displayMetrics.widthPixels - 16;

        Bundle extras = getIntent().getExtras();
        String fileName = extras.getString("movedPic");
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

    public void splitImage(Bitmap bitmap, int chunkNumbers) {
        int rows, cols;
        int chunkHeight, chunkWidth;
        chunkedImages = new ArrayList<Bitmap>(chunkNumbers);
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
                tmpImage = addBlackBorder(tmpImage, 2);
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
        emptyRowNum = rowColLimit - 1;
        emptyColNum = rowColLimit - 1;

        generateSolvablePuzzle();
        showAllPieces();
        makeInteractable();
    }

    private Bitmap addBlackBorder(Bitmap bmp, int borderSize) {
        Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawBitmap(bmp, 0,0, null);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);
        drawRectangle(canvas, paint, 1,1,bmp.getWidth() - 2,  bmp.getHeight() - 2);
        return bmpWithBorder;
    }

    public static void drawRectangle(Canvas canvas,Paint paint,float startX,float startY, float endX, float endY)
    {
        float minX = startX;
        float minY = startY;
        float maxX = endX;
        float maxY = endY;
        if(minX > maxX)
        {
            minX = endX;
            maxX = startX;
        }
        if(minY > maxY)
        {
            minY = endY;
            maxY = startY;
        }
        RectF rectangle = new RectF(minX,minY,maxX,maxY);
        canvas.drawRect(rectangle, paint);
    }

    public void makeInteractable() {
        board = new ImageView[rowColLimit][rowColLimit];
        for (int i = 0; i < rowColLimit; i++) {
            for (int j = 0; j < rowColLimit; j++) {
                board[i][j] = allPieces.get(scrambledBoard[i][j]);
            }
        }

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
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
                            checkIfSolved();
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
        //board[emptyRowNum][emptyColNum].setVisibility(View.INVISIBLE); //set to empty somehow
        Bitmap bitmap = generateBlankImageWithColor(tableWidth / rowColLimit, tableWidth / rowColLimit, Color.TRANSPARENT);
        board[emptyRowNum][emptyColNum].setImageBitmap(bitmap);
    }

    public static Bitmap generateBlankImageWithColor(int width, int height,int color)
    {
        Bitmap cs = null;
        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(cs);

        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawRect(0, 0,width, height, paint);

        return cs;
    }

    public void movePiece(View v, int row, int col) {
        Bitmap bm = loadBitmapFromView(v);
        board[emptyRowNum][emptyColNum].setImageBitmap(bm);
        //board[emptyRowNum][emptyColNum].setVisibility(View.VISIBLE);

        int temp = scrambledBoard[emptyRowNum][emptyColNum];
        scrambledBoard[emptyRowNum][emptyColNum] = scrambledBoard[row][col];
        scrambledBoard[row][col] = temp;

        emptyRowNum = row;
        emptyColNum = col;

        /*
        board[emptyRowNum][emptyColNum].setVisibility(View.INVISIBLE);
        int[] colors = {0,0,0};
        Bitmap bitmap = Bitmap.createBitmap(colors, tableWidth / rowColLimit, tableWidth / rowColLimit, ARGB_8888);
        board[emptyRowNum][emptyColNum].setImageBitmap(bitmap);
        */
        Bitmap bitmap = generateBlankImageWithColor(tableWidth / rowColLimit, tableWidth / rowColLimit, Color.TRANSPARENT);
        board[emptyRowNum][emptyColNum].setImageBitmap(bitmap);
    }

    public void movePiece(int row, int col) {
        int temp = scrambledBoard[emptyRowNum][emptyColNum];
        scrambledBoard[emptyRowNum][emptyColNum] = scrambledBoard[row][col];
        scrambledBoard[row][col] = temp;

        emptyRowNum = row;
        emptyColNum = col;
    }

    public boolean checkIfNextToEmptySpace(int row, int col) {
        if (((row == emptyRowNum - 1) && (col == emptyColNum))
                || ((row == emptyRowNum + 1) && (col == emptyColNum))
                || ((row == emptyRowNum) && (col == emptyColNum - 1))
                || ((row == emptyRowNum) && (col == emptyColNum + 1))) {
            return true;
        }
        return false;
    }

    public void showAllPieces() {
        int count = 0;
        TableLayout table = (TableLayout) findViewById(R.id.tableLO);
        for (int i = 0; i < rowColLimit; i++) {
            TableRow tr = new TableRow(this);
            for (int j = 0; j < rowColLimit; j++) {
                ImageView view = allPieces.get(scrambledBoard[i][j]);
                view.setAdjustViewBounds(false);
                view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                tr.addView(view);
                count++;
            }
            table.addView(tr);
        }
    }

    public Bitmap centerCrop(Bitmap srcBmp) {
        Bitmap dstBmp = null;
        if (srcBmp.getWidth() >= srcBmp.getHeight()) {
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
        Bitmap b = Bitmap.createBitmap( width, height, ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    public void setInitialBoard() {
        int count = 0;
        for (int i = 0; i < rowColLimit; i++) {
            for (int j = 0; j < rowColLimit; j++) {
                scrambledBoard[i][j] = count;
                count++;
            }
        }
    }

    public boolean checkIfSolved() {
        int var = 0;
        for (int i = 0; i < rowColLimit; i++) {
            for (int j = 0; j < rowColLimit; j++) {
                if (scrambledBoard[i][j] != var) {
                    return false;
                }
                var++;
            }
        }
        Context context = getApplicationContext();
        CharSequence text = "Congratulations! You solved the puzzle!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        return true;
    }

    public boolean findAllMoveablePieces(int row, int col) {
        if (((row == emptyRowNum - 1) && (col == emptyColNum))
                || ((row == emptyRowNum + 1) && (col == emptyColNum))
                || ((row == emptyRowNum) && (col == emptyColNum - 1))
                || ((row == emptyRowNum) && (col == emptyColNum + 1))) {
            return true;
        }
        return false;
    }

    public void generateSolvablePuzzle() {
        int count = 0;
        setInitialBoard();
        int shuffle = numToShuffle();

        while (count < shuffle) {
            for (int i = 0; i < rowColLimit; i++) {
                for (int j = 0; j < rowColLimit; j++) {
                    if (findAllMoveablePieces(i, j)) {
                        rowNums.add(i);
                        colNums.add(j);
                    }
                }
            }
            int randNum = (int) (Math.random() * (rowNums.size()));
            movePiece(rowNums.get(randNum), colNums.get(randNum));
            rowNums.clear();
            colNums.clear();
            count++;
        }
    }


    public int numToShuffle() {
        if (pieceNum == 4) {
            return 10;
        } else if (pieceNum == 9) {
            return 70;
        } else if (pieceNum == 25) {
            return 100;
        } else if (pieceNum == 36) {
            return 200;
        } else if (pieceNum == 49) {
            return 300;
        } else if (pieceNum == 64) {
            return 400;
        } else if (pieceNum == 81) {
            return 500;
        } else return 650;
    }
}
