package com.example.jcdug.andnonogame;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class ColorSelectActivity extends AppCompatActivity {

    final Set<Integer> chosenColors = new LinkedHashSet<Integer>();

    int[] size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_select);

        Intent intent = getIntent();
        size = intent.getIntArrayExtra("size");

        int colors[] = this.getBaseContext().getResources().getIntArray(R.array.selectColors);
        Drawable[] colorBoxs = new Drawable[colors.length];
        chosenColors.add(Color.WHITE);
        for(int i = 0; i < colors.length; i++) {
            colorBoxs[i] = ContextCompat.getDrawable(this, R.drawable.border_button);
            colorBoxs[i].setColorFilter(colors[i], PorterDuff.Mode.MULTIPLY);
            //colorBoxs[i].setColorFilter(ContextCompat.getColor(this, colors[i]), PorterDuff.Mode.MULTIPLY);
        }


        TableLayout colorChoices = (TableLayout) this.findViewById(R.id.colorList);
        LayoutInflater inflater = this.getLayoutInflater();

        //Create an OnClickListener for each color
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //Retrieve clicked puzzle box and its stored tag values
                CheckedTextView ctv = (CheckedTextView) view.findViewById(view.getId());


                //Handles choosing of colors
                if (ctv.isChecked()) {
                    ctv.setChecked(false);
                    ctv.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);
                    chosenColors.remove((Integer) ctv.getTag());
                    //Log.d("Color List", "" + chosenColors.size());
                }
                else {
                    ctv.setChecked(true);
                    ctv.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
                    chosenColors.add((Integer) ctv.getTag());
                    //Log.d("Color List", "" + chosenColors.size());
                }
            }
        };

        int colorCount = 0;

        for(int i = 0; i < Math.ceil(colors.length/5.0); i++){

            TableRow tableRow = new TableRow(this);
            tableRow.setGravity(Gravity.CENTER_HORIZONTAL);
            colorChoices.addView(tableRow);


            for(int j = 0; j < 5; j++){

                if(colorCount < colors.length) {
                    CheckedTextView colorBox = (CheckedTextView) inflater.inflate(R.layout.color_choice_box, tableRow, false);
                    colorBox.setBackground(colorBoxs[colorCount]);
                    colorBox.setTag(colors[colorCount]);
                    colorBox.setOnClickListener(listener);
                    tableRow.addView(colorBox);
                    colorCount++;
                }
            }
        }

    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.back_button_color_select:
                super.onBackPressed();
                break;
            case R.id.goto_create_color:
                if(chosenColors.size() > 10){
                    //Create a popup to indicate too many colors have been chosen
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Too Many Colors!");
                    alertDialog.setMessage("You can only choose up to 9 colors.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                else if(chosenColors.size() < 3){
                    //Create a popup to indicate too few colors have been chosen
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Not Enough Colors!");
                    alertDialog.setMessage("You must choose at least 2 colors.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                else {
                    Intent i1 = new Intent(this, CreateColorPuzzleActivity.class);
                    i1.putExtra("size", size);
                    ArrayList<Integer> sendColors = new ArrayList<Integer>(chosenColors.size());
                    sendColors.addAll(chosenColors);
                    i1.putExtra("colors", sendColors);//chosenColors.toArray());
                    startActivity(i1);
                    break;
                }
        }
    }
}
