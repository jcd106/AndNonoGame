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
import android.util.Log;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateColorPuzzleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
@SuppressWarnings("ResourceType")
public class CreateColorPuzzleFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // Auto-Generated
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private CreateColorPuzzleFragment.OnFragmentInteractionListener mListener;

    int id;                 //ID of the puzzle being displayed
    int[][] currentState;   //Current state of the puzzle being displayed
    int numRows;            //Number of rows in the puzzle
    int numCols;            //Number of columns in the puzzle
    Drawable filled[];      //Color of filled puzzle boxes
    Drawable empty;         //Color of empty puzzle boxes
    Drawable markedBlank;   //Color of boxes marked Blank
    int[] size;

    int selectedColor = 1;
    int[] colors;

    private Map<Integer, Integer> colorTracker = new HashMap<Integer,Integer>();

    private Stack<TextView> prevMoves = new Stack<TextView>();  //The previous moves the user made during this display of the puzzle

    public static final String COLOR_CHOICE = "ColorChoice";    //Used access saved preference color choice

    /**
     * Default constructor for fragment
     */
    public CreateColorPuzzleFragment() {
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
        View view = inflater.inflate(R.layout.fragment_create_color_puzzle, container, false);

        //Retrieve ID of current puzzle from PuzzleActivity bundle
        Bundle bundle = this.getArguments();
        size = bundle.getIntArray("size");
        id = bundle.getInt("puzzleID");
        colors = bundle.getIntArray("colors");

        //Initialize color tracker
        for(int i = 1; i < colors.length; i++){
            colorTracker.put(colors[i], 0);
        }

        //Set drawable background for empty boxes to retrieved color choice
        empty = ContextCompat.getDrawable(this.getActivity(), R.drawable.border_button);


        //Store all of the puzzle objects information in the PuzzleFragment
        numCols = size[0];
        numRows = size[1];
        currentState = new int[numRows][numCols];


        filled = new Drawable[colors.length];
        for(int i = 0; i < colors.length; i++) {
            filled[i] = ContextCompat.getDrawable(this.getActivity(), R.drawable.border_button);
            filled[i].setColorFilter(colors[i], PorterDuff.Mode.MULTIPLY);
        }
        empty.setColorFilter(colors[0], PorterDuff.Mode.MULTIPLY);

        markedBlank = ContextCompat.getDrawable(this.getActivity(), R.drawable.blank_button);
        markedBlank.setColorFilter(colors[0], PorterDuff.Mode.MULTIPLY);

        //Create TableLayout to organize puzzle boxes into a grid
        TableLayout puzzleLayout = (TableLayout) view.findViewById(R.id.fragment_create_color_puzzle);

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
                if (boxState != selectedColor) {
                    int prevColor = colors[currentState[yLoc][xLoc]];
                    Log.d("Color: ", prevColor + "");
                    if(prevColor != Color.WHITE) {
                        int prevColorCount = colorTracker.get(prevColor);
                        colorTracker.put(prevColor, --prevColorCount);
                    }

                    int colorCount = colorTracker.get(colors[selectedColor]);
                    colorTracker.put(colors[selectedColor], ++colorCount);

                    b.setTag(R.id.state, selectedColor);
                    currentState[yLoc][xLoc] = selectedColor;
                    b.setBackground(filled[selectedColor]);
                } else {
                    int prevColor = colors[currentState[yLoc][xLoc]];
                    int prevColorCount = colorTracker.get(prevColor);
                    colorTracker.put(prevColor, --prevColorCount);

                    b.setTag(R.id.state, 0);
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

                //Display the empty boxes
                newBox.setBackground(empty);

                //Add OnClickListener to each box
                newBox.setOnClickListener(listener);
                tableRow.addView(newBox);
            }
        }


        return view;
    }

    int[][][] newRowConstraints, newColConstraints;
    ColorPuzzle newPuzzle;

    public boolean savePuzzle() {
        Context context = this.getActivity();


        //Check which colors have actually been used in the puzzle
        int colorCount = 0;
        for(int i = 1; i < colors.length; i++){
            if(colorTracker.get(colors[i]) > 0){
                colorCount++;
            }
        }

        if(colorCount < colors.length - 1){
            //Create a popup warning the user that not all of the colors have been used
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Missing Colors");
            alertDialog.setMessage("You have not used all of the selected colors");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            return false;
        }

//        int[] usedColors = new int[colorCount+1];
//        usedColors[0] = Color.WHITE;
//        int colorIndex = 1;
//        for(int i = 1; i < colors.length; i++){
//            if(colorTracker.get(colors[i]) > 0){
//                usedColors[colorIndex] = colors[i];
//                colorIndex++;
//            }
//        }

        //Checks if currentState is equal to the solutionState after each move
        if (Arrays.deepEquals(currentState, new int[numRows][numCols])) {
            //Create a popup notifying the user no changes have been made to puzzle
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
            int maxRowCs = numCols;
            int maxColCs = numRows;
            int[][][] rowConstraints = new int[numRows][maxRowCs][2];
            int[][][] colConstraints = new int[maxColCs][numCols][2];
            for(int i = 0; i < numRows; i++){
                int currentRun = 0;
                int currentColor = 0;
                int currentConstraintIndex = maxRowCs - 1;
                for(int j = numCols - 1; j >= 0; j--){
                    if(currentState[i][j] > 0 && currentRun == 0) {
                        currentRun++;
                        currentColor = currentState[i][j];
                    } else if(currentState[i][j] > 0 && currentState[i][j] == currentColor) {
                        currentRun++;
                    } else if(currentRun > 0 && currentState[i][j] == 0) {
                        rowConstraints[i][currentConstraintIndex][0] = currentRun;
                        rowConstraints[i][currentConstraintIndex][1] = currentColor;
                        currentRun = 0;
                        currentColor = 0;
                        currentConstraintIndex--;
                    } else if(currentRun > 0 && currentState[i][j] > 0) {
                        rowConstraints[i][currentConstraintIndex][0] = currentRun;
                        rowConstraints[i][currentConstraintIndex][1] = currentColor;
                        currentRun = 1;
                        currentColor = currentState[i][j];
                        currentConstraintIndex--;
                    }
                }
                if(currentRun > 0) {
                    rowConstraints[i][currentConstraintIndex][0] = currentRun;
                    rowConstraints[i][currentConstraintIndex][1] = currentColor;
                }
            }

            for(int j = 0; j < numCols; j++){
                int currentRun = 0;
                int currentColor = 0;
                int currentConstraintIndex = maxColCs - 1;
                for(int i = numRows - 1; i >= 0; i--){
                    if(currentState[i][j] > 0 && currentRun == 0) {
                        currentRun++;
                        currentColor = currentState[i][j];
                    } else if(currentState[i][j] > 0 && currentState[i][j] == currentColor) {
                        currentRun++;
                    } else if(currentRun > 0 && currentState[i][j] == 0) {
                        colConstraints[currentConstraintIndex][j][0] = currentRun;
                        colConstraints[currentConstraintIndex][j][1] = currentColor;
                        currentRun = 0;
                        currentColor = 0;
                        currentConstraintIndex--;
                    } else if(currentRun > 0 && currentState[i][j] > 0) {
                        colConstraints[currentConstraintIndex][j][0] = currentRun;
                        colConstraints[currentConstraintIndex][j][1] = currentColor;
                        currentRun = 1;
                        currentColor = currentState[i][j];
                        currentConstraintIndex--;
                    }
                }
                if(currentRun > 0) {
                    colConstraints[currentConstraintIndex][j][0] = currentRun;
                    colConstraints[currentConstraintIndex][j][1] = currentColor;
                }
            }

            int numColCs = maxColCs;
            for(int i = 0; i < maxColCs; i++){
                boolean isEmpty = true;
                for(int j = 0; j < numCols; j++){
                    if(colConstraints[i][j][0] != 0){
                        isEmpty = false;
                    }
                }
                if(isEmpty)
                    numColCs--;
                else
                    break;
            }
            newColConstraints = new int[numColCs][numCols][2];
            int colDiff = maxColCs - numColCs;
            for(int i = 0; i < numColCs; i++) {
                newColConstraints[i] = colConstraints[i+colDiff];
            }

            int numRowCs = maxRowCs;
            for(int j = 0; j < maxRowCs; j++){
                boolean isEmpty = true;
                for(int i = 0; i < numRows; i++) {
                    if (rowConstraints[i][j][0] != 0) {
                        isEmpty = false;
                    }
                }
                if(isEmpty)
                    numRowCs--;
                else
                    break;
            }
            newRowConstraints = new int[numRows][numRowCs][2];
            int rowDiff = maxRowCs - numRowCs;
            for(int i = 0; i < numRows; i++) {
                for(int j = 0; j < numRowCs; j++) {
                    newRowConstraints[i][j] = rowConstraints[i][j+rowDiff];
                }
            }

            //Get the PuzzleDatabase and update the current state of the puzzle as complete
            PuzzleDatabase db = MainActivity.getDB();

            int complete = 0;
            newPuzzle = new ColorPuzzle(id, "AddUserIDLater", size, currentState, newRowConstraints, newColConstraints, colors, complete);
            try {
                db.insertPuzzle(getString(R.string.yourColorTable),id,newPuzzle,size,complete,db.getWritableDatabase());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    public boolean saveUploadPuzzle() {
        boolean saved = savePuzzle();
        String user = MainActivity.getAccount().getId();
        final ColorPuzzleUpload pu = newPuzzle.convertToUpload(user);
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
        if (context instanceof CreateColorPuzzleFragment.OnFragmentInteractionListener) {
            mListener = (CreateColorPuzzleFragment.OnFragmentInteractionListener) context;
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

    public int[] getColors() {
        return colors;
    }

    public void setSelectedColor(int color) {
        selectedColor = color;
    }

}