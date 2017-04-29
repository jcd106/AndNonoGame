package com.example.jcdug.andnonogame;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.jcdug.andnonogame.R.id.spinner;

public class QueryActivity extends AppCompatActivity implements BarFragment.OnFragmentInteractionListener{

    Spinner sizeSpinner;
    Spinner ratingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        sizeSpinner = (Spinner) findViewById(R.id.choose_query_puzzle_size);
        String[] sizes = new String[]{
                "Size",
                "5x5",
                "5x10",
                "10x5",
                "10x10"
        };
        ArrayAdapter<String> sizeSpinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, sizes);
        sizeSpinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_layout);
        sizeSpinner.setAdapter(sizeSpinnerArrayAdapter);

        ratingSpinner = (Spinner) findViewById(R.id.choose_query_puzzle_rating);
        String[] ratings = new String[]{
                "Rating",
                "0 Stars",
                "1 Star",
                "2 Stars",
                "3 Stars",
                "4 Stars",
                "5 Stars"
        };
        ArrayAdapter<String> ratingSpinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, ratings);
        ratingSpinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_layout);
        ratingSpinner.setAdapter(ratingSpinnerArrayAdapter);
    }

    /**
     * Starts the correct activity depending on which button is clicked
     *
     * @param view The view that was clicked
     */
    public void onClicked(View view) {
        switch (view.getId()) {
            case R.id.search_button:

                String searchSize = sizeSpinner.getSelectedItem().toString();
                String searchRating = ratingSpinner.getSelectedItem().toString();

                if(searchSize.equals("Size") && searchRating.equals("Rating")){
                    //Create a popup notifying the user that no search parameters are given
                    AlertDialog alertDialog = new AlertDialog.Builder(QueryActivity.this).create();
                    alertDialog.setTitle("Search Failed");
                    alertDialog.setMessage("You have not chosen a search criteria");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                else {
                    CheckBox searchColor = (CheckBox) findViewById(R.id.searchColorCheckBox);
                    Intent i1 = new Intent(this, TableActivity.class);
                    i1.putExtra("Size", searchSize);
                    i1.putExtra("Rating", searchRating);
                    if (searchColor.isChecked()) {
                        i1.putExtra("Type", "Color");
                    } else {
                        i1.putExtra("Type", "Binary");
                    }

                    startActivity(i1);
                }
                break;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
