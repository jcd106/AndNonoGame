package com.example.jcdug.andnonogame;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreatePuzzleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
@SuppressWarnings("ResourceType")
public class CreatePuzzleFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // Auto-Generated
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private CreatePuzzleFragment.OnFragmentInteractionListener mListener;

    int id;                 //ID of the puzzle being displayed
    int[][] currentState;   //Current state of the puzzle being displayed
    int numRows;            //Number of rows in the puzzle
    int numCols;            //Number of columns in the puzzle
    Drawable filled;        //Color of filled puzzle boxes
    Drawable empty;         //Color of empty puzzle boxes\
    int[] size;

    private Stack<TextView> prevMoves = new Stack<TextView>();  //The previous moves the user made during this display of the puzzle

    public static final String COLOR_CHOICE = "ColorChoice";    //Used access saved preference color choice

    /**
     * Default constructor for fragment
     */
    public CreatePuzzleFragment() {
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
        View view = inflater.inflate(R.layout.fragment_create_puzzle, container, false);

        //Retrieve ID of current puzzle from PuzzleActivity bundle
        Bundle bundle = this.getArguments();
        size = bundle.getIntArray("size");
        id = bundle.getInt("puzzleID");

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


        //Store all of the puzzle objects information in the PuzzleFragment
        numCols = size[0];
        numRows = size[1];
        currentState = new int[numRows][numCols];

        //Create TableLayout to organize puzzle boxes into a grid
        TableLayout puzzleLayout = (TableLayout) view.findViewById(R.id.fragment_create_puzzle);

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
                    b.setTag(R.id.state, boxState + 1);
                    currentState[yLoc][xLoc] = 1;
                    b.setBackground(filled);
                } else if (boxState == 1) {
                    b.setTag(R.id.state, boxState - 1);
                    currentState[yLoc][xLoc] = 0;
                    b.setBackground(empty);
                }
            }
        };

        int idCount = 1;
        //Dynamically add the constraint values and puzzle boxes to the TableLayout
        for (int i = 0; i < numRows; i++) {

            //Add new row to the table
            TableRow tableRow = new TableRow(this.getActivity());
            puzzleLayout.addView(tableRow);

            for (int j = 0; j < numCols; j++) {

                //TableLayout.LayoutParams params = new TableLayout.LayoutParams();

                //Create a new blank box with a different size depending on puzzle size
                TextView newBox;
                if (numRows < 10 && numCols < 10) {
                    newBox = (TextView) inflater.inflate(R.layout.border_box_large, tableRow, false);
                } else {
                    newBox = (TextView) inflater.inflate(R.layout.border_box, tableRow, false);
                }
                //Add clickable boxes to the puzzle grid
                //Create new TextView with empty box background color and id as box position, add it to the table
                //int boxID = Integer.parseInt(i + "" + j);
                newBox.setId(idCount);
                idCount++;

                //Set x and y location and current state(filled/empty) of box in puzzle grid as a tag
                int x_val = j;
                int y_val = i;

                newBox.setTag(R.id.x_loc, new Integer(x_val));
                newBox.setTag(R.id.y_loc, new Integer(y_val));
                newBox.setTag(R.id.state, new Integer(currentState[y_val][x_val]));

                //Display correct box state form current state
                if (currentState[y_val][x_val] == 1) {
                    newBox.setBackground(filled);
                } else {
                    newBox.setBackground(empty);
                }

                //Add OnClickListener to each box
                newBox.setOnClickListener(listener);
                tableRow.addView(newBox);
            }
        }


        return view;
    }

    int[][] newRowConstraints, newColConstraints;
    Puzzle newPuzzle;

    public boolean savePuzzle() {
        Context context = this.getActivity();

        //Checks if currentState is equal to the solutionState after each move
        if (Arrays.deepEquals(currentState, new int[numRows][numCols])) {
            //Create a popup congratulating the user on puzzle completion
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Nothing to Save");
            alertDialog.setMessage("You have not made any changes to the grid");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            return false;
        } else {
            int maxRowCs = (numCols+1)/2;
            int maxColCs = (numRows+1)/2;
            int[][] rowConstraints = new int[numRows][maxRowCs];
            int[][] colConstraints = new int[maxColCs][numCols];
            for(int i = 0; i < numRows; i++){
                int currentRun = 0;
                int currentConstraintIndex = maxRowCs - 1;
                for(int j = numCols - 1; j >= 0; j--){
                    if(currentState[i][j] == 1) {
                        currentRun++;
                    } else if(currentRun > 0){
                        rowConstraints[i][currentConstraintIndex] = currentRun;
                        currentRun = 0;
                        currentConstraintIndex--;
                    }
                }
                if(currentRun > 0) {
                    rowConstraints[i][currentConstraintIndex] = currentRun;
                }
            }

            for(int j = 0; j < numCols; j++){
                int currentRun = 0;
                int currentConstraintIndex = maxColCs - 1;
                for(int i = numRows - 1; i >= 0; i--){
                    if(currentState[i][j] == 1) {
                        currentRun++;
                    } else if(currentRun > 0){
                        colConstraints[currentConstraintIndex][j] = currentRun;
                        currentRun = 0;
                        currentConstraintIndex--;
                    }
                }
                if(currentRun > 0) {
                    colConstraints[currentConstraintIndex][j] = currentRun;
                }
            }

            int numColCs = maxColCs;
            for(int i = 0; i < maxColCs; i++){
                boolean isEmpty = true;
                for(int j = 0; j < numCols; j++){
                    if(colConstraints[i][j] != 0){
                        isEmpty = false;
                    }
                }
                if(isEmpty)
                    numColCs--;
                else
                    break;
            }
            newColConstraints = new int[numColCs][numCols];
            int colDiff = maxColCs - numColCs;
            for(int i = 0; i < numColCs; i++) {
                newColConstraints[i] = colConstraints[i+colDiff];
            }

            int numRowCs = maxRowCs;
            for(int j = 0; j < maxRowCs; j++){
                boolean isEmpty = true;
                for(int i = 0; i < numRows; i++) {
                    if (rowConstraints[i][j] != 0) {
                        isEmpty = false;
                    }
                }
                if(isEmpty)
                    numRowCs--;
                else
                    break;
            }
            newRowConstraints = new int[numRows][numRowCs];
            int rowDiff = maxRowCs - numRowCs;
            for(int i = 0; i < numRows; i++) {
                for(int j = 0; j < numRowCs; j++) {
                    newRowConstraints[i][j] = rowConstraints[i][j+rowDiff];
                }
            }


            //Get the PuzzleDatabase and update the current state of the puzzle as complete
            PuzzleDatabase db = MainActivity.getDB();

            int complete = 0;
            newPuzzle = new Puzzle(id, "AddUserIDLater", size, currentState, newRowConstraints, newColConstraints, complete);
            try {
                db.insertPuzzle(getString(R.string.yourTable),id,newPuzzle,size,complete,db.getWritableDatabase());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    public boolean saveUploadPuzzle() {
        boolean saved = savePuzzle();
        String user = MainActivity.getAccount().getId();
        final PuzzleUpload pu = newPuzzle.convertToUpload(user);
        final DynamoDBMapper mapper = MainActivity.getMapper();
        new Thread(new Runnable() {
            public void run() {
                //ddbClient.listTables();
                mapper.save(pu);
            }
        }).start();
        return saved;

    }

    // Auto-generated
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CreatePuzzleFragment.OnFragmentInteractionListener) {
            mListener = (CreatePuzzleFragment.OnFragmentInteractionListener) context;
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
}