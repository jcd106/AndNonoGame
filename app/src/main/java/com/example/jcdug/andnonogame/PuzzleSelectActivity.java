package com.example.jcdug.andnonogame;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PuzzleSelectActivity extends AppCompatActivity implements BarFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_select);
        Intent i = getIntent();
        String size = i.getStringExtra("size");
        String[] rxc = size.split(" ");
        int[] s = {Integer.parseInt(rxc[0]),Integer.parseInt(rxc[1])};
        TextView sizeText = (TextView) findViewById(R.id.puzzle_select_size);
        sizeText.setText(s[0]+"x"+s[1]);
        PuzzleDatabase db = MainActivity.getDB();
        Cursor c1 = db.getPuzzlesBySize(s[0],s[1]);
        c1.moveToFirst();
        RelativeLayout puzzleSelectLayout = (RelativeLayout) findViewById(R.id.activity_puzzle_select);
        int i1 = c1.getColumnIndex("PuzzleID");
        int prevId = R.id.puzzle_select_size;
        final Context context = this;
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, PuzzleActivity.class);
                i.putExtra("puzzleID", Integer.toString(view.getId()));
                startActivity(i);
            }
        };
        while(!c1.isAfterLast()){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                    (RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(20,20,20,20);
            Button b = new Button(this);
            int id = c1.getInt(i1);
            b.setId(id);
            b.setText("PuzzleID: "+id);
            b.setBackgroundResource(R.drawable.border_button);
            b.setOnClickListener(listener);
            params.addRule(RelativeLayout.BELOW, prevId);
            puzzleSelectLayout.addView(b, params);
            c1.moveToNext();
            prevId = id;
        }
    }
    public void onFragmentInteraction(Uri uri) {

    }

}
