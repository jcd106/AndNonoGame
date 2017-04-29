package com.example.jcdug.andnonogame;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity implements BarFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button downloadButton = (Button) findViewById(R.id.download_puzzles_button);

        if(MainActivity.getSignInStatus())
            downloadButton.setEnabled(true);
        else
            downloadButton.setEnabled(false);
    }

    /**
     * Starts the correct activity depending on which button is clicked
     *
     * @param view The view that was clicked
     */
    public void onClicked(View view) {
        switch (view.getId()) {
            case R.id.normal_puzzles_button:
                Intent i1 = new Intent(this, SizeSelectActivity.class);
                i1.putExtra("color", false);
                startActivity(i1);
                break;
            case R.id.color_puzzles_button:
                Intent i2 = new Intent(this, SizeSelectActivity.class);
                i2.putExtra("color", true);
                startActivity(i2);
                break;
            case R.id.create_own_button:
                Intent i3 = new Intent(this, CreateOwnActivity.class);
                startActivity(i3);
                break;
            case R.id.download_puzzles_button:
                Intent i4 = new Intent(this, QueryActivity.class);
                startActivity(i4);
                break;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
