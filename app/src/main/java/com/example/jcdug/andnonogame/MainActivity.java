package com.example.jcdug.andnonogame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onPlayClicked(View view)
    {
        Intent i = new Intent(this, SizeSelectActivity.class);
        startActivity(i);
    }

    public void onTutorialClicked(View view)
    {
        Intent i = new Intent(this, TutorialActivity.class);
        startActivity(i);
    }

    public void onSettingsClicked(View view)
    {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }
}
