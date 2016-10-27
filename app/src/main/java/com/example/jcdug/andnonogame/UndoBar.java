package com.example.jcdug.andnonogame;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UndoBar.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * <p>
 * Handles the display and functionality of the undo bar
 *
 * @author Josh Dughi, Peter Todorov
 * @version 1.4.3
 */
public class UndoBar extends Fragment implements View.OnClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    int id;

    private OnFragmentInteractionListener mListener;

    public UndoBar() {
        // Required empty public constructor
    }

    // Auto-Generated
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * Adds reset and undo buttons to the view of the undo bar
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        id = bundle.getInt("puzzleID");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_undo_bar, container, false);
        ImageButton undoButton = (ImageButton) view.findViewById(R.id.undo_button);
        undoButton.setOnClickListener(this);
        ImageButton resetButton = (ImageButton) view.findViewById(R.id.reset_puzzle_button);
        resetButton.setOnClickListener(this);
        return view;
    }

    // Auto-Generated
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    // Auto-Generated
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Sets the functionality for when the buttons are clicked
     * The undo button undoes the previous move
     * The reset button resets the puzzle to its blank state
     *
     * @param v The view that was clicked
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //Reset button was clicked
            case R.id.reset_puzzle_button:
                //Create a prompt asking the user if he/she is sure
                AlertDialog alertDialog = new AlertDialog.Builder(this.getActivity()).create();
                alertDialog.setTitle("Reset Puzzle!");
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
                                try {
                                    //Reset the current puzzle in the PuzzleDatabase
                                    PuzzleDatabase db = MainActivity.getDB();
                                    db.resetPuzzle(id);

                                    //Redraw the PuzzleFragment with the new reset state
                                    BlankFragment bf = (BlankFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.blank_fragment);
                                    PuzzleFragment puzzleFragment = (PuzzleFragment) bf.getChildFragmentManager().findFragmentByTag("PuzzleFragment");
                                    puzzleFragment.resetCurrentState();
                                    bf.getChildFragmentManager().beginTransaction().detach(puzzleFragment).attach(puzzleFragment).commit();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                break;

            //Undo button was clicked
            case R.id.undo_button:
                BlankFragment bf = (BlankFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.blank_fragment);
                PuzzleFragment puzzleFragment = (PuzzleFragment) bf.getChildFragmentManager().findFragmentByTag("PuzzleFragment");
                puzzleFragment.undoMostRecent();
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
