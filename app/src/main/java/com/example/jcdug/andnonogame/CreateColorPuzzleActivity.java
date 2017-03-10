package com.example.jcdug.andnonogame;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class CreateColorPuzzleActivity extends AppCompatActivity implements BlankFragment.OnFragmentInteractionListener, CreateColorPuzzleFragment.OnFragmentInteractionListener, CreateColorSelect.OnFragmentInteractionListener{

    CreateColorPuzzleFragment createColorPuzzleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_color_puzzle);

        //Retrieve PuzzleDatabase from MainActivity
        PuzzleDatabase db = MainActivity.getDB();

        //Make a new ID
        int[] yourIDs = db.getAllYourPuzzleIDs();
        int id = yourIDs.length+1;

        Intent i = getIntent();
        int[] size = i.getIntArrayExtra("size");
        ArrayList<Integer> colors = i.getIntegerArrayListExtra("colors");
        int[] c = new int[colors.size()];

        for(int j = 0; j < colors.size(); j++){
            c[j] = colors.get(j);
        }

        TextView puzzleID = (TextView) findViewById(R.id.color_puzzle_id_activity);
        puzzleID.setText("Puzzle: " + id);

        //Used to pass information to the new CreatePuzzleFragment
        Bundle bundle = new Bundle();
        bundle.putInt("puzzleID", id);
        bundle.putIntArray("size", size);
        bundle.putIntArray("colors", c);

        //Passes bundle to the CreatePuzzleFragment
        createColorPuzzleFragment = new CreateColorPuzzleFragment();
        createColorPuzzleFragment.setArguments(bundle);

        //Adds new CreatePuzzleFragment to BlankFragment
        BlankFragment bf = (BlankFragment) getSupportFragmentManager().findFragmentById(R.id.blank_fragment);
        FragmentTransaction childFT = bf.getChildFragmentManager().beginTransaction();
        childFT.add(R.id.blank_fragment, createColorPuzzleFragment, "CreateColorPuzzleFragment").commit();

        CreateColorSelect colorBar = new CreateColorSelect();
        colorBar.setArguments(bundle);

        BlankFragment bf2 = (BlankFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_create_colors);
        FragmentTransaction childFT2 = bf2.getChildFragmentManager().beginTransaction();
        childFT2.add(R.id.fragment_create_colors, colorBar, "CreateColorSelect").commit();
    }

    /**
     * Starts the correct activity depending on which button is clicked
     *
     * @param view The view that was clicked
     */
    public void onClicked(View view) {
        switch (view.getId()) {
            case R.id.back_button_create_color:
                onBackPressed();
                break;
            case R.id.save_color_puzzle_button:
                boolean isSaved = createColorPuzzleFragment.savePuzzle();
                if(isSaved) {
                    Intent i = new Intent(this, CreateOwnActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                break;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
