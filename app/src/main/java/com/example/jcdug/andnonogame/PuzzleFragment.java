package com.example.jcdug.andnonogame;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
public class PuzzleFragment extends Fragment implements View.OnTouchListener {
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
    String table;
    float mSquareWidth;
    float mSquareHeight;
    private final int marginVertical = 180;
    private final int marginHorizontal = 34;

    private Puzzle puzzle;

    int[][] rowVals;
    int[][] colVals;

    private Stack<TextView> prevMoves = new Stack<TextView>();  //The previous moves the user made during this display of the puzzle

    public static final String COLOR_CHOICE = "ColorChoice";    //Used access saved preference color choice

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
        //getActivity().findViewById(R.id.blank_fragment).setOnTouchListener(this);

        //Retrieve ID of current puzzle from PuzzleActivity bundle
        Bundle bundle = this.getArguments();
        id = bundle.getInt("puzzleID");
        table = bundle.getString("table");

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
            Cursor c1 = db.getPuzzleByID(table, id);
            int p1 = c1.getColumnIndex("Puzzle");
            c1.moveToFirst();
            byte[] b = c1.getBlob(p1);

            //Create a new input stream and deserialize the puzzle to be displayed
            ByteArrayInputStream bis = new ByteArrayInputStream(b);
            ObjectInput in = new ObjectInputStream(bis);
            final Puzzle p = (Puzzle) in.readObject();
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

            //Create TableLayout to organize puzzle boxes into a grid
            TableLayout puzzleLayout = (TableLayout) view.findViewById(R.id.fragment_puzzle);
            //puzzleLayout.setOnTouchListener(this);

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
                    newBox = (TextView) inflater.inflate(R.layout.border_box_large, tableRow, false);

                    float boxScale = mSquareWidth / newBox.getLayoutParams().width;
                    newBox.setTextSize(newBox.getTextSize()*boxScale/scale);

                    newBox.getLayoutParams().height = (int) (mSquareHeight - 0.5);
                    newBox.getLayoutParams().width = (int) (mSquareWidth - 0.5);

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

    @Override
    public void onStart(){
        super.onStart();
        getActivity().findViewById(R.id.blank_fragment).findViewById(R.id.fragment_puzzle).setOnTouchListener(this);
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
        for(int i = 0; i < rowVals.length; i++) {
            int rowIndex = rowVals[0].length - 1;
            int currentRun = 0;

            for (int j = numCols-1; j >= 0; j--) {
                //End of current row but another run is expected
                if (j == 0 && rowIndex > 0 && rowVals[i][rowIndex-1] != 0)
                    return false;
                //New run found or continued run
                if (currentState[i][j] == 1) {
                    currentRun++;
                    //No more constraints but another run was found
                    if (rowIndex < 0)
                        return false;
                    //Continued run longer than expected constraint
                    if (currentRun > rowVals[i][rowIndex])
                        return false;
                    //End of current row but run found did not match expected constraint
                    if (j == 0 && currentRun != rowVals[i][rowIndex])
                        return false;
                }
                //No run found yet or current run has ended
                else {
                    //There are still constraints left to check
                    if(rowIndex >= 0) {
                        //End of current row but run found did not match expected constraint
                        if (j == 0 && rowVals[i][rowIndex] != currentRun)
                            return false;
                        //Another run was found
                        else if (currentRun != 0){
                            //Run matches expected constraint, move to next constraint and reset current run
                            if (currentRun == rowVals[i][rowIndex]) {
                                rowIndex--;
                                currentRun = 0;
                            }
                            //Run does not match expected constraint
                            else if (currentRun != rowVals[i][rowIndex])
                                return false;
                        }
                    }
                }
            }
        }

        //Check the column constraints
        for(int i = 0; i < colVals[0].length; i++) {
            int colIndex = colVals.length-1;
            int currentRun = 0;

            for (int j = numRows-1; j >= 0; j--) {
                //End of current column but another run is expected
                if (j == 0 && colIndex > 0 && colVals[colIndex - 1][i] != 0)
                    return false;
                //New run found or continued run
                if (currentState[j][i] == 1) {
                    currentRun++;
                    //No more constraints but another run was found
                    if (colIndex < 0)
                        return false;
                    //Continued run longer than expected constraint
                    if (currentRun > colVals[colIndex][i])
                        return false;
                    //End of current column but run found did not match expected constraint
                    if (j == 0 && currentRun != colVals[colIndex][i])
                        return false;
                }
                //No run found yet or current run has ended
                else {
                    //There are still constraints left to check
                    if (colIndex >= 0) {
                        //End of current column but run found did not match expected constraint
                        if (j == 0 && colVals[colIndex][i] != currentRun)
                            return false;
                        //Another run was found
                        else if (currentRun != 0) {
                            //Run matches expected constraint, move to next constraint and reset current run
                            if (currentRun == colVals[colIndex][i]) {
                                colIndex--;
                                currentRun = 0;
                            }
                            //Run does not match expected constraint
                            else if (currentRun != colVals[colIndex][i])
                                return false;
                        }
                    }
                }
            }
        }
        //Current state fits given constraint values
        return true;
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

        if(v.equals(this.getActivity().findViewById(R.id.fragment_puzzle)))
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

        if(v.equals(this.getActivity().findViewById(R.id.fragment_puzzle)))
            v.getLocationOnScreen(loc);

        float x = event.getRawX() + event.getX(1) + loc[0];
        float y = event.getRawY() + event.getY(1) + loc[1];
        point.set(x / 2, y / 2);

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

    public void uploadPuzzle() {
        String user = MainActivity.getAccount().getId();
        final PuzzleUpload pu = puzzle.convertToUpload(user);
        final DynamoDBMapper mapper = MainActivity.getMapper();
        final GoogleSignInAccount acct = MainActivity.getAccount();
        final AmazonDynamoDBClient ddbClient = MainActivity.getClient();

        final Map<String,AttributeValue> map = new HashMap<>();
        map.put("UserID", new AttributeValue(acct.getId()));
        //AttributeValue av = new AttributeValue();
        //av.setN(""+id);
        map.put("PuzzleID", new AttributeValue().withN(""+puzzle.getID()));
        new Thread(new Runnable() {
            public void run() {
                GetItemResult item = ddbClient.getItem("Puzzles", map);
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
        PuzzleUpload pu = puzzle.getPuzzleUpload();
        boolean added = pu.updateRatings(newRating, acct.getId());
        final PuzzleUpload p2 = pu;
        if (added) {
            new Thread(new Runnable() {
                public void run() {
                    mapper.save(p2);
                }
            }).start();
        }
    }
}