package com.example.jcdug.andnonogame;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 *
 * Handles the display and functionality of the action bar
 *
 * @author  Josh Dughi, Peter Todorov
 * @version 1.4.3
 */
public class BarFragment extends Fragment implements View.OnClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // Auto-Generated
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public BarFragment() {
        // Required empty public constructor
    }

    // Auto-Generated
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * This methods inflates the layout for the fragment
     * and sets the OnClickListener for the buttons in the fragment
     * @param inflater  Used to add views to the activity view
     * @param container
     * @param savedInstanceState
     * @return  The activity view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bar, container, false);
        //
        ImageButton settingsButton = (ImageButton) view.findViewById(R.id.settings_button_bar);
        settingsButton.setOnClickListener(this);
        ImageButton backButton = (ImageButton) view.findViewById(R.id.back_button_bar);
        backButton.setOnClickListener(this);
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
     * Sets the functionality for when the buttons are clicked
     * The settings button starts the SettingsActivity
     * The back button finishes the currentActivity and resumes the previous activity
     *
     * @param v The view that was clicked
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings_button_bar:
                Intent i = new Intent(getActivity(), SettingsActivity.class);
                getActivity().startActivity(i);
                break;
            case R.id.back_button_bar:
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
     * Auto-Generated
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
