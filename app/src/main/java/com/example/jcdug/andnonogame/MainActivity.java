package com.example.jcdug.andnonogame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * The Activity for the home screen of the application
 *
 * @author Josh Dughi, Peter Todorov
 * @version 1.4.3
 */
public class MainActivity extends AppCompatActivity {

    private static PuzzleDatabase db;

    /**
     * Creates the view for the activity
     * and initializes the PuzzleDatabase
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new PuzzleDatabase(this);
    }

    /**
     * Starts the correct activity depending on which button is clicked
     *
     * @param view The view that was clicked
     */
    public void onClicked(View view) {
        switch (view.getId()) {
            case R.id.play_button_main:
                Intent i1 = new Intent(this, SizeSelectActivity.class);
                startActivity(i1);
                break;
            case R.id.tutorial_button:
                Intent i2 = new Intent(this, TutorialActivity.class);
                startActivity(i2);
                break;
            case R.id.settings_button_main:
                Intent i3 = new Intent(this, SettingsActivity.class);
                startActivity(i3);
                break;
            case R.id.play_color:
                Intent i4 = new Intent(this, ColorPuzzleActivity.class);
                startActivity(i4);
                break;
            case R.id.create_own:
                Intent i5 = new Intent(this, CreateOwnActivity.class);
                startActivity(i5);
                break;
        }
    }

    /**
     * Returns the PuzzleDatabase
     *
     * @return The PuzzleDatabase
     */
    public static PuzzleDatabase getDB() {
        return db;
    }
}
