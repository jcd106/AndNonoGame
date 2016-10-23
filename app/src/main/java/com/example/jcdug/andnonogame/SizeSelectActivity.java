package com.example.jcdug.andnonogame;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SizeSelectActivity extends AppCompatActivity implements BarFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_size_select);
        Button fiveByFive = (Button) findViewById(R.id.fivebyfive);
        Button tenByTen = (Button) findViewById(R.id.tenbyten);
        int num5 = 0;
        int num10 = 0;
        int comp5 = 0;
        int comp10 = 0;
        PuzzleDatabase db = MainActivity.getDB();
        Cursor c1 = db.getCountBySize();
        c1.moveToFirst();
        Cursor c2 = db.getCountCompletedBySize();
        c2.moveToFirst();
        int i1 = c1.getColumnIndex("numPuzzles");
        int r1 = c1.getColumnIndex("Rows");
        int col1 = c1.getColumnIndex("Cols");
        while(!c1.isAfterLast()){
            if(c1.getInt(r1) == 5 && c1.getInt(col1) == 5) {
                num5 = c1.getInt(i1);
            }
            if(c1.getInt(r1) == 10 && c1.getInt(col1) == 10) {
                num10 = c1.getInt(i1);
            }
            c1.moveToNext();
        }
        int i2 = c2.getColumnIndex("numPuzzles");
        int r2 = c2.getColumnIndex("Rows");
        int col2 = c2.getColumnIndex("Cols");
        while(!c2.isAfterLast()){
            if(c2.getInt(r2) == 5 && c2.getInt(col2) == 5) {
                comp5 = c2.getInt(i2);
            }
            if(c2.getInt(r2) == 10 && c2.getInt(col2) == 10) {
                comp10 = c2.getInt(i2);
            }
            c2.moveToNext();
        }
        String fiveFiveText = "5x5 ("+comp5+"/"+num5+")";
        fiveByFive.setText(fiveFiveText);
        String tenTenText = "10x10 ("+comp10+"/"+num10+")";
        tenByTen.setText(tenTenText);

    }
    public void onFragmentInteraction(Uri uri) {

    }
    public void onButtonClick(View view)
    {
        Intent i = new Intent(this, PuzzleSelectActivity.class);
        switch(view.getId()) {
            case R.id.fivebyfive:
                i.putExtra("size","5 5");
                break;
            case R.id.tenbyten:
                i.putExtra("size","10 10");
                break;
        }
        startActivity(i);
    }
}
