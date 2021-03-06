package com.example.jcdug.andnonogame;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

public class ColorPuzzleActivity extends AppCompatActivity implements ColorUndoBar.OnFragmentInteractionListener, BarFragment.OnFragmentInteractionListener, BlankFragment.OnFragmentInteractionListener, ColorPuzzleFragment.OnFragmentInteractionListener, ColorSelect.OnFragmentInteractionListener {

    private ColorPuzzleFragment puzzleFragment;
    protected Context context;

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
        context = this;

        //Stores the current puzzle ID
        Intent i = getIntent();
        int id = Integer.parseInt(i.getStringExtra("puzzleID"));
        String table = i.getStringExtra("table");
        String user = i.getStringExtra("user");

        TextView puzzleID = (TextView) findViewById(R.id.puzzle_id_activity);
        puzzleID.setText("Puzzle: " + id);

        //Used to pass information to the new PuzzleFragment
        Bundle bundle = new Bundle();
        bundle.putInt("puzzleID", id);
        bundle.putString("table", table);
        bundle.putString("user", user);

        Button uploadButton = (Button) findViewById(R.id.upload_puzzle_button);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        RatingBar.OnRatingBarChangeListener listener = new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(final RatingBar ratingBar, float rating, boolean fromUser) {
                if(rating == 0f)
                    return;
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Rate Puzzle?");
                alertDialog.setMessage("Would you like to rate the puzzle?");
                final float r2 = rating;
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                puzzleFragment.updateRating(r2);
                                dialog.dismiss();
                                //ratingBar.setClickable(false);
                                ratingBar.setEnabled(false);
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ratingBar.setProgress(0);
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        };
        ratingBar.setOnRatingBarChangeListener(listener);
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(ContextCompat.getColor(this, R.color.teal), PorterDuff.Mode.SRC_ATOP);
        boolean isDown = getString(R.string.downColorTable).equals(table);
        boolean isYour = getString(R.string.yourColorTable).equals(table);
        if (!isDown)
            ratingBar.setVisibility(View.INVISIBLE);
        else
            ratingBar.setVisibility(View.VISIBLE);

        if(MainActivity.getSignInStatus())
            uploadButton.setEnabled(true);
        else
            uploadButton.setEnabled(false);

        if (!isYour)
            uploadButton.setVisibility(View.INVISIBLE);
        else
            uploadButton.setVisibility(View.VISIBLE);

        //Passes bundle to the PuzzleFragment
        puzzleFragment = new ColorPuzzleFragment();
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
        Intent i = getIntent();
        int id = Integer.parseInt(i.getStringExtra("puzzleID"));
        String table = i.getStringExtra("table");
        String user = i.getStringExtra("user");

        //Used to pass information to the new PuzzleFragment
        Bundle bundle = new Bundle();
        bundle.putInt("puzzleID", id);
        bundle.putString("table", table);
        bundle.putString("user", user);

        Button uploadButton = (Button) findViewById(R.id.upload_puzzle_button);
        boolean isYour = getString(R.string.yourColorTable).equals(table);
        if (!isYour)
            uploadButton.setVisibility(View.INVISIBLE);
        else
            uploadButton.setVisibility(View.VISIBLE);

        //Passes bundle to new PuzzleFragment
        puzzleFragment = new ColorPuzzleFragment();
        puzzleFragment.setArguments(bundle);

        //Adds the new PuzzleFragment to the BlankFragment
        BlankFragment bf = (BlankFragment) getSupportFragmentManager().findFragmentById(R.id.blank_fragment);
        FragmentTransaction childFT = bf.getChildFragmentManager().beginTransaction();
        ColorPuzzleFragment pf = (ColorPuzzleFragment) bf.getChildFragmentManager().findFragmentByTag("ColorPuzzleFragment");
        childFT.detach(pf).attach(pf).remove(pf).add(R.id.blank_fragment, puzzleFragment, "ColorPuzzleFragment").commit();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.upload_puzzle_button:
                puzzleFragment.uploadPuzzle();
        }
    }
}
