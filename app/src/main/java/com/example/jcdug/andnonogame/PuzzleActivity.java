package com.example.jcdug.andnonogame;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class PuzzleActivity extends AppCompatActivity implements UndoBar.OnFragmentInteractionListener, BarFragment.OnFragmentInteractionListener, BlankFragment.OnFragmentInteractionListener, PuzzleFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
//        TextView puzzleID = (TextView) findViewById(R.id.puzzle_id);
        Intent i = getIntent();
        int id = Integer.parseInt(i.getStringExtra("puzzleID"));
        //puzzleID.setText("Puzzle:  "+id);

        Bundle bundle = new Bundle();
        bundle.putInt("puzzleID", id);
        PuzzleFragment puzzleFragment = new PuzzleFragment();
        puzzleFragment.setArguments(bundle);
        UndoBar undoBar = new UndoBar();
        undoBar.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.blank_fragment, puzzleFragment);
        ft.add(R.id.fragment_undo_bar_blank, undoBar).commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onResume()
    {
        super.onResume();
        Intent i = getIntent();
        int id = Integer.parseInt(i.getStringExtra("puzzleID"));
        //puzzleID.setText("Puzzle:  "+id);

        Bundle bundle = new Bundle();
        bundle.putInt("puzzleID", id);
        PuzzleFragment puzzleFragment = new PuzzleFragment();
        puzzleFragment.setArguments(bundle);
        UndoBar undoBar = new UndoBar();
        undoBar.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.blank_fragment, puzzleFragment);
        ft.add(R.id.fragment_undo_bar_blank, undoBar).commit();
    }
}
