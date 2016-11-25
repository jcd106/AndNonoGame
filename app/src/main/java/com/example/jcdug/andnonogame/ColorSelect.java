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
 * {@link ColorSelect.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ColorSelect#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ColorSelect extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "colors";

    // TODO: Rename and change types of parameters
    private int[] colors;
    int selectedColor = 1;

    private OnFragmentInteractionListener mListener;

    public ColorSelect() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param colors Parameter 1.
     * @return A new instance of fragment ColorSelect.
     */
    // TODO: Rename and change types and number of parameters
    public static ColorSelect newInstance(int[] colors) {
        ColorSelect fragment = new ColorSelect();
        Bundle args = new Bundle();
        args.putIntArray(ARG_PARAM1, colors);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            colors = getArguments().getIntArray(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_color_select, container, false);

        Bundle bundle = this.getArguments();
        int id = bundle.getInt("puzzleID");
        try {
            //Retrieve PuzzleDatabase from MainActivity
            PuzzleDatabase db = MainActivity.getDB();

            //Get the correct serialized puzzle by its ID from the PuzzleDatabase
            Cursor c1 = db.getColorPuzzleByID(id);
            int p1 = c1.getColumnIndex("Puzzle");
            c1.moveToFirst();
            byte[] b = c1.getBlob(p1);

            //Create a new input stream and deserialize the puzzle to be displayed
            ByteArrayInputStream bis = new ByteArrayInputStream(b);
            ObjectInput in = new ObjectInputStream(bis);
            final ColorPuzzle p = (ColorPuzzle) in.readObject();
            bis.close();
            in.close();

            //Store all of the puzzle objects information in the PuzzleFragment
            colors = p.getColors();

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    //Retrieve clicked puzzle box and its stored tag values
                    TextView b = (TextView) view.findViewById(view.getId());
                    Integer color = (Integer) b.getTag(R.id.color);

                    BlankFragment bf = (BlankFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.blank_fragment);
                    ColorPuzzleFragment puzzleFragment = (ColorPuzzleFragment) bf.getChildFragmentManager().findFragmentByTag("ColorPuzzleFragment");
                    puzzleFragment.setSelectedColor(color);

                    BlankFragment bf2 = (BlankFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_colors);
                    ColorSelect colorSelect = (ColorSelect) bf2.getChildFragmentManager().findFragmentByTag("ColorSelect");
                    colorSelect.selectedColor = color;
                    bf2.getChildFragmentManager().beginTransaction().detach(colorSelect).attach(colorSelect).commit();
                }
            };

            TableLayout colorLayout = (TableLayout) view.findViewById(R.id.fragment_color_select);
            TableRow tableRow = new TableRow(this.getActivity());
            colorLayout.addView(tableRow);

            for (int i = 1; i < colors.length; i++) {
                TextView newBox;
                if(i == selectedColor)
                    newBox = (TextView) inflater.inflate(R.layout.border_box_selected, tableRow, false);
                else
                    newBox = (TextView) inflater.inflate(R.layout.border_box_large, tableRow, false);
                newBox.setTag(R.id.color, i);
                newBox.setOnClickListener(listener);
                Drawable filled = ContextCompat.getDrawable(this.getActivity(), R.drawable.border_button);
                filled.setColorFilter(colors[i], PorterDuff.Mode.MULTIPLY);
                newBox.setBackground(filled);
                tableRow.addView(newBox);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
