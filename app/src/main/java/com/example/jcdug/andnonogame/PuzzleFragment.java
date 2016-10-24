package com.example.jcdug.andnonogame;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PuzzleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PuzzleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PuzzleFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    int id;
    int[][] currentState;
    int[][] solutionState;
    int complete;

    private OnFragmentInteractionListener mListener;

    public PuzzleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PuzzleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PuzzleFragment newInstance(String param1, String param2) {
        PuzzleFragment fragment = new PuzzleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_puzzle, container, false);

        Bundle bundle = this.getArguments();
        id = bundle.getInt("puzzleID");
        //int id = Integer.parseInt(args);
        //int id = 1;
        try {
            PuzzleDatabase db = MainActivity.getDB();
            Cursor c1 = db.getPuzzleByID(id);
            int p1 = c1.getColumnIndex("Puzzle");
            int row1 = c1.getColumnIndex("Rows");
            int col1 = c1.getColumnIndex("Cols");
            c1.moveToFirst();

            byte[] b = c1.getBlob(p1);
            ByteArrayInputStream bis = new ByteArrayInputStream(b);
            ObjectInput in = new ObjectInputStream(bis);
            final Puzzle p = (Puzzle) in.readObject();
            bis.close();
            in.close();

            currentState = p.getCurrentState();
            solutionState = p.getSolution();
            complete = p.isCompleted();
            int[] size = p.getSize();
            //Log.d("myTag",""+currentState[0][0]);
            int numRows = size[0];
            int numCols = size[1];

            int[][] rowVals = p.getRows();
            int[][] colVals = p.getCols();


            TableLayout puzzleLayout = (TableLayout) view.findViewById(R.id.fragment_puzzle);

            //puzzleLayout.setRowCount(numRows);
            //puzzleLayout.setColumnCount(numCols);
            final Context context = this.getActivity();
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        //Button b = (Button) view.findViewById(view.getId());
                        //PuzzleDatabase db = MainActivity.getDB();
                        TextView b = (TextView) view.findViewById(view.getId());
                        Integer buttonState = (Integer) b.getTag(R.id.state);
                        Integer x = (Integer) b.getTag(R.id.x_loc);
                        Integer y = (Integer) b.getTag(R.id.y_loc);
                        int xLoc = x.intValue();
                        int yLoc = y.intValue();
                        //int[][] currentState = p.getCurrentState();
                        //int[][] solutionState = p.getSolution();


                        if (buttonState.intValue() == 0) {
                            b.setTag(R.id.state, buttonState + 1);
                            currentState[xLoc][yLoc] = 1;
                            b.setBackgroundColor(Color.BLACK);
                        } else if (buttonState.intValue() == 1) {
                            b.setTag(R.id.state, buttonState - 1);
                            currentState[xLoc][yLoc]=0;
                            b.setBackgroundResource(R.drawable.border_button);
                            //b.setBackgroundColor(Color.WHITE);
                        }
                        if (Arrays.deepEquals(currentState, solutionState))
                        {
                            //puzzle.setCompleted(1);
                            //puzzle.setCurrentState(currentState);
                            PuzzleDatabase db = MainActivity.getDB();
                            complete = 1;
                            db.updatePuzzle(id,currentState,complete);
                            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                            alertDialog.setTitle("Congratulations!");
                            alertDialog.setMessage("You have completed the puzzle!");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which){
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                        else{
                            complete = 0;
                        }
                    }
                    catch(IOException e){

                    }
                    catch (ClassNotFoundException e){

                    }
                }
            };

            for(int i = 0; i < numRows + colVals.length; i++) {
                TableRow tableRow = new TableRow(this.getActivity());
                puzzleLayout.addView(tableRow);
                for (int j = 0; j < numCols + rowVals[0].length; j++) {
                    //TableLayout.LayoutParams params = new TableLayout.LayoutParams();
                    if (i < colVals.length && j < rowVals[0].length)
                    {
                        TextView blank = (TextView) inflater.inflate(R.layout.border_box, tableRow, false);
                        //blank.setText("11");
                        tableRow.addView(blank);
                    }
                    else if (i < colVals.length && j >= rowVals[0].length)
                    {
                        TextView columnValue = (TextView) inflater.inflate(R.layout.border_box, tableRow, false);
                        int val = colVals[i][j-rowVals[0].length];
                        if(val != 0)
                            columnValue.setText(Integer.toString(val));
                        tableRow.addView(columnValue);
                    }
                    else if (i >= colVals.length && j < rowVals[0].length)
                    {
                        TextView rowValue = (TextView) inflater.inflate(R.layout.border_box, tableRow, false);
                        int val = rowVals[i-colVals.length][j];
                        if(val != 0)
                            rowValue.setText(Integer.toString(val));
                        tableRow.addView(rowValue);
                    }
                    else if (i >= colVals.length && j >= rowVals[0].length){
                        //Button box = (Button) inflater.inflate(R.layout.my_button, tableRow, false);
                        TextView box = (TextView) inflater.inflate(R.layout.border_box, tableRow, false);
                        int boxID = Integer.parseInt(i + "" + j);
                        box.setId(boxID);
                        int x_val = i-colVals.length;
                        int y_val = j-rowVals[0].length;
                        box.setTag(R.id.x_loc, new Integer(x_val));
                        box.setTag(R.id.y_loc, new Integer(y_val));
                        box.setTag(R.id.state, new Integer(currentState[x_val][y_val]));
                        if (currentState[x_val][y_val] == 1) {
                            box.setBackgroundColor(Color.BLACK);
                        }

                        box.setOnClickListener(listener);
                        tableRow.addView(box);
                    }
                }
            }


        } catch (IOException e1) {
        }
        catch (ClassNotFoundException e2){
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroy()
    {
        PuzzleDatabase db = MainActivity.getDB();
        try {
            db.updatePuzzle(id,currentState,complete);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
