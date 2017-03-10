package com.example.jcdug.andnonogame;

import android.content.Context;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateColorSelect.OnFragmentInteractionListener} interface
 * to handle interaction events.
 *
 * @author Josh Dughi, Peter Todorov
 */
public class CreateColorSelect extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    int selectedColor = 1;
    private int[] colors;

    public CreateColorSelect() {
        // Required empty public constructor
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_color_select, container, false);

        Bundle bundle = this.getArguments();
        int id = bundle.getInt("puzzleID");
        colors = bundle.getIntArray("colors");



            //Create a new onClickListener for the TextViews in the fragment
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //Retrieve clicked puzzle box and its stored tag values
                    TextView b = (TextView) view.findViewById(view.getId());
                    Integer color = (Integer) b.getTag(R.id.color);

                    BlankFragment bf = (BlankFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.blank_fragment);
                    CreateColorPuzzleFragment puzzleFragment = (CreateColorPuzzleFragment) bf.getChildFragmentManager().findFragmentByTag("CreateColorPuzzleFragment");
                    puzzleFragment.setSelectedColor(color);

                    //Change which color is selected and refresh the fragment
                    BlankFragment bf2 = (BlankFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_create_colors);
                    CreateColorSelect colorSelect = (CreateColorSelect) bf2.getChildFragmentManager().findFragmentByTag("CreateColorSelect");
                    colorSelect.selectedColor = color;
                    bf2.getChildFragmentManager().beginTransaction().detach(colorSelect).attach(colorSelect).commit();
                }
            };

            //Create TableLayout to organize colors into a table
            TableLayout colorLayout = (TableLayout) view.findViewById(R.id.fragment_create_color_select);
            //Create a TableRow to add the colors to
            TableRow tableRow = new TableRow(this.getActivity());
            colorLayout.addView(tableRow);

            //For each non-empty color
            for (int i = 1; i < colors.length; i++) {
                //Create a new TextView
                TextView newBox;

                //If the color is the selectedColor, inflate the TextView with the border_box_selected layout
                if(i == selectedColor) {
                    newBox = (TextView) inflater.inflate(R.layout.border_box_selected, tableRow, false);
                }

                    //Otherwise, inflate the TextView with the border_box_large layout
                else {
                    newBox = (TextView) inflater.inflate(R.layout.border_box_unselected, tableRow, false);
                }

                //Set the color tag of the TextView to i
                newBox.setTag(R.id.color, i);
                newBox.setId(-1*colors[i]);

                //Add the onClickListener
                newBox.setOnClickListener(listener);

                //Add the correct background to the TextView and and the TextView to the table row
                Drawable filled = ContextCompat.getDrawable(this.getActivity(), R.drawable.border_button);
                filled.setColorFilter(colors[i], PorterDuff.Mode.MULTIPLY);
                newBox.setBackground(filled);
                tableRow.addView(newBox);
            }
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
