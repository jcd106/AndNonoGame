package com.example.jcdug.andnonogame;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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

    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        spinner = (Spinner) findViewById(R.id.choose_query_puzzle_size);
        String[] sizes = new String[]{
                "5x5",
                "5x10",
                "10x5",
                "10x10"
        };
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, sizes);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    /**
     * Starts the correct activity depending on which button is clicked
     *
     * @param view The view that was clicked
     */
    public void onClicked(View view) {
        switch (view.getId()) {
            case R.id.search_button:
                Intent i1 = new Intent(this, TableActivity.class);
                i1.putExtra("Size", spinner.getSelectedItem().toString());
                startActivity(i1);
                break;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
