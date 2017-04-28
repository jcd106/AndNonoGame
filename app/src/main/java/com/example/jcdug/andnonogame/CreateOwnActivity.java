package com.example.jcdug.andnonogame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

public class CreateOwnActivity extends AppCompatActivity {

    Spinner spinner;    //Spinner storing sizes
    int[] size = {5,5};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_own);

        spinner = (Spinner) findViewById(R.id.choose_puzzle_size);
        String[] sizes = new String[]{
                "5x5",
                "5x10",
                "10x5",
                "10x10"
        };
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, sizes);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(spinnerArrayAdapter);

        //Creates an OnItemSelectedListener for the spinner which store the chosen color scheme in shared preferences
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                String[] rxc = selected.split("x");
                size[0] = Integer.parseInt(rxc[0]);
                size[1] = Integer.parseInt(rxc[1]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Starts the correct activity depending on which button is clicked
     *
     * @param view The view that was clicked
     */
    public void onClicked(View view) {
        switch (view.getId()) {
            case R.id.goto_create:
                CheckBox isColor = (CheckBox) findViewById(R.id.isColorCheckBox);
                if(!isColor.isChecked()) {
                    Intent i1 = new Intent(this, CreatePuzzleActivity.class);
                    i1.putExtra("size", size);
                    startActivity(i1);
                }
                else{
                    Intent i2 = new Intent(this, ColorSelectActivity.class);
                    i2.putExtra("size", size);
                    startActivity(i2);
                }
                break;
            case R.id.cancel_create:
                onBackPressed();
                break;
            case R.id.back_button_create:
                onBackPressed();
                break;
        }
    }
}
