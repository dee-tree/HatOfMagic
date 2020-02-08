package com.codemitry.hatofmagic;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.to_top, android.R.anim.fade_out);
    }

    public void onStartClick(View v) {
        Intent gameIntent = new Intent(this, GameActivity.class);
        startActivity(gameIntent);
        overridePendingTransition(android.R.anim.fade_in, R.anim.to_bottom);
    }

    public void onSettingsClick(View v) {

    }


    public void onExitClick(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else finish();

    }

}
