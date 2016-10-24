package com.example.jcdug.andnonogame;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
 * Use the {@link UndoBar#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UndoBar extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    int id;

    private OnFragmentInteractionListener mListener;

    public UndoBar() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UndoBar.
     */
    // TODO: Rename and change types and number of parameters
    public static UndoBar newInstance(String param1, String param2) {
        UndoBar fragment = new UndoBar();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_puzzle_button:
                try {
                    PuzzleDatabase db = MainActivity.getDB();
                    db.resetPuzzle(id);
//                    View view = v.findViewById(R.id.fragment_puzzle);
//                    view.refreshDrawableState();
                    //this.getActivity().recreate();
                    PuzzleFragment puzzleFragment = (PuzzleFragment) this.getActivity().getSupportFragmentManager().findFragmentById(R.id.blank_fragment);
                    getFragmentManager().beginTransaction().detach(puzzleFragment).attach(puzzleFragment).commit();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.undo_button:
                getActivity().onBackPressed();
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
