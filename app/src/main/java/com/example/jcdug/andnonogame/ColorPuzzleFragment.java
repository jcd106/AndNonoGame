package com.example.jcdug.andnonogame;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


public class ColorPuzzleFragment extends Fragment implements View.OnTouchListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // Auto-Generated
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private ColorPuzzleFragment.OnFragmentInteractionListener mListener;

    int id;                 //ID of the puzzle being displayed
    int[][] currentState;   //Current state of the puzzle being displayed
    int[][] solutionState;  //Solution state of the puzzle being displayed
    int numRows;            //Number of rows in the puzzle
    int numCols;            //Number of columns in the puzzle
    int complete;           //Store whether puzzle has been completed
    int[] colors;           //Store the colors of the puzzle
    Drawable filled[];      //Color of filled puzzle boxes
    Drawable empty;         //Color of empty puzzle boxes
    Drawable markedBlank;   //Color of boxes marked Blank
    int selectedColor;
    String table;
    float mSquareWidth;
    float mSquareHeight;
    private final int marginVertical = 210;
    private final int marginHorizontal = 34;

    private ColorPuzzle puzzle;

    int[][][] rowVals;
    int[][][] colVals;

    private Stack<TextView> prevMoves = new Stack<TextView>();  //The previous moves the user made during this display of the puzzle
    private Stack<Integer> prevColors = new Stack<Integer>();

    private static final String TAG = "Touch";

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    /**
     * Default constructor for fragment
     */
    public ColorPuzzleFragment() {
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
        View view = inflater.inflate(R.layout.fragment_color_puzzle, container, false);
        selectedColor = 1;

        //Retrieve ID of current puzzle from PuzzleActivity bundle
        Bundle bundle = this.getArguments();
        id = bundle.getInt("puzzleID");
        table = bundle.getString("table");

        //filled.setColorFilter(fillColor, PorterDuff.Mode.MULTIPLY);
        empty = ContextCompat.getDrawable(this.getActivity(), R.drawable.border_button);
        //empty.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

        try {
            //Retrieve PuzzleDatabase from MainActivity
            PuzzleDatabase db = MainActivity.getDB();

            //Get the correct serialized puzzle by its ID from the PuzzleDatabase
            Cursor c1 = db.getPuzzleByID(table,id);
            int p1 = c1.getColumnIndex("Puzzle");
            c1.moveToFirst();
            byte[] b = c1.getBlob(p1);

            //Create a new input stream and deserialize the puzzle to be displayed
            ByteArrayInputStream bis = new ByteArrayInputStream(b);
            ObjectInput in = new ObjectInputStream(bis);
            final ColorPuzzle p = (ColorPuzzle) in.readObject();
            bis.close();
            in.close();
            puzzle = p;

            //Store all of the puzzle objects information in the PuzzleFragment
            currentState = p.getCurrentState();
            solutionState = p.getSolution();
            complete = p.isCompleted();
            int[] size = p.getSize();
            numCols = size[0];
            numRows = size[1];
            rowVals = p.getRows();
            colVals = p.getCols();
            colors = p.getColors();

            final float scale = Resources.getSystem().getDisplayMetrics().density;
            int viewW = Resources.getSystem().getDisplayMetrics().widthPixels - ((int) (marginHorizontal * scale));
            int viewH = Resources.getSystem().getDisplayMetrics().heightPixels - ((int) (marginVertical * scale));

            // Set width and height to be used for the squares.
            mSquareWidth = viewW / (float) (numCols + rowVals[0].length);
            mSquareHeight = viewH / (float) (numRows + colVals.length);

            if (mSquareHeight > mSquareWidth)
                mSquareHeight = mSquareWidth;
            else
                mSquareWidth = mSquareHeight;

            //Set drawable background for filled and empty boxes to retrieved color choice
            filled = new Drawable[colors.length];
            for(int i = 0; i < colors.length; i++) {
                filled[i] = ContextCompat.getDrawable(this.getActivity(), R.drawable.border_button);
                filled[i].setColorFilter(colors[i], PorterDuff.Mode.MULTIPLY);
                //filled[i].setColorFilter(ContextCompat.getColor(this.getContext(), R.color.red), PorterDuff.Mode.MULTIPLY);
            }
            empty.setColorFilter(colors[0], PorterDuff.Mode.MULTIPLY);

            markedBlank = ContextCompat.getDrawable(this.getActivity(), R.drawable.blank_button);
            markedBlank.setColorFilter(colors[0], PorterDuff.Mode.MULTIPLY);

            //Create TableLayout to organize puzzle boxes into a grid
            TableLayout puzzleLayout = (TableLayout) view.findViewById(R.id.fragment_color_puzzle);

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
                    Drawable back = b.getBackground();
                    if (back.equals(markedBlank))
                        prevColors.push(-1);
                    else {
                        for (int i = 0; i < colors.length; i++) {
                            if(back.equals(filled[i]))
                                prevColors.push(i);
                        }
                    }

                    //Handles switching of box colors and updates puzzle's current state
                    if (boxState == -1) {
                        b.setTag(R.id.state, 0);
                        currentState[yLoc][xLoc] = 0;
                        b.setBackground(empty);
                    }
                    else if (boxState != selectedColor) {
                        b.setTag(R.id.state, selectedColor);
                        currentState[yLoc][xLoc] = selectedColor;
                        b.setBackground(filled[selectedColor]);
                    } else {
                        b.setTag(R.id.state, -1);
                        currentState[yLoc][xLoc] = -1;
                        b.setBackground(markedBlank);
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

                    //TableLayout.LayoutParams params = new TableLayout.LayoutParams(mSquareWidthDP, mSquareHeightDP);

                    //Create a new blank box with a different size depending on puzzle size
                    TextView newBox;
                    newBox = (TextView) inflater.inflate(R.layout.border_box_large, tableRow, false);

                    float boxScale = mSquareWidth / newBox.getLayoutParams().width;
                    newBox.setTextSize(newBox.getTextSize()*boxScale/scale);

                    newBox.getLayoutParams().height = (int) (mSquareHeight - 0.5);
                    newBox.getLayoutParams().width = (int) (mSquareWidth - 0.5);

                    //Add blank spaces in top right of puzzle grid to leave room for constraint values
                    if (i < colVals.length && j < rowVals[0].length) {
                        //Create new TextView with empty box background color and add it to the table
                        newBox.setBackground(filled[0]);
                        tableRow.addView(newBox);
                    }

                    //Add column constraint values to the puzzle grid
                    else if (i < colVals.length && j >= rowVals[0].length) {
                        //Make TextView with empty box background color and add it to the table
                        newBox.setBackground(filled[colVals[i][j-rowVals[0].length][1]]);

                        //Retrieve correct column constraint value and set TextView's text
                        int val = colVals[i][j - rowVals[0].length][0];
                        if (val != 0)
                            newBox.setText(Integer.toString(val));
                        tableRow.addView(newBox);
                    }

                    //Add row constraint values to the puzzle gird
                    else if (i >= colVals.length && j < rowVals[0].length) {
                        //Make TextView with empty box background color and add it to the table
                        newBox.setBackground(filled[rowVals[i-colVals.length][j][1]]);

                        //Retrieve correct row constraint value and set TextView's text
                        int val = rowVals[i - colVals.length][j][0];
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
                        if (currentState[y_val][x_val] > 0) {
                            newBox.setBackground(filled[currentState[y_val][x_val]]);
                        } else if (currentState[y_val][x_val] == -1){
                            newBox.setBackground(markedBlank);
                        } else {
                            newBox.setBackground(filled[0]);
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

    @Override
    public void onStart(){
        super.onStart();
        getActivity().findViewById(R.id.blank_fragment).findViewById(R.id.fragment_color_puzzle).setOnTouchListener(this);
    }


    /**
     * Checks if the puzzle is solved
     */
    private void checkIfSolved() {
        Context context = this.getActivity();

        //Checks if currentState is equal to the solutionState after each move
        if (validSolutionFound()) {
            //Get the PuzzleDatabase and update the current state of the puzzle as complete
            PuzzleDatabase db = MainActivity.getDB();
            complete = 1;
            try {
                db.updatePuzzle(table, id, currentState, complete);
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


    private boolean validSolutionFound() {

        //Check the row constraints
        for (int i = 0; i < rowVals.length; i++) {
            int rowIndex = rowVals[0].length - 1;
            int currentRun = 0;
            int currentRunColor = 0;
            for (int j = numCols - 1; j >= 0; j--) {
                //There is currently no run
                if (currentRunColor <= 0) {
                    //If the current box is filled, update the current run values
                    if (currentState[i][j] > 0) {
                        currentRun = 1;
                        currentRunColor = currentState[i][j];
                    }
                }
                //Otherwise there is currently a run
                else {
                    //The run is continuing
                    if (currentRunColor == currentState[i][j])
                        currentRun++;
                    //The current run is over
                    else {
                        //Check if the current run is longer than the current row constraint
                        if (currentRun > rowVals[i][rowIndex][0])
                            return false;
                        //Check if the current run is correct and update row index and the current run values
                        if (currentRun == rowVals[i][rowIndex][0] && currentRunColor == rowVals[i][rowIndex][1]) {
                            rowIndex--;
                            if (currentState[i][j] > 0) {
                                currentRun = 1;
                                currentRunColor = currentState[i][j];
                            } else {
                                currentRun = 0;
                                currentRunColor = 0;
                            }
                        }
                    }
                }

                //At the end of current row
                if (j == 0) {
                    //Check if there should be another run
                    if (rowIndex >= 0 && rowVals[i][rowIndex][0] != 0) {
                        //Check if the current run matches the current row constraint
                        if (currentRun == rowVals[i][rowIndex][0] && currentRunColor == rowVals[i][rowIndex][1]) {
                            rowIndex--;
                            //If there should be another run, then there are too few runs for the row
                            if (rowIndex >= 0 && rowVals[i][rowIndex][0] != 0)
                                return false;
                        }
                        //The current run does not match the current row constraint
                        else
                            return false;
                    } else {
                        //Check if the current run is too long
                        if (rowIndex >= 0 && currentRun > rowVals[i][rowIndex][0])
                            return false;
                    }
                }
            }
        }

        //Check the column constraints
        for (int i = 0; i < colVals[0].length; i++) {
            int colIndex = colVals.length - 1;
            int currentRun = 0;
            int currentRunColor = 0;
            for (int j = numRows - 1; j >= 0; j--) {
                //There is currently no run
                if (currentRunColor <= 0) {
                    //If the current box is filled, update the current run values
                    if (currentState[j][i] > 0) {
                        currentRun = 1;
                        currentRunColor = currentState[j][i];
                    }
                }
                //Otherwise there is currently a run
                else {
                    //The run is continuing
                    if (currentRunColor == currentState[j][i])
                        currentRun++;
                    //The current run is over
                    else {
                        //Check if the current run is longer than the current column constraint
                        if (currentRun > colVals[colIndex][i][0])
                            return false;
                        //Check if the current run is correct and update column index and the current run values
                        if (currentRun == colVals[colIndex][i][0] && currentRunColor == colVals[colIndex][i][1]) {
                            colIndex--;
                            if (currentState[j][i] > 0) {
                                currentRun = 1;
                                currentRunColor = currentState[j][i];
                            } else {
                                currentRun = 0;
                                currentRunColor = 0;
                            }
                        }
                    }
                }

                //At the end of current column
                if (j == 0) {
                    //Check if there should be another run
                    if (colIndex >= 0 && colVals[colIndex][i][0] != 0) {
                        //Check if the current run matches the current column constraint
                        if (currentRun == colVals[colIndex][i][0] && currentRunColor == colVals[colIndex][i][1]) {
                            colIndex--;
                            //If there should be another run, then there are too few runs for the column
                            if (colIndex >= 0 && colVals[colIndex][i][0] != 0)
                                return false;
                        }
                        //The current run does not match the current column constraint
                        else
                            return false;
                    } else {
                        //Check if the current run is too long
                        if (colIndex >= 0 && currentRun > colVals[colIndex][i][0])
                            return false;
                    }
                }
            }
        }

        return true;
    }


    // Undoes the most recent move made by the user
    public void undoMostRecent() {
        if (!prevMoves.isEmpty()) {
            TextView prev = prevMoves.pop();
            int prevColor = prevColors.pop();
            Integer x_loc = (Integer) prev.getTag(R.id.x_loc);
            Integer y_loc = (Integer) prev.getTag(R.id.y_loc);
            //Integer box_state = (Integer) prev.getTag(R.id.state);

            currentState[y_loc][x_loc] = prevColor;
            prev.setTag(R.id.state, prevColor);
            if (prevColor == -1) {
                prev.setBackground(markedBlank);
            }
            else
                prev.setBackground(filled[prevColor]);

            // Checks if the puzzle is solved
            checkIfSolved();
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        View view = v;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                start.set(event.getRawX(), event.getRawY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = fingerSpacing(v, event);
                if (oldDist > 200f) {
                    midPoint(v, mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    int loc[] = {0, 0};
                    view.getLocationOnScreen(loc);
                    view.setX(view.getX() + event.getRawX() - start.x);
                    view.setY(view.getY() + event.getRawY() - start.y);
                    start.set(event.getRawX(), event.getRawY());
                }
                else if (mode == ZOOM) {
                    float newDist = fingerSpacing(v, event);
                    if (newDist > 200f) {

                        float scale = newDist / oldDist;
                        view.setPivotX(mid.x);
                        view.setPivotY(mid.y);
                        view.setScaleX(scale);
                        view.setScaleY(scale);
                    }
                }
                break;
        }

        return true;

    }

    private float fingerSpacing(View v, MotionEvent event) {

        int loc[] = {0, 0};

        if(v.equals(this.getActivity().findViewById(R.id.fragment_color_puzzle)))
            v.getLocationOnScreen(loc);

        float x0 = event.getRawX();
        float x1 = event.getX(1) + loc[0];
        float x = x0 - x1;

        float y0 = event.getRawY();
        float y1 = event.getY(1) + loc[1];
        float y = y0 - y1;
        float dist = (float) (Math.sqrt(x*x + y*y));

        return (float) (Math.sqrt(x * x + y * y));
    }

    private void midPoint(View v, PointF point, MotionEvent event) {

        int loc[] = {0, 0};

        if(v.equals(this.getActivity().findViewById(R.id.fragment_color_puzzle)))
            v.getLocationOnScreen(loc);

        float x = event.getRawX() + event.getX(1) + loc[0];
        float y = event.getRawY() + event.getY(1) + loc[1];
        point.set(x / 2, y / 2);

    }


    // Auto-generated
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ColorPuzzleFragment.OnFragmentInteractionListener) {
            mListener = (ColorPuzzleFragment.OnFragmentInteractionListener) context;
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
            db.updatePuzzle(table, id, currentState, complete);
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
            db.updatePuzzle(table, id, currentState, complete);
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

    public void resetPuzzle(PuzzleDatabase db) {
        try {
            db.resetDownPuzzle(table, id, puzzle.getUser());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int[] getColors() {
        return colors;
    }

    public void setSelectedColor(int color) {
        selectedColor = color;
    }

    public void uploadPuzzle() {
        String user = MainActivity.getAccount().getId();
        final ColorPuzzleUpload pu = puzzle.convertToUpload(user);
        final DynamoDBMapper mapper = MainActivity.getMapper();
        final GoogleSignInAccount acct = MainActivity.getAccount();
        final AmazonDynamoDBClient ddbClient = MainActivity.getClient();

        final Map<String,AttributeValue> map = new HashMap<>();
        map.put("UserID", new AttributeValue(acct.getId()));
        map.put("PuzzleID", new AttributeValue().withN(""+puzzle.getID()));
        new Thread(new Runnable() {
            public void run() {
                GetItemResult item = ddbClient.getItem("ColorPuzzles", map);
                if (item.getItem() == null){
                    mapper.save(pu);
                }
            }
        }).start();
    }

    public void updateRating(float newRating) {
        final DynamoDBMapper mapper = MainActivity.getMapper();
        final GoogleSignInAccount acct = MainActivity.getAccount();
        final AmazonDynamoDBClient ddbClient = MainActivity.getClient();
        ColorPuzzleUpload pu = puzzle.getPuzzleUpload();
        boolean added = pu.updateRatings(newRating, acct.getId());
        final ColorPuzzleUpload p2 = pu;
        if (added) {
            new Thread(new Runnable() {
                public void run() {
                    mapper.save(p2);
                }
            }).start();
        }
    }
}
