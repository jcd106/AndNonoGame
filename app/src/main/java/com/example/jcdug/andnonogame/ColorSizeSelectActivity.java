package com.example.jcdug.andnonogame;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ColorSizeSelectActivity extends AppCompatActivity implements BarFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_size_select);
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
        i.putExtra("color","1");

        //Set the extra for the intent based on the button clicked
        switch (view.getId()) {
            case R.id.fivebyfive:
                i.putExtra("size", "5 5");
                break;
            case R.id.tenbyten:
                i.putExtra("size", "10 10");
                break;
            case R.id.fivebyten:
                i.putExtra("size", "5 10");
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
        //Gets the buttons by id from the view
        Button fiveByFive = (Button) findViewById(R.id.fivebyfive);
        Button tenByTen = (Button) findViewById(R.id.tenbyten);
        Button tenByFive = (Button) findViewById(R.id.fivebyten);

        //Set the number of puzzles for the sizes to 0
        int num5by5 = 0;
        int num10by10 = 0;
        int num5by10 = 0;

        //Set the number of completed puzzles for the sizes to 0
        int comp5by5 = 0;
        int comp10by10 = 0;
        int comp5by10 = 0;

        //Get the database
        PuzzleDatabase db = MainActivity.getDB();

        //Query the database for the count of puzzles grouped by size and move the cursor to the first tuple
        Cursor c1 = db.getColorCountBySize();
        c1.moveToFirst();

        //Query the database for the count completed grouped by size and move the cursor to the first tuple
        Cursor c2 = db.getColorCountCompletedBySize();
        c2.moveToFirst();

        //Get the index of numPuzzles, Rows, and Cols in c1
        int i1 = c1.getColumnIndex("numPuzzles");
        int r1 = c1.getColumnIndex("Rows");
        int col1 = c1.getColumnIndex("Cols");

        //While there are tuples in c1 to be read
        while (!c1.isAfterLast()) {
            //if Rows in c1 = 5 and Cols in c1 = 5 in the tuple, set num5by5 to numPuzzles from c1
            if (c1.getInt(r1) == 5 && c1.getInt(col1) == 5) {
                num5by5 = c1.getInt(i1);
            }

            //if Rows in c1 = 10 and Cols in c1 = 10 in the tuple, set num10by10 to numPuzzles from c1
            if (c1.getInt(r1) == 10 && c1.getInt(col1) == 10) {
                num10by10 = c1.getInt(i1);
            }

            //if Rows in c1 = 5 and Cols in c1 = 10 in the tuple, set num5by10 to numPuzzles from c1
            if (c1.getInt(r1) == 10 && c1.getInt(col1) == 5) {
                num5by10 = c1.getInt(i1);
            }

            //Move to the next tuple
            c1.moveToNext();
        }

        //Get the index of numComplete, Rows, and Cols in c2
        int i2 = c2.getColumnIndex("numComplete");
        int r2 = c2.getColumnIndex("Rows");
        int col2 = c2.getColumnIndex("Cols");

        //While there are tuples in c2 to be read
        while (!c2.isAfterLast()) {
            //if Rows in c2 = 5 and Cols in c2 = 5 in the tuple, set comp5by5 to numComplete from c2
            if (c2.getInt(r2) == 5 && c2.getInt(col2) == 5) {
                comp5by5 = c2.getInt(i2);
            }

            //if Rows in c2 = 10 and Cols in c2 = 10 in the tuple, set comp10by10 to numComplete from c2
            if (c2.getInt(r2) == 10 && c2.getInt(col2) == 10) {
                comp10by10 = c2.getInt(i2);
            }

            //if Rows in c2 = 5 and Cols in c2 = 10 in the tuple, set comp5by10 to numComplete from c2
            if (c2.getInt(r2) == 10 && c2.getInt(col2) == 5) {
                comp5by10 = c2.getInt(i2);
            }

            //Move to the next tuple
            c2.moveToNext();
        }

        //Set the text for the buttons
        String fiveFiveText = "5x5 (" + comp5by5 + "/" + num5by5 + ")";
        fiveByFive.setText(fiveFiveText);
        String tenTenText = "10x10 (" + comp10by10 + "/" + num10by10 + ")";
        tenByTen.setText(tenTenText);
        String tenFiveText = "5x10 (" + comp5by10 + "/" + num5by10 + ")";
        tenByFive.setText(tenFiveText);
    }

    //Auto-Generated
    public void onFragmentInteraction(Uri uri) {

    }
}