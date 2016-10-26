package com.example.jcdug.andnonogame;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.IOException;

/**
 * Activity which handles the display of the settings screen
 *
 * @author Josh Dughi, Peter Todorov
 * @version 1.4.3
 */
public class SettingsActivity extends AppCompatActivity {


    public static final String COLOR_CHOICE = "ColorChoice";    //Name of shared preferences storage location
    Spinner spinner;    //Spinner storing color scheme options

    /**
     * Handles the creation of the SettingsActivity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Retrieves spinner displayed on the screen and sets its options
        spinner = (Spinner) findViewById(R.id.spinner);
        String[] colors = new String[]{
                "Black and White",
                "Red and White",
                "Red and Blue"
        };
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, colors);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(spinnerArrayAdapter);

        //Retrieves the stored spinner position from shared preferences and sets the spinner
        SharedPreferences preferences = this.getSharedPreferences(COLOR_CHOICE, Context.MODE_PRIVATE);
        int position = preferences.getInt("position", 0);
        spinner.setSelection(position);

        //Creates an OnItemSelectedListener for the spinner which store the chosen color scheme in shared preferences
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                SharedPreferences.Editor editor = getSharedPreferences(COLOR_CHOICE, MODE_PRIVATE).edit();
                String[] colors = selected.split(" ");
                editor.putString("empty", colors[2].toUpperCase());
                editor.putString("filled", colors[0].toUpperCase());
                editor.putInt("position", spinner.getSelectedItemPosition());
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Handles the button clicks
     *
     * @param view the button clicked
     */
    public void onClicked(View view) {
        switch (view.getId()) {
            //if the back button, then finish activity and go to previous activity
            case R.id.back_button_settings:
                super.onBackPressed();
                break;
            //if the reset all button, then ask the user if he/she is sure
            case R.id.reset_all_button:
                //Create a prompt asking the user if he/she is sure
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Reset All Puzzles!");
                alertDialog.setMessage("Are you sure?");

                //If the user says no, do nothing
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                //If the user says yes, reset all the puzzles
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Get the puzzle database and get the ids of all puzzles
                                PuzzleDatabase db = MainActivity.getDB();
                                int[] ids = db.getAllPuzzleIDs();

                                //Loop through the ids and reset the puzzle corresponding to the id
                                for (int i = 0; i < ids.length; i++) {
                                    try {
                                        db.resetPuzzle(ids[i]);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                break;

            //if the instructions button, go to instructions activity
            case R.id.instructions_button:
                Intent i = new Intent(this, TutorialActivity.class);
                startActivity(i);
                break;
        }
    }
}