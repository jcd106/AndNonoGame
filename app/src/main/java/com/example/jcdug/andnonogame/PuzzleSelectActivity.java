package com.example.jcdug.andnonogame;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

/**
 * The activity which is used to display the select puzzle screen
 *
 * @author Josh Dughi, Peter Todorov
 * @version 1.4.3
 */
public class PuzzleSelectActivity extends AppCompatActivity implements BarFragment.OnFragmentInteractionListener {

    boolean isColor;
    String table;
    /**
     * Handles the creation of the activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_select);
        this.setupScreen();
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
        //Get the layout for the activity
        RelativeLayout puzzleSelectLayout = (RelativeLayout) findViewById(R.id.activity_puzzle_select);
        removeButtons(puzzleSelectLayout);

        //Get the context of the activity
        final Context context = this;

        //Get the size chosen on the size select screen
        Intent i = getIntent();
        String size = i.getStringExtra("size");
        isColor = i.getBooleanExtra("color", false);
        table = i.getStringExtra("table");
        if (size.equals("your")) {

            TextView sizeText = (TextView) findViewById(R.id.puzzle_select_size);
            sizeText.setText("Your Puzzles");

            //Get the puzzle database
            PuzzleDatabase db = MainActivity.getDB();

            //Query the database for all puzzles with the selected size
            Cursor c1 = db.getAllYourPuzzles(table);

            //Move the cursor to the first tuple
            c1.moveToFirst();

            //Get the index of puzzle id attribute in the query
            int i1 = c1.getColumnIndex("PuzzleID");
            int complete = c1.getColumnIndex("Complete");

            //Set prevId to the id of the text view
            int prevId = R.id.puzzle_select_size;
            int belowId = R.id.puzzle_select_size;
            int count = 0;

            //Create a new OnClickListener for a puzzle being selected
            View.OnClickListener listener = new View.OnClickListener() {
                //When the button is clicked, PuzzleActivity is started with the id of the chosen puzzle
                @Override
                public void onClick(View view) {
                    Intent i;
                    if (isColor) {
                        i = new Intent(context, ColorPuzzleActivity.class);
                    } else {
                        i = new Intent(context, PuzzleActivity.class);
                    }
                    i.putExtra("table", table);
                    i.putExtra("puzzleID", Integer.toString(view.getId()));
                    startActivity(i);
                }
            };
            //While there are still tuples for the cursor to read
            while (!c1.isAfterLast()) {
                final float scale = this.getResources().getDisplayMetrics().density;
                int pixels = (int) (125 * scale + 0.5f);

                //Create a layout and set the margins for the puzzle select button
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(pixels, pixels);
                //        (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(40, 20, 40, 20);

                //Create a new button
                //Button b = new Button(this);
                LayoutInflater li = LayoutInflater.from(this);
                Button b = (Button) li.inflate(R.layout.puzzle_button, puzzleSelectLayout, false);

                //Get the id from the cursor and set the button's id and text
                int id = c1.getInt(i1);
                int isComp = c1.getInt(complete);
                b.setId(id);
                if (isComp == 1)
                    b.setText("Puzzle: " + id + " is complete");
                else
                    b.setText("Puzzle: " + id + " is incomplete");

                //Set the button's background and OnClickListener
                //b.setBackgroundResource(R.drawable.border_button);
                b.setOnClickListener(listener);

                //Make sure the next button is below prevId
                if (count % 2 == 0) {
                    params.addRule(RelativeLayout.BELOW, prevId);
                    belowId = prevId;
                } else {
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.addRule(RelativeLayout.BELOW, belowId);
                }

                count++;

                puzzleSelectLayout.addView(b, params);

                //Move the cursor to the next tuple
                c1.moveToNext();

                //Set prevId to the current button's id
                prevId = id;
            }
        }
        else if(size.equals("down")){
            TextView sizeText = (TextView) findViewById(R.id.puzzle_select_size);
            sizeText.setText("Downloaded Puzzles");

            //Get the puzzle database
            PuzzleDatabase db = MainActivity.getDB();

            //Query the database for all puzzles with the selected size
            Cursor c1 = db.getAllDownPuzzles(table);

            //Move the cursor to the first tuple
            c1.moveToFirst();

            //Get the index of puzzle id attribute in the query
            int i1 = c1.getColumnIndex("PuzzleID");
            int complete = c1.getColumnIndex("Complete");

            //Set prevId to the id of the text view
            int prevId = R.id.puzzle_select_size;
            int belowId = R.id.puzzle_select_size;
            int count = 0;

            //Create a new OnClickListener for a puzzle being selected
            View.OnClickListener listener = new View.OnClickListener() {
                //When the button is clicked, PuzzleActivity is started with the id of the chosen puzzle
                @Override
                public void onClick(View view) {
                    Intent i;
                    String userid = view.getTag().toString();
                    if (isColor) {
                        i = new Intent(context, ColorPuzzleActivity.class);
                    } else {
                        i = new Intent(context, PuzzleActivity.class);
                    }
                    i.putExtra("table", table);
                    i.putExtra("user", userid);
                    int user8 = Integer.parseInt(userid.substring(0,8));
                    i.putExtra("puzzleID", Integer.toString(view.getId()-user8));
                    startActivity(i);
                }
            };
            //While there are still tuples for the cursor to read
            while (!c1.isAfterLast()) {
                final float scale = this.getResources().getDisplayMetrics().density;
                int pixels = (int) (125 * scale + 0.5f);

                //Create a layout and set the margins for the puzzle select button
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(pixels, pixels);
                //        (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(40, 20, 40, 20);

                //Create a new button
                //Button b = new Button(this);
                LayoutInflater li = LayoutInflater.from(this);
                Button b = (Button) li.inflate(R.layout.puzzle_button, puzzleSelectLayout, false);

                //Get the id from the cursor and set the button's id and text
                int isComp = c1.getInt(complete);
                int userIndex = c1.getColumnIndex("UserID");
                String userId = c1.getString(userIndex);
                int user8 = Integer.parseInt(userId.substring(0,8));
                int id = c1.getInt(i1)+user8;
                b.setId(id);
                b.setTag(userId);
                if (isComp == 1)
                    b.setText("Puzzle: " + (id-user8) + " is complete");
                else
                    b.setText("Puzzle: " + (id-user8) + " is incomplete");

                //Set the button's background and OnClickListener
                //b.setBackgroundResource(R.drawable.border_button);
                b.setOnClickListener(listener);

                //Make sure the next button is below prevId
                if (count % 2 == 0) {
                    params.addRule(RelativeLayout.BELOW, prevId);
                    belowId = prevId;
                } else {
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.addRule(RelativeLayout.BELOW, belowId);
                }

                count++;

                puzzleSelectLayout.addView(b, params);

                //Move the cursor to the next tuple
                c1.moveToNext();

                //Set prevId to the current button's id
                prevId = id;
            }
        }
        else {
            String[] rxc = size.split(" ");
            int[] s = {Integer.parseInt(rxc[0]), Integer.parseInt(rxc[1])};

            //Create a text view with the text being the chosen size
            TextView sizeText = (TextView) findViewById(R.id.puzzle_select_size);
            sizeText.setText(s[0] + "x" + s[1]);

            //Get the puzzle database
            PuzzleDatabase db = MainActivity.getDB();

            //Query the database for all puzzles with the selected size
            Cursor c1 = db.getPuzzlesBySize(table, s[0], s[1]);

            //Move the cursor to the first tuple
            c1.moveToFirst();

            //Get the index of puzzle id attribute in the query
            int i1 = c1.getColumnIndex("PuzzleID");
            int complete = c1.getColumnIndex("Complete");

            //Set prevId to the id of the text view
            int prevId = R.id.puzzle_select_size;
            int belowId = R.id.puzzle_select_size;
            int count = 0;

            //Create a new OnClickListener for a puzzle being selected
            View.OnClickListener listener = new View.OnClickListener() {
                //When the button is clicked, PuzzleActivity is started with the id of the chosen puzzle
                @Override
                public void onClick(View view) {
                    Intent i;
                    if (isColor) {
                        i = new Intent(context, ColorPuzzleActivity.class);
                    } else {
                        i = new Intent(context, PuzzleActivity.class);
                    }
                    i.putExtra("table", table);
                    i.putExtra("puzzleID", Integer.toString(view.getId()));
                    startActivity(i);
                }
            };
            //While there are still tuples for the cursor to read
            while (!c1.isAfterLast()) {
                final float scale = this.getResources().getDisplayMetrics().density;
                int pixels = (int) (125 * scale + 0.5f);

                //Create a layout and set the margins for the puzzle select button
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(pixels, pixels);
                //        (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(40, 20, 40, 20);

                //Create a new button
                //Button b = new Button(this);
                LayoutInflater li = LayoutInflater.from(this);
                Button b = (Button) li.inflate(R.layout.puzzle_button, puzzleSelectLayout, false);

                //Get the id from the cursor and set the button's id and text
                int id = c1.getInt(i1);
                int isComp = c1.getInt(complete);
                b.setId(id);
                if (isComp == 1)
                    b.setText("Puzzle: " + id + " is complete");
                else
                    b.setText("Puzzle: " + id + " is incomplete");

                //Set the button's background and OnClickListener
                //b.setBackgroundResource(R.drawable.border_button);
                b.setOnClickListener(listener);

                //Make sure the next button is below prevId
                if (count % 2 == 0) {
                    params.addRule(RelativeLayout.BELOW, prevId);
                    belowId = prevId;
                } else {
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.addRule(RelativeLayout.BELOW, belowId);
                }

                count++;

                puzzleSelectLayout.addView(b, params);

                //Move the cursor to the next tuple
                c1.moveToNext();

                //Set prevId to the current button's id
                prevId = id;
            }
        }
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

    // Auto-Generated
    public void onFragmentInteraction(Uri uri) {

    }
}