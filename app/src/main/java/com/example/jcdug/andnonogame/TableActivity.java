package com.example.jcdug.andnonogame;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TableActivity extends AppCompatActivity implements BarFragment.OnFragmentInteractionListener{

    private static PuzzleDatabase db;
    DynamoDBMapper mapper = MainActivity.getMapper();
    String size;
    String type;
    String rating;
    private ListView puzzleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        db = MainActivity.getDB();
        Intent prev = this.getIntent();
        size = prev.getStringExtra("Size");
        type = prev.getStringExtra("Type");
        rating = prev.getStringExtra("Rating");
    }

    @Override
    public void onStart(){
        super.onStart();

        try {
            if(type.equals("Binary")) {
                if(rating.equals("Rating")) {
                    final ArrayList<PuzzleUpload> puzzles = performSizeQuery();

                    puzzleList = (ListView) findViewById(R.id.result_list);
                    puzzleList.setBackground(ContextCompat.getDrawable(this, R.drawable.border_button));

                    ArrayAdapter<PuzzleUpload> arrayAdapter = new ArrayAdapter<PuzzleUpload>(
                            this,
                            android.R.layout.simple_list_item_1,
                            puzzles);

                    puzzleList.setAdapter(arrayAdapter);

                    puzzleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        // argument position gives the index of item which is clicked
                        public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                            PuzzleUpload selectedPuzzle = puzzles.get(position);
                            Puzzle puzzle = selectedPuzzle.convertToPuzzle();
                            try {
                                db.insertDownloadedPuzzle(selectedPuzzle.getUserID(), puzzle.getID(), puzzle, puzzle.getSize(), 0, db.getWritableDatabase());
                                Toast.makeText(getApplicationContext(), "Downloaded puzzle: " + selectedPuzzle, Toast.LENGTH_LONG).show();
                            } catch (SQLiteConstraintException | IOException e) {
                                //Create a popup notifying the user that puzzle has been downloaded previously
                                AlertDialog alertDialog = new AlertDialog.Builder(TableActivity.this).create();
                                alertDialog.setTitle("Duplicate Puzzle");
                                alertDialog.setMessage("You have already downloaded this puzzle!");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                            }
                        }
                    });
                }
                else if(size.equals("Size")) {
                    final ArrayList<PuzzleUpload> puzzles = performRatingQuery();

                    puzzleList = (ListView) findViewById(R.id.result_list);
                    puzzleList.setBackground(ContextCompat.getDrawable(this, R.drawable.border_button));

                    ArrayAdapter<PuzzleUpload> arrayAdapter = new ArrayAdapter<PuzzleUpload>(
                            this,
                            android.R.layout.simple_list_item_1,
                            puzzles);

                    puzzleList.setAdapter(arrayAdapter);

                    puzzleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        // argument position gives the index of item which is clicked
                        public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                            PuzzleUpload selectedPuzzle = puzzles.get(position);
                            Puzzle puzzle = selectedPuzzle.convertToPuzzle();
                            try {
                                db.insertDownloadedPuzzle(selectedPuzzle.getUserID(), puzzle.getID(), puzzle, puzzle.getSize(), 0, db.getWritableDatabase());
                                Toast.makeText(getApplicationContext(), "Downloaded puzzle: " + selectedPuzzle, Toast.LENGTH_LONG).show();
                            } catch (SQLiteConstraintException | IOException e) {
                                //Create a popup notifying the user that puzzle has been downloaded previously
                                AlertDialog alertDialog = new AlertDialog.Builder(TableActivity.this).create();
                                alertDialog.setTitle("Duplicate Puzzle");
                                alertDialog.setMessage("You have already downloaded this puzzle!");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                            }
                        }
                    });
                }
                else{
                    final ArrayList<PuzzleUpload> puzzles = performSizeRatingQuery();

                    puzzleList = (ListView) findViewById(R.id.result_list);
                    puzzleList.setBackground(ContextCompat.getDrawable(this, R.drawable.border_button));

                    ArrayAdapter<PuzzleUpload> arrayAdapter = new ArrayAdapter<PuzzleUpload>(
                            this,
                            android.R.layout.simple_list_item_1,
                            puzzles);

                    puzzleList.setAdapter(arrayAdapter);

                    puzzleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        // argument position gives the index of item which is clicked
                        public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                            PuzzleUpload selectedPuzzle = puzzles.get(position);
                            Puzzle puzzle = selectedPuzzle.convertToPuzzle();
                            try {
                                db.insertDownloadedPuzzle(selectedPuzzle.getUserID(), puzzle.getID(), puzzle, puzzle.getSize(), 0, db.getWritableDatabase());
                                Toast.makeText(getApplicationContext(), "Downloaded puzzle: " + selectedPuzzle, Toast.LENGTH_LONG).show();
                            } catch (SQLiteConstraintException | IOException e) {
                                //Create a popup notifying the user that puzzle has been downloaded previously
                                AlertDialog alertDialog = new AlertDialog.Builder(TableActivity.this).create();
                                alertDialog.setTitle("Duplicate Puzzle");
                                alertDialog.setMessage("You have already downloaded this puzzle!");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                            }
                        }
                    });
                }
            }
            else if(type.equals("Color")) {
                if(rating.equals("Rating")) {
                    final ArrayList<ColorPuzzleUpload> puzzles = performColorSizeQuery();

                    puzzleList = (ListView) findViewById(R.id.result_list);
                    puzzleList.setBackground(ContextCompat.getDrawable(this, R.drawable.border_button));

                    ArrayAdapter<ColorPuzzleUpload> arrayAdapter = new ArrayAdapter<ColorPuzzleUpload>(
                            this,
                            android.R.layout.simple_list_item_1,
                            puzzles);

                    puzzleList.setAdapter(arrayAdapter);

                    puzzleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        // argument position gives the index of item which is clicked
                        public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                            ColorPuzzleUpload selectedPuzzle = puzzles.get(position);
                            ColorPuzzle puzzle = selectedPuzzle.convertToPuzzle();
                            try {
                                db.insertDownloadedColorPuzzle(selectedPuzzle.getUserID(), puzzle.getID(), puzzle, puzzle.getSize(), 0, db.getWritableDatabase());
                                Toast.makeText(getApplicationContext(), "Downloaded puzzle: " + selectedPuzzle, Toast.LENGTH_LONG).show();
                            } catch (SQLiteConstraintException | IOException e) {
                                //Create a popup notifying the user that puzzle has been downloaded previously
                                AlertDialog alertDialog = new AlertDialog.Builder(TableActivity.this).create();
                                alertDialog.setTitle("Duplicate Puzzle");
                                alertDialog.setMessage("You have already downloaded this puzzle!");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                            }
                        }
                    });
                }
                else if(size.equals("Size")){
                    final ArrayList<ColorPuzzleUpload> puzzles = performColorRatingQuery();

                    puzzleList = (ListView) findViewById(R.id.result_list);
                    puzzleList.setBackground(ContextCompat.getDrawable(this, R.drawable.border_button));

                    ArrayAdapter<ColorPuzzleUpload> arrayAdapter = new ArrayAdapter<ColorPuzzleUpload>(
                            this,
                            android.R.layout.simple_list_item_1,
                            puzzles);

                    puzzleList.setAdapter(arrayAdapter);

                    puzzleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        // argument position gives the index of item which is clicked
                        public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                            ColorPuzzleUpload selectedPuzzle = puzzles.get(position);
                            ColorPuzzle puzzle = selectedPuzzle.convertToPuzzle();
                            try {
                                db.insertDownloadedColorPuzzle(selectedPuzzle.getUserID(), puzzle.getID(), puzzle, puzzle.getSize(), 0, db.getWritableDatabase());
                                Toast.makeText(getApplicationContext(), "Downloaded puzzle: " + selectedPuzzle, Toast.LENGTH_LONG).show();
                            } catch (SQLiteConstraintException | IOException e) {
                                //Create a popup notifying the user that puzzle has been downloaded previously
                                AlertDialog alertDialog = new AlertDialog.Builder(TableActivity.this).create();
                                alertDialog.setTitle("Duplicate Puzzle");
                                alertDialog.setMessage("You have already downloaded this puzzle!");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                            }
                        }
                    });
                }
                else{
                    final ArrayList<ColorPuzzleUpload> puzzles = performColorSizeRatingQuery();

                    puzzleList = (ListView) findViewById(R.id.result_list);
                    puzzleList.setBackground(ContextCompat.getDrawable(this, R.drawable.border_button));

                    ArrayAdapter<ColorPuzzleUpload> arrayAdapter = new ArrayAdapter<ColorPuzzleUpload>(
                            this,
                            android.R.layout.simple_list_item_1,
                            puzzles);

                    puzzleList.setAdapter(arrayAdapter);

                    puzzleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        // argument position gives the index of item which is clicked
                        public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                            ColorPuzzleUpload selectedPuzzle = puzzles.get(position);
                            ColorPuzzle puzzle = selectedPuzzle.convertToPuzzle();
                            try {
                                db.insertDownloadedColorPuzzle(selectedPuzzle.getUserID(), puzzle.getID(), puzzle, puzzle.getSize(), 0, db.getWritableDatabase());
                                Toast.makeText(getApplicationContext(), "Downloaded puzzle: " + selectedPuzzle, Toast.LENGTH_LONG).show();
                            } catch (SQLiteConstraintException | IOException e) {
                                //Create a popup notifying the user that puzzle has been downloaded previously
                                AlertDialog alertDialog = new AlertDialog.Builder(TableActivity.this).create();
                                alertDialog.setTitle("Duplicate Puzzle");
                                alertDialog.setMessage("You have already downloaded this puzzle!");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                            }
                        }
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<PuzzleUpload> performSizeQuery() throws Exception{

        ExecutorService executor = Executors.newCachedThreadPool();
        Future<ArrayList<PuzzleUpload>> futureCall = executor.submit(new SizeQuery());
        ArrayList<PuzzleUpload> puzzles = futureCall.get(10, TimeUnit.SECONDS);
        return puzzles;
    }

    public class SizeQuery implements Callable<ArrayList<PuzzleUpload>> {
        @Override
        public ArrayList<PuzzleUpload> call() throws Exception {
            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            eav.put(":val1", new AttributeValue().withS(size));

            final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("Size = :val1").withExpressionAttributeValues(eav);
            List<PuzzleUpload> scanResult = mapper.scan(PuzzleUpload.class, scanExpression);
            ArrayList<PuzzleUpload> puzzles = new ArrayList<PuzzleUpload>(scanResult);
            return puzzles;
        }
    }

    public ArrayList<ColorPuzzleUpload> performColorSizeQuery() throws Exception{

        ExecutorService executor = Executors.newCachedThreadPool();
        Future<ArrayList<ColorPuzzleUpload>> futureCall = executor.submit(new ColorSizeQuery());
        ArrayList<ColorPuzzleUpload> puzzles = futureCall.get(10, TimeUnit.SECONDS);
        return puzzles;
    }

    public class ColorSizeQuery implements Callable<ArrayList<ColorPuzzleUpload>> {
        @Override
        public ArrayList<ColorPuzzleUpload> call() throws Exception {
            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            eav.put(":val1", new AttributeValue().withS(size));

            final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("Size = :val1").withExpressionAttributeValues(eav);
            List<ColorPuzzleUpload> scanResult = mapper.scan(ColorPuzzleUpload.class, scanExpression);
            ArrayList<ColorPuzzleUpload> puzzles = new ArrayList<ColorPuzzleUpload>(scanResult);
            return puzzles;
        }
    }


    public ArrayList<PuzzleUpload> performRatingQuery() throws Exception{

        ExecutorService executor = Executors.newCachedThreadPool();
        Future<ArrayList<PuzzleUpload>> futureCall = executor.submit(new RatingQuery());
        ArrayList<PuzzleUpload> puzzles = futureCall.get(10, TimeUnit.SECONDS);
        return puzzles;
    }

    public class RatingQuery implements Callable<ArrayList<PuzzleUpload>> {
        @Override
        public ArrayList<PuzzleUpload> call() throws Exception {
            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            int ratingVal = Integer.parseInt(rating.split(" ")[0]);
            eav.put(":val1", new AttributeValue().withN(ratingVal + ""));
            eav.put(":val2", new AttributeValue().withN(ratingVal+1 + ""));

            final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("AverageRating >= :val1 and AverageRating < :val2").withExpressionAttributeValues(eav);
            List<PuzzleUpload> scanResult = mapper.scan(PuzzleUpload.class, scanExpression);
            ArrayList<PuzzleUpload> puzzles = new ArrayList<PuzzleUpload>(scanResult);
            return puzzles;
        }
    }


    public ArrayList<PuzzleUpload> performSizeRatingQuery() throws Exception{

        ExecutorService executor = Executors.newCachedThreadPool();
        Future<ArrayList<PuzzleUpload>> futureCall = executor.submit(new SizeRatingQuery());
        ArrayList<PuzzleUpload> puzzles = futureCall.get(10, TimeUnit.SECONDS);
        return puzzles;
    }

    public class SizeRatingQuery implements Callable<ArrayList<PuzzleUpload>> {
        @Override
        public ArrayList<PuzzleUpload> call() throws Exception {
            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            int ratingVal = Integer.parseInt(rating.split(" ")[0]);
            eav.put(":val1", new AttributeValue().withN(ratingVal + ""));
            eav.put(":val2", new AttributeValue().withN(ratingVal+1 + ""));
            eav.put(":val3", new AttributeValue().withS(size));

            final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("AverageRating >= :val1 and AverageRating < :val2 and Size = :val3").withExpressionAttributeValues(eav);
            List<PuzzleUpload> scanResult = mapper.scan(PuzzleUpload.class, scanExpression);
            ArrayList<PuzzleUpload> puzzles = new ArrayList<PuzzleUpload>(scanResult);
            return puzzles;
        }
    }

    public ArrayList<ColorPuzzleUpload> performColorRatingQuery() throws Exception{

        ExecutorService executor = Executors.newCachedThreadPool();
        Future<ArrayList<ColorPuzzleUpload>> futureCall = executor.submit(new ColorRatingQuery());
        ArrayList<ColorPuzzleUpload> puzzles = futureCall.get(10, TimeUnit.SECONDS);
        return puzzles;
    }

    public class ColorRatingQuery implements Callable<ArrayList<ColorPuzzleUpload>> {
        @Override
        public ArrayList<ColorPuzzleUpload> call() throws Exception {
            int ratingVal = Integer.parseInt(rating.split(" ")[0]);
            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            eav.put(":val1", new AttributeValue().withN(ratingVal + ""));
            eav.put(":val2", new AttributeValue().withN(ratingVal+1 + ""));

            final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("AverageRating >= :val1 and AverageRating < :val2").withExpressionAttributeValues(eav);
            List<ColorPuzzleUpload> scanResult = mapper.scan(ColorPuzzleUpload.class, scanExpression);
            ArrayList<ColorPuzzleUpload> puzzles = new ArrayList<ColorPuzzleUpload>(scanResult);
            return puzzles;
        }
    }

    public ArrayList<ColorPuzzleUpload> performColorSizeRatingQuery() throws Exception{

        ExecutorService executor = Executors.newCachedThreadPool();
        Future<ArrayList<ColorPuzzleUpload>> futureCall = executor.submit(new ColorSizeRatingQuery());
        ArrayList<ColorPuzzleUpload> puzzles = futureCall.get(10, TimeUnit.SECONDS);
        return puzzles;
    }

    public class ColorSizeRatingQuery implements Callable<ArrayList<ColorPuzzleUpload>> {
        @Override
        public ArrayList<ColorPuzzleUpload> call() throws Exception {
            int ratingVal = Integer.parseInt(rating.split(" ")[0]);
            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            eav.put(":val1", new AttributeValue().withN(ratingVal + ""));
            eav.put(":val2", new AttributeValue().withN(ratingVal+1 + ""));
            eav.put(":val3", new AttributeValue().withS(size));

            final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("AverageRating >= :val1 and AverageRating < :val2 and Size = :val3").withExpressionAttributeValues(eav);
            List<ColorPuzzleUpload> scanResult = mapper.scan(ColorPuzzleUpload.class, scanExpression);
            ArrayList<ColorPuzzleUpload> puzzles = new ArrayList<ColorPuzzleUpload>(scanResult);
            return puzzles;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
