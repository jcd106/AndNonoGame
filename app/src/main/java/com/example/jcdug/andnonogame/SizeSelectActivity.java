package com.example.jcdug.andnonogame;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Size;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The activity which is used to display the size select screen
 *
 * @author Josh Dughi, Peter Todorov
 * @version 1.4.3
 */
public class SizeSelectActivity extends AppCompatActivity implements BarFragment.OnFragmentInteractionListener {

    boolean isColor;
    String table, yourTable;

    /**
     * Handles the creation of the view
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_size_select);
        this.setupScreen();
    }

    /**
     * Handles the button clicks on the screen
     *
     * @param view the button that was clicked
     */
    public void onButtonClick(View view) {
        //Create a new intent to start PuzzleSelectActivity
        Intent i = new Intent(this, PuzzleSelectActivity.class);
        if (isColor) {
            i.putExtra("color", true);
        } else {
            i.putExtra("color", false);
        }

        //Set the extra for the intent based on the button clicked
        switch (view.getId()) {
            case R.id.fivebyfive:
                i.putExtra("size", "5 5");
                break;
            case R.id.tenbyten:
                i.putExtra("size", "10 10");
                break;
            case R.id.tenbyfive:
                i.putExtra("size", "10 5");
                break;
            case R.id.yourpuzzles:
                i.putExtra("size", "your");
                break;
        }

        //Start the intent
        startActivity(i);
    }

    /**
     * Handles the resume of the activity
     */
    @Override
    public void onResume() {
        super.onResume();
        this.setupScreen();
    }

    /**
     * Sets up the buttons for the layout with the correct information
     */
    private void setupScreen() {
        Intent prev = this.getIntent();
        isColor = prev.getBooleanExtra("color", false);

        if (isColor) {
            table = getString(R.string.colorTable);
            yourTable = getString(R.string.yourColorTable);
        } else {
            table = getString(R.string.puzzleTable);
            yourTable = getString(R.string.yourTable);
        }

        final Context context = this;

        //Get the layout for the activity
        RelativeLayout sizeSelectLayout = (RelativeLayout) findViewById(R.id.activity_size_select);
        removeButtons(sizeSelectLayout);

        //Create a new OnClickListener for a size being selected
        View.OnClickListener listener = new View.OnClickListener() {
            //When the button is clicked, PuzzleActivity is started with the id of the chosen puzzle
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, PuzzleSelectActivity.class);
                if (isColor) {
                    i.putExtra("color", true);
                } else {
                    i.putExtra("color", false);
                }
                i.putExtra("table", table);

                String size = (String) view.getTag(R.id.size);
                if(size != null){
                    if(size.equals("your")){
                        i.putExtra("size", "your");
                    }
                    else{
                        i.putExtra("size", size);
                    }
                }
                startActivity(i);
            }
        };

        int countPuzzles = 0;
        int countCompleted = 0;

        ArrayList<ArrayList<Integer>> sizesWithCount = new ArrayList<ArrayList<Integer>>();

        //Gets the buttons by id from the view
        Button fiveByFive = (Button) findViewById(R.id.fivebyfive);
        Button tenByTen = (Button) findViewById(R.id.tenbyten);
        Button tenByFive = (Button) findViewById(R.id.tenbyfive);
        Button yourPuzzles = (Button) findViewById(R.id.yourpuzzles);
        //yourPuzzles.setTransformationMethod(null);

        //Set the number of puzzles for the sizes to 0
        int num5by5 = 0;
        int num10by10 = 0;
        int num10by5 = 0;
        int numYours = 0;

        //Set the number of completed puzzles for the sizes to 0
        int comp5by5 = 0;
        int comp10by10 = 0;
        int comp10by5 = 0;
        int compYours = 0;

        //Get the database
        PuzzleDatabase db = MainActivity.getDB();

        //Query the database for the count of puzzles grouped by size and move the cursor to the first tuple
        Cursor c1 = db.getCountBySize(table);
        c1.moveToFirst();

        //Query the database for the count completed grouped by size and move the cursor to the first tuple
        Cursor c2 = db.getCountCompletedBySize(table);
        c2.moveToFirst();

        //Get the index of numPuzzles, Rows, and Cols in c1
        int i1 = c1.getColumnIndex("numPuzzles");
        int r1 = c1.getColumnIndex("Rows");
        int col1 = c1.getColumnIndex("Cols");

        //While there are tuples in c1 to be read
        while (!c1.isAfterLast()) {
            ArrayList<Integer> size = new ArrayList<Integer>();
            size.add(c1.getInt(r1));
            size.add(c1.getInt(col1));
            size.add(c1.getInt(i1));
            size.add(0);
            sizesWithCount.add(size);

            /*
            //if Rows in c1 = 5 and Cols in c1 = 5 in the tuple, set num5by5 to numPuzzles from c1
            if (c1.getInt(r1) == 5 && c1.getInt(col1) == 5) {
                num5by5 = c1.getInt(i1);
            }

            //if Rows in c1 = 10 and Cols in c1 = 10 in the tuple, set num10by10 to numPuzzles from c1
            if (c1.getInt(r1) == 10 && c1.getInt(col1) == 10) {
                num10by10 = c1.getInt(i1);
            }

            //if Rows in c1 = 5 and Cols in c1 = 10 in the tuple, set num10by5 to numPuzzles from c1
            if (c1.getInt(r1) == 5 && c1.getInt(col1) == 10) {
                num10by5 = c1.getInt(i1);
            }
            */


            //Move to the next tuple
            c1.moveToNext();
        }

        //Get the index of numComplete, Rows, and Cols in c2
        int i2 = c2.getColumnIndex("numComplete");
        int r2 = c2.getColumnIndex("Rows");
        int col2 = c2.getColumnIndex("Cols");

        //While there are tuples in c2 to be read
        while (!c2.isAfterLast()) {

            for(int j = 0; j < sizesWithCount.size(); j++){
                if(c2.getInt(r2) == sizesWithCount.get(j).get(0) && c2.getInt(col2) == sizesWithCount.get(j).get(1)){
                    sizesWithCount.get(j).add(3, c2.getInt(i2));
                }
            }

            /*
            //if Rows in c2 = 5 and Cols in c2 = 5 in the tuple, set comp5by5 to numComplete from c2
            if (c2.getInt(r2) == 5 && c2.getInt(col2) == 5) {
                comp5by5 = c2.getInt(i2);
            }

            //if Rows in c2 = 10 and Cols in c2 = 10 in the tuple, set comp10by10 to numComplete from c2
            if (c2.getInt(r2) == 10 && c2.getInt(col2) == 10) {
                comp10by10 = c2.getInt(i2);
            }

            //if Rows in c2 = 5 and Cols in c2 = 10 in the tuple, set comp10by5 to numComplete from c2
            if (c2.getInt(r2) == 5 && c2.getInt(col2) == 10) {
                comp10by5 = c2.getInt(i2);
            }
            */

            //Move to the next tuple
            c2.moveToNext();
        }

        //Query the database for the count of puzzles grouped by size and move the cursor to the first tuple
        Cursor c3 = db.getCountYour(yourTable);
        c3.moveToFirst();

        //Query the database for the count completed grouped by size and move the cursor to the first tuple
        Cursor c4 = db.getCountCompletedYour(yourTable);
        c4.moveToFirst();

        //Get the index of numPuzzles, Rows, and Cols in c1
        int i3 = c3.getColumnIndex("numPuzzles");

        numYours = c3.getInt(i3);

        //Get the index of numComplete, Rows, and Cols in c2
        int i4 = c2.getColumnIndex("numComplete");

        compYours = c4.getInt(i4);

        sizesWithCount.add(new ArrayList<Integer>(Arrays.asList(0, 0, numYours, compYours)));

        /*

        //Set the text for the buttons
        String fiveFiveText = "5x5 (" + comp5by5 + "/" + num5by5 + ")";
        fiveByFive.setText(fiveFiveText);
        String tenTenText = "10x10 (" + comp10by10 + "/" + num10by10 + ")";
        tenByTen.setText(tenTenText);
        String tenFiveText = "10x5 (" + comp10by5 + "/" + num10by5 + ")";
        tenByFive.setText(tenFiveText);
        String yourText = "Your Puzzles (" + compYours + "/" + numYours + ")";
        yourPuzzles.setText(yourText);

        */

        int belowId = R.id.fragment_bar_size_select;

        for(int j = 0; j < sizesWithCount.size(); j++) {

            final float scale = this.getResources().getDisplayMetrics().density;
            int pixels = (int) (125 * scale + 0.5f);

            //Create a layout and set the margins for the puzzle select button
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 20, 0, 20);

            //Create a new button
            //Button b = new Button(this);
            LayoutInflater li = LayoutInflater.from(this);
            Button b = (Button) li.inflate(R.layout.puzzle_sizes_button, sizeSelectLayout, false);

            //x by y puzzle has x columns and y rows
            int rowSize = sizesWithCount.get(j).get(0);
            int colSize = sizesWithCount.get(j).get(1);
            int numComplete = sizesWithCount.get(j).get(3);
            int numPuzzles = sizesWithCount.get(j).get(2);

            String buttonText;
            if(rowSize != 0 && colSize != 0) {
                buttonText = colSize + "x" + rowSize + " (" + numComplete + "/" + numPuzzles + ")";
                b.setTag(R.id.size, colSize + " " + rowSize);
            }
            else{
                buttonText = "Your Puzzles (" + numComplete + "/" + numPuzzles + ")";
                b.setTag(R.id.size, "your");
            }
            b.setText(buttonText);
            b.setOnClickListener(listener);
            b.setId(j+100);

            params.addRule(RelativeLayout.BELOW, belowId);
            sizeSelectLayout.addView(b, params);
            belowId = j+100;

        }

    }

    //Auto-Generated
    public void onFragmentInteraction(Uri uri) {

    }

    private void removeButtons(RelativeLayout rl) {
        boolean doBreak = false;
        while(!doBreak) {
            int childCount = rl.getChildCount();
            int i;
            for(i = 0; i < childCount; i++) {
                View current = rl.getChildAt(i);
                if(current instanceof Button) {
                    rl.removeView(current);
                    break;
                }
            }
            if (i == childCount)
                doBreak = true;
        }
    }
}
