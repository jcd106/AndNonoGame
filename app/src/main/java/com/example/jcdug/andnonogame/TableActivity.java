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
    private ListView puzzleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        db = MainActivity.getDB();
        Intent prev = this.getIntent();
        size = prev.getStringExtra("Size");
        Log.d("Size", size);
        type = prev.getStringExtra("Type");
    }

    @Override
    public void onStart(){
        super.onStart();

        try {
            if(type.equals("Binary")) {
                final ArrayList<PuzzleUpload> puzzles = performSizeQuery(size);

                puzzleList = (ListView) findViewById(R.id.result_list);
                puzzleList.setBackground(ContextCompat.getDrawable(this, R.drawable.border_button));

                Log.d("ListView", puzzleList.toString());

                ArrayAdapter<PuzzleUpload> arrayAdapter = new ArrayAdapter<PuzzleUpload>(
                        this,
                        android.R.layout.simple_list_item_1,
                        puzzles);

                puzzleList.setAdapter(arrayAdapter);

                puzzleList.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    // argument position gives the index of item which is clicked
                    public void onItemClick(AdapterView<?> arg0, View v,int position, long arg3)
                    {
                        PuzzleUpload selectedPuzzle = puzzles.get(position);
                        Puzzle puzzle = selectedPuzzle.convertToPuzzle();
                        try {
                            db.insertDownloadedPuzzle(selectedPuzzle.getUserID(), puzzle.getID(), puzzle, puzzle.getSize(), 0, db.getWritableDatabase());
                            Toast.makeText(getApplicationContext(), "Downloading puzzle: "+ selectedPuzzle,   Toast.LENGTH_LONG).show();
                        } catch (SQLiteConstraintException|IOException e) {
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
                            e.printStackTrace();
                        }
                    }
                });
            }
            else if(type.equals("Color")) {
                final ArrayList<ColorPuzzleUpload> puzzles = performColorSizeQuery(size);

                puzzleList = (ListView) findViewById(R.id.result_list);
                puzzleList.setBackground(ContextCompat.getDrawable(this, R.drawable.border_button));

                Log.d("ListView", puzzleList.toString());

                ArrayAdapter<ColorPuzzleUpload> arrayAdapter = new ArrayAdapter<ColorPuzzleUpload>(
                        this,
                        android.R.layout.simple_list_item_1,
                        puzzles);

                puzzleList.setAdapter(arrayAdapter);

                puzzleList.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    // argument position gives the index of item which is clicked
                    public void onItemClick(AdapterView<?> arg0, View v,int position, long arg3)
                    {
                        ColorPuzzleUpload selectedPuzzle = puzzles.get(position);
                        /*ColorPuzzle puzzle = selectedPuzzle;
                        try {
                            db.insertDownloadedColorPuzzle(selectedPuzzle.getUserID(), puzzle.getID(), puzzle, puzzle.getSize(), 0, db.getWritableDatabase());
                            Toast.makeText(getApplicationContext(), "Downloading puzzle: "+ selectedPuzzle,   Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
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
                            e.printStackTrace();
                            e.printStackTrace();
                        }
                        */

                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<PuzzleUpload> performSizeQuery(String size) throws Exception{

        ExecutorService executor = Executors.newCachedThreadPool();
        Future<ArrayList<PuzzleUpload>> futureCall = executor.submit(new PerformQuery());
        ArrayList<PuzzleUpload> puzzles = futureCall.get(10, TimeUnit.SECONDS); // Here the thread will be blocked
        // until the result came back.
        return puzzles;
    }

    public class PerformQuery implements Callable<ArrayList<PuzzleUpload>> {
        @Override
        public ArrayList<PuzzleUpload> call() throws Exception {
            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            eav.put(":val1", new AttributeValue().withS(size));

            final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("Size = :val1").withExpressionAttributeValues(eav);
            List<PuzzleUpload> scanResult = mapper.scan(PuzzleUpload.class, scanExpression);
            ArrayList<PuzzleUpload> puzzles = new ArrayList<PuzzleUpload>(scanResult);
            for (PuzzleUpload puzzle : scanResult) {
                Log.d("Puzzle", puzzle.toString());
            }
            return puzzles;
        }
    }

    public ArrayList<ColorPuzzleUpload> performColorSizeQuery(String size) throws Exception{

        ExecutorService executor = Executors.newCachedThreadPool();
        Future<ArrayList<ColorPuzzleUpload>> futureCall = executor.submit(new PerformColorQuery());
        ArrayList<ColorPuzzleUpload> puzzles = futureCall.get(10, TimeUnit.SECONDS); // Here the thread will be blocked
        // until the result came back.
        return puzzles;
    }

    public class PerformColorQuery implements Callable<ArrayList<ColorPuzzleUpload>> {
        @Override
        public ArrayList<ColorPuzzleUpload> call() throws Exception {
            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            eav.put(":val1", new AttributeValue().withS(size));

            final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("Size = :val1").withExpressionAttributeValues(eav);
            List<ColorPuzzleUpload> scanResult = mapper.scan(ColorPuzzleUpload.class, scanExpression);
            ArrayList<ColorPuzzleUpload> puzzles = new ArrayList<ColorPuzzleUpload>(scanResult);
            for (ColorPuzzleUpload puzzle : scanResult) {
                Log.d("Puzzle", puzzle.toString());
            }
            return puzzles;
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
