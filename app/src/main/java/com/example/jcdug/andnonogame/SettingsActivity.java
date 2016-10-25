package com.example.jcdug.andnonogame;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.IOException;

public class SettingsActivity extends AppCompatActivity {

    public static final String COLOR_CHOICE = "ColorChoice";
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        spinner = (Spinner) findViewById(R.id.spinner);
        String[] colors = new String[]{
                "Black and White",
                "Red and White",
                "Red and Blue"
        };

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_layout, colors);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(spinnerArrayAdapter);
        SharedPreferences preferences = this.getSharedPreferences(COLOR_CHOICE, Context.MODE_PRIVATE);
        int position = preferences.getInt("position", 0);
        spinner.setSelection(position);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                SharedPreferences.Editor editor = getSharedPreferences(COLOR_CHOICE, MODE_PRIVATE).edit();
                String[] colors = selected.split(" ");
                editor.putString("empty", colors[2].toUpperCase());
                editor.putString("filled", colors[0].toUpperCase());
                editor.putInt("position", spinner.getSelectedItemPosition());
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void onBackClicked(View view) {
        super.onBackPressed();
    }

    public void onResetAllClicked(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Reset All Puzzles!");
        alertDialog.setMessage("Are you sure?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which){
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which){
                        PuzzleDatabase db = MainActivity.getDB();
                        int[] ids = db.getAllPuzzleIDs();
                        for(int i = 0; i < ids.length; i++)
                        {
                            try {
                                db.resetPuzzle(ids[i]);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
