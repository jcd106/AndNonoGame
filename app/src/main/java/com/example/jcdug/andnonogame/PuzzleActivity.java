package com.example.jcdug.andnonogame;

import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

/**
 * The activity which is used to display the screen for puzzle play
 *
 * @author Josh Dughi, Peter Todorov
 * @version 1.4.3
 */
public class PuzzleActivity extends AppCompatActivity implements UndoBar.OnFragmentInteractionListener, BarFragment.OnFragmentInteractionListener, BlankFragment.OnFragmentInteractionListener, PuzzleFragment.OnFragmentInteractionListener {

    private PuzzleFragment puzzleFragment;

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
        setContentView(R.layout.activity_puzzle);

        //Stores the current puzzle ID
        Intent i = getIntent();
        int id = Integer.parseInt(i.getStringExtra("puzzleID"));
        String table = i.getStringExtra("table");

        TextView puzzleID = (TextView) findViewById(R.id.puzzle_id_activity);
        puzzleID.setText("Puzzle: " + id);

        //Used to pass information to the new PuzzleFragment
        Bundle bundle = new Bundle();
        bundle.putInt("puzzleID", id);
        bundle.putString("table", table);

        Button uploadButton = (Button) findViewById(R.id.upload_puzzle_button);
        boolean isYour = getString(R.string.yourTable).equals(table);
        if (!isYour)
            uploadButton.setVisibility(View.INVISIBLE);
        else
            uploadButton.setVisibility(View.VISIBLE);

        //Passes bundle to the PuzzleFragment
        puzzleFragment = new PuzzleFragment();
        puzzleFragment.setArguments(bundle);

        //Passes bundle to the undoBar
        UndoBar undoBar = new UndoBar();
        undoBar.setArguments(bundle);

        //Adds UndoBar to the activity
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_undo_bar_blank, undoBar).commit();

        //Adds new PuzzleFragment to BlankFragment
        BlankFragment bf = (BlankFragment) getSupportFragmentManager().findFragmentById(R.id.blank_fragment);
        FragmentTransaction childFT = bf.getChildFragmentManager().beginTransaction();
        childFT.add(R.id.blank_fragment, puzzleFragment, "PuzzleFragment").commit();
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
        Intent i = getIntent();
        int id = Integer.parseInt(i.getStringExtra("puzzleID"));
        String table = i.getStringExtra("table");

        //Used to pass information to the new PuzzleFragment
        Bundle bundle = new Bundle();
        bundle.putInt("puzzleID", id);
        bundle.putString("table", table);

        Button uploadButton = (Button) findViewById(R.id.upload_puzzle_button);
        boolean isYour = getString(R.string.yourTable).equals(table);
        if (!isYour)
            uploadButton.setVisibility(View.INVISIBLE);
        else
            uploadButton.setVisibility(View.VISIBLE);

        //Passes bundle to new PuzzleFragment
        puzzleFragment = new PuzzleFragment();
        puzzleFragment.setArguments(bundle);

        //Adds the new PuzzleFragment to the BlankFragment
        BlankFragment bf = (BlankFragment) getSupportFragmentManager().findFragmentById(R.id.blank_fragment);
        FragmentTransaction childFT = bf.getChildFragmentManager().beginTransaction();
        PuzzleFragment pf = (PuzzleFragment) bf.getChildFragmentManager().findFragmentByTag("PuzzleFragment");
        childFT.detach(pf).attach(pf).remove(pf).add(R.id.blank_fragment, puzzleFragment, "PuzzleFragment").commit();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.upload_puzzle_button:
                puzzleFragment.uploadPuzzle();
                break;
        }
    }
}
