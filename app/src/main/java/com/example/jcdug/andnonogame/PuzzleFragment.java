package com.example.jcdug.andnonogame;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PuzzleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PuzzleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PuzzleFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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

        //Bundle bundle = this.getArguments();
        //int id = bundle.getInt("puzzleID");
        //int id = Integer.parseInt(args);
        int id = 1;
        try {
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
                Puzzle p = (Puzzle) in.readObject();
                bis.close();
                in.close();

                int[][] currentState = p.getCurrentState();
                int[] size = p.getSize();
                Log.d("myTag",""+currentState[0][0]);
                int numRows = size[0];
                int numCols = size[1];

                GridLayout puzzleLayout = (GridLayout) view.findViewById(R.id.fragment_puzzle);
                puzzleLayout.setRowCount(numRows);
                puzzleLayout.setColumnCount(numCols);
                final Context context = this.getActivity();
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Button b = (Button) view.findViewById(view.getId());
                        Integer buttonState = (Integer) b.getTag(R.id.state);

                        if(buttonState.intValue() == 0){
                            b.setTag(R.id.state, buttonState+1);
                            b.setBackgroundColor(Color.BLACK);
                        }
                        else if(buttonState.intValue() == 1){
                            b.setTag(R.id.state,buttonState-1);
                            b.setBackgroundResource(R.drawable.border_button);
                            //b.setBackgroundColor(Color.WHITE);
                        }

                    }
                };
                for(int i = 0; i < numRows; i++) {
                    for (int j = 0; j < numCols; j++) {
                        //GridLayout.Spec ro = GridLayout.spec(0,5);
                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        //params.setMargins(5, 5, 5, 5);
                        Button box = new Button(context);
                        //box.setLayoutParams(new GridLayout.LayoutParams(l));
                        //box.setHeight(30);
                        //box.setWidth(30);
                        int boxID = Integer.parseInt(i + "" + j);
                        box.setId(boxID);
                        box.setTag(R.id.y_loc, new Integer(i));
                        box.setTag(R.id.x_loc, new Integer(j));
                        box.setTag(R.id.state, new Integer(currentState[i][j]));
                        box.setBackgroundResource(R.drawable.border_button);
                        box.setOnClickListener(listener);
                        puzzleLayout.addView(box, params);
                    }
                }


            } catch (IOException e1) {
            }
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
}
