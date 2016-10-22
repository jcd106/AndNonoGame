package com.example.jcdug.andnonogame;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SizeSelectActivity extends AppCompatActivity implements BarFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_size_select);
    }
    public void onFragmentInteraction(Uri uri) {

    }
    public void onButtonClick(View view)
    {
        Intent i = new Intent(this, PuzzleSelectActivity.class);
        startActivity(i);
    }
}
