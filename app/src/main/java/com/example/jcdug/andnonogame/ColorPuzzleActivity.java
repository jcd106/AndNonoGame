package com.example.jcdug.andnonogame;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ColorPuzzleActivity extends AppCompatActivity implements ColorUndoBar.OnFragmentInteractionListener, BarFragment.OnFragmentInteractionListener, BlankFragment.OnFragmentInteractionListener, ColorPuzzleFragment.OnFragmentInteractionListener, ColorSelect.OnFragmentInteractionListener {


    /**
     * Creates the view for the activity and
     * handles the creation of a new PuzzleFragment
     * and UndoBar
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_puzzle);

        //Stores the current puzzle ID
        //TODO get/send id from selection
        //Intent i = getIntent();
        //int id = Integer.parseInt(i.getStringExtra("puzzleID"));
        int id = 1;

        TextView puzzleID = (TextView) findViewById(R.id.puzzle_id_activity);
        puzzleID.setText("Puzzle: " + id);

        //Used to pass information to the new PuzzleFragment
        Bundle bundle = new Bundle();
        bundle.putInt("puzzleID", id);

        //Passes bundle to the PuzzleFragment
        ColorPuzzleFragment puzzleFragment = new ColorPuzzleFragment();
        puzzleFragment.setArguments(bundle);

        //Passes bundle to the undoBar
        ColorUndoBar undoBar = new ColorUndoBar();
        undoBar.setArguments(bundle);

        //Adds UndoBar to the activity
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_undo_bar_blank, undoBar).commit();

        //Adds new PuzzleFragment to BlankFragment
        BlankFragment bf = (BlankFragment) getSupportFragmentManager().findFragmentById(R.id.blank_fragment);
        FragmentTransaction childFT = bf.getChildFragmentManager().beginTransaction();
        childFT.add(R.id.blank_fragment, puzzleFragment, "ColorPuzzleFragment").commit();

        ColorSelect colorBar = new ColorSelect();
        colorBar.setArguments(bundle);

        BlankFragment bf2 = (BlankFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_colors);
        FragmentTransaction childFT2 = bf2.getChildFragmentManager().beginTransaction();
        childFT2.add(R.id.fragment_colors, colorBar, "ColorSelect").commit();
    }

    // Auto-generated
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * Creates the view for the activity and
     * handles the creation of a PuzzleFragment on resume
     */
    @Override
    public void onResume() {
        super.onResume();

        //Stores the current puzzle ID
        //Intent i = getIntent();
        //int id = Integer.parseInt(i.getStringExtra("puzzleID"));
        int id = 1;

        //Used to pass information to the new PuzzleFragment
        Bundle bundle = new Bundle();
        bundle.putInt("puzzleID", id);

        //Passes bundle to new PuzzleFragment
        ColorPuzzleFragment puzzleFragment = new ColorPuzzleFragment();
        puzzleFragment.setArguments(bundle);

        //Adds the new PuzzleFragment to the BlankFragment
        BlankFragment bf = (BlankFragment) getSupportFragmentManager().findFragmentById(R.id.blank_fragment);
        FragmentTransaction childFT = bf.getChildFragmentManager().beginTransaction();
        ColorPuzzleFragment pf = (ColorPuzzleFragment) bf.getChildFragmentManager().findFragmentByTag("ColorPuzzleFragment");
        childFT.detach(pf).attach(pf).remove(pf).add(R.id.blank_fragment, puzzleFragment, "ColorPuzzleFragment").commit();
    }
}
