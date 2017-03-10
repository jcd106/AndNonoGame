package com.example.jcdug.andnonogame;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

public class CreatePuzzleActivity extends AppCompatActivity implements BlankFragment.OnFragmentInteractionListener, CreatePuzzleFragment.OnFragmentInteractionListener{

    CreatePuzzleFragment createPuzzleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_puzzle);

        //Retrieve PuzzleDatabase from MainActivity
        PuzzleDatabase db = MainActivity.getDB();

        //Make a new ID
        int[] yourIDs = db.getAllPuzzleIDs(getString(R.string.yourTable));
        int id = yourIDs.length+1;

        Intent i = getIntent();
        int[] size = i.getIntArrayExtra("size");

        TextView puzzleID = (TextView) findViewById(R.id.puzzle_id_activity);
        puzzleID.setText("Puzzle: " + id);

        //Used to pass information to the new CreatePuzzleFragment
        Bundle bundle = new Bundle();
        bundle.putInt("puzzleID", id);
        bundle.putIntArray("size", size);

        //Passes bundle to the CreatePuzzleFragment
        createPuzzleFragment = new CreatePuzzleFragment();
        createPuzzleFragment.setArguments(bundle);

        //Adds new CreatePuzzleFragment to BlankFragment
        BlankFragment bf = (BlankFragment) getSupportFragmentManager().findFragmentById(R.id.blank_fragment);
        FragmentTransaction childFT = bf.getChildFragmentManager().beginTransaction();
        childFT.add(R.id.blank_fragment, createPuzzleFragment, "CreatePuzzleFragment").commit();
    }

    /**
     * Starts the correct activity depending on which button is clicked
     *
     * @param view The view that was clicked
     */
    public void onClicked(View view) {
        switch (view.getId()) {
            case R.id.back_button_create:
                onBackPressed();
                break;
            case R.id.save_puzzle_button:
                boolean isSaved = createPuzzleFragment.savePuzzle();
                if(isSaved) {
                    onBackPressed();
                }
                break;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
