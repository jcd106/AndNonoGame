package com.example.jcdug.andnonogame;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Queue;
import java.util.Stack;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PuzzleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * <p>
 * Class that handles puzzle display in the PuzzleActivity
 *
 * @author Josh Dughi, Peter Todorov
 * @version 1.4.3
 */
public class PuzzleFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // Auto-Generated
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    int id;                 //ID of the puzzle being displayed
    int[][] currentState;   //Current state of the puzzle being displayed
    int[][] solutionState;  //Solution state of the puzzle being displayed
    int numRows;            //Number of rows in the puzzle
    int numCols;            //Number of columns in the puzzle
    int complete;           //Store whether puzzle has been completed
    Drawable filled;        //Color of filled puzzle boxes
    Drawable empty;         //Color of empty puzzle boxes
    Drawable markedBlank;   //Color of boxes marked Blank
    int isYourPuzzle;

    private Stack<TextView> prevMoves = new Stack<TextView>();  //The previous moves the user made during this display of the puzzle

    public static final String COLOR_CHOICE = "ColorChoice";    //Used access saved preference color choice

    /**
     * Default constructor for fragment
     */
    public PuzzleFragment() {
        // Required empty public constructor
    }

    /**
     * Handles operations on fragment creation
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    /**
     * Handle drawing of puzzle as well as initialiation of puzzle boxes
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_puzzle, container, false);

        //Retrieve ID of current puzzle from PuzzleActivity bundle
        Bundle bundle = this.getArguments();
        id = bundle.getInt("puzzleID");
        isYourPuzzle = bundle.getInt("your");

        //Retrieve user color choice as string from shared preferences
        SharedPreferences preferences = this.getActivity().getSharedPreferences(COLOR_CHOICE, Context.MODE_PRIVATE);
        String emptyChoice = preferences.getString("empty", null);
        String filledChoice = preferences.getString("filled", null);
        if (emptyChoice == null) {
            emptyChoice = "WHITE";
        }
        if (filledChoice == null) {
            filledChoice = "BLACK";
        }


        //Convert color string to int color value
        int emptyColor, fillColor;
        switch (emptyChoice) {
            case "WHITE":
                emptyColor = Color.WHITE;
                break;
            case "BLUE":
                emptyColor = Color.BLUE;
                break;
            default:
                emptyColor = Color.WHITE;
        }

        switch (filledChoice) {
            case "BLACK":
                fillColor = Color.DKGRAY;
                break;
            case "RED":
                fillColor = Color.RED;
                break;
            default:
                fillColor = Color.DKGRAY;
        }

        //Set drawable background for filled and empty boxes to retrieved color choice
        filled = ContextCompat.getDrawable(this.getActivity(), R.drawable.border_button);
        filled.setColorFilter(fillColor, PorterDuff.Mode.MULTIPLY);
        empty = ContextCompat.getDrawable(this.getActivity(), R.drawable.border_button);
        empty.setColorFilter(emptyColor, PorterDuff.Mode.MULTIPLY);
        markedBlank = ContextCompat.getDrawable(this.getActivity(), R.drawable.blank_button);
        markedBlank.setColorFilter(emptyColor, PorterDuff.Mode.MULTIPLY);

        try {
            //Retrieve PuzzleDatabase from MainActivity
            PuzzleDatabase db = MainActivity.getDB();

            //Get the correct serialized puzzle by its ID from the PuzzleDatabase
            Cursor c1;
            if(isYourPuzzle == 1) {
                c1 = db.getYourPuzzleByID(id);
            } else {
                c1 = db.getPuzzleByID(id);
            }
            int p1 = c1.getColumnIndex("Puzzle");
            c1.moveToFirst();
            byte[] b = c1.getBlob(p1);

            //Create a new input stream and deserialize the puzzle to be displayed
            ByteArrayInputStream bis = new ByteArrayInputStream(b);
            ObjectInput in = new ObjectInputStream(bis);
            final Puzzle p = (Puzzle) in.readObject();
            bis.close();
            in.close();

            //Store all of the puzzle objects information in the PuzzleFragment
            currentState = p.getCurrentState();
            solutionState = p.getSolution();
            complete = p.isCompleted();
            int[] size = p.getSize();
            numCols = size[0];
            numRows = size[1];
            int[][] rowVals = p.getRows();
            int[][] colVals = p.getCols();

            //Create TableLayout to organize puzzle boxes into a grid
            TableLayout puzzleLayout = (TableLayout) view.findViewById(R.id.fragment_puzzle);

            //Create an OnClickListener for each box in the puzzle
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    //Retrieve clicked puzzle box and its stored tag values
                    TextView b = (TextView) view.findViewById(view.getId());
                    Integer boxState = (Integer) b.getTag(R.id.state);
                    Integer x = (Integer) b.getTag(R.id.x_loc);
                    Integer y = (Integer) b.getTag(R.id.y_loc);
                    int xLoc = x;
                    int yLoc = y;

                    prevMoves.push(b);


                    //Handles switching of box colors and updates puzzle's current state
                    if (boxState == 0) {
                        b.setTag(R.id.state, 1);
                        currentState[yLoc][xLoc] = 1;
                        b.setBackground(filled);
                    } else if (boxState == 1) {
                        b.setTag(R.id.state, -1);
                        currentState[yLoc][xLoc] = -1;
                        b.setBackground(markedBlank);
                    } else if (boxState == -1) {
                        b.setTag(R.id.state, 0);
                        currentState[yLoc][xLoc] = 0;
                        b.setBackground(empty);
                    }

                    //Checks if the puzzle is solved
                    checkIfSolved();


                }
            };

            int idCount = 1;
            //Dynamically add the constraint values and puzzle boxes to the TableLayout
            for (int i = 0; i < numRows + colVals.length; i++) {

                //Add new row to the table
                TableRow tableRow = new TableRow(this.getActivity());
                puzzleLayout.addView(tableRow);

                for (int j = 0; j < numCols + rowVals[0].length; j++) {

                    //TableLayout.LayoutParams params = new TableLayout.LayoutParams();

                    //Create a new blank box with a different size depending on puzzle size
                    TextView newBox;
                    if (numRows < 10 && numCols < 10) {
                        newBox = (TextView) inflater.inflate(R.layout.border_box_large, tableRow, false);
                    } else {
                        newBox = (TextView) inflater.inflate(R.layout.border_box, tableRow, false);
                    }
                    //Add blank spaces in top right of puzzle grid to leave room for constraint values
                    if (i < colVals.length && j < rowVals[0].length) {
                        //Create new TextView with empty box background color and add it to the table
                        newBox.setBackground(empty);
                        tableRow.addView(newBox);
                    }

                    //Add column constraint values to the puzzle grid
                    else if (i < colVals.length && j >= rowVals[0].length) {
                        //Make TextView with empty box background color and add it to the table
                        newBox.setBackground(empty);

                        //Retrieve correct column constraint value and set TextView's text
                        int val = colVals[i][j - rowVals[0].length];
                        if (val != 0)
                            newBox.setText(Integer.toString(val));
                        tableRow.addView(newBox);
                    }

                    //Add row constraint values to the puzzle gird
                    else if (i >= colVals.length && j < rowVals[0].length) {
                        //Make TextView with empty box background color and add it to the table
                        newBox.setBackground(empty);

                        //Retrieve correct row constraint value and set TextView's text
                        int val = rowVals[i - colVals.length][j];
                        if (val != 0)
                            newBox.setText(Integer.toString(val));
                        tableRow.addView(newBox);
                    }

                    //Add clickable boxes to the puzzle grid
                    else if (i >= colVals.length && j >= rowVals[0].length) {

                        //Create new TextView with empty box background color and id as box position, add it to the table
                        //int boxID = Integer.parseInt(i + "" + j);
                        newBox.setId(idCount);
                        idCount++;

                        //Set x and y location and current state(filled/empty) of box in puzzle grid as a tag
                        int x_val = j - rowVals[0].length;
                        int y_val = i - colVals.length;

                        newBox.setTag(R.id.x_loc, new Integer(x_val));
                        newBox.setTag(R.id.y_loc, new Integer(y_val));
                        newBox.setTag(R.id.state, new Integer(currentState[y_val][x_val]));

                        //Display correct box state form current state
                        if (currentState[y_val][x_val] == 1) {
                            newBox.setBackground(filled);
                        } else if (currentState[y_val][x_val] == -1){
                            newBox.setBackground(markedBlank);
                        } else {
                            newBox.setBackground(empty);
                        }

                        //Add OnClickListener to each box
                        newBox.setOnClickListener(listener);
                        tableRow.addView(newBox);
                    }
                }
            }


        } catch (IOException e1) {
        } catch (ClassNotFoundException e2) {
        }
        return view;
    }

    /**
     * Checks if the puzzle is solved
     */
    private void checkIfSolved() {
        Context context = this.getActivity();

        //Checks if currentState is equal to the solutionState after each move
        if (Arrays.deepEquals(currentState, solutionState)) {
            //Get the PuzzleDatabase and update the current state of the puzzle as complete
            PuzzleDatabase db = MainActivity.getDB();
            complete = 1;
            try {
                if(isYourPuzzle == 1) {
                    db.updateYourPuzzle(id, currentState, complete);
                } else {
                    db.updatePuzzle(id, currentState, complete);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            //Create a popup congratulating the user on puzzle completion
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Congratulations!");
            alertDialog.setMessage("You have completed the puzzle!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } else {
            complete = 0;
        }
    }

    // Undoes the most recent move made by the user
    public void undoMostRecent() {
        if (!prevMoves.isEmpty()) {
            TextView prev = prevMoves.pop();
            Integer x_loc = (Integer) prev.getTag(R.id.x_loc);
            Integer y_loc = (Integer) prev.getTag(R.id.y_loc);
            Integer box_state = (Integer) prev.getTag(R.id.state);

            if (box_state == 0) {
                currentState[y_loc][x_loc] = -1;
                prev.setTag(R.id.state, -1);
                prev.setBackground(markedBlank);
            } else if (box_state == 1) {
                currentState[y_loc][x_loc] = 0;
                prev.setTag(R.id.state, 0);
                prev.setBackground(empty);
            } else if (box_state == -1) {
                currentState[y_loc][x_loc] = 1;
                prev.setTag(R.id.state, 1);
                prev.setBackground(filled);
            }

            // Checks if the puzzle is solved
            checkIfSolved();
        }
    }

    // Auto-generated
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

    // Auto-generated
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    /**
     * Saves the puzzle's current state when the PuzzleFragment is destroyed
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        PuzzleDatabase db = MainActivity.getDB();
        try {
            if(isYourPuzzle == 1) {
                db.updateYourPuzzle(id, currentState, complete);
            } else {
                db.updatePuzzle(id, currentState, complete);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the puzzle's current state when the PuzzleFragment is paused
     */
    @Override
    public void onPause() {
        super.onPause();
        PuzzleDatabase db = MainActivity.getDB();
        try {
            if(isYourPuzzle == 1) {
                db.updateYourPuzzle(id, currentState, complete);
            } else {
                db.updatePuzzle(id, currentState, complete);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Resets the puzzle's current state in the fragment
     */
    public void resetCurrentState() {
        currentState = new int[numRows][numCols];
        complete = 0;
    }

}
