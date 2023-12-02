package com.example.memorygame;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity implements SwipeGestureDetector.OnSwipeListener{

    Toolbar toolbar;
    private MediaPlayer mediaPlayer;
    private boolean isMusicOn = false;

    private SwipeGestureDetector mSwipeGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SwitchCompat musicSwitch = findViewById(R.id.musicSwitch);
        SwitchCompat gestureSwitch = findViewById(R.id.gestureSwitch);

        // fetch and set the music button status
        musicSwitch.setChecked(getMusicStatus());

        // fetch and set the gesture button status
        gestureSwitch.setChecked(getSensorStatus());


        musicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> changeMusicStatus(isChecked));
        gestureSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> changeSensorStatus(isChecked));

        toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // set the title "Settings" in the toolbar in the settingsActivity
        toolbar.setTitle("Settings");

        // Retrieve music button state from the database
        DatabaseHelper db = new DatabaseHelper(this);
        isMusicOn = db.isMusicOn();
        db.close();

        // Play or pause background music according to the music button state
        if (isMusicOn) {
            mediaPlayer = MediaPlayer.create(this, R.raw.settings_background);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }

        mSwipeGestureDetector = new SwipeGestureDetector(this, this);

        // Set SwipeGestureDetector as the touch listener for the root view
        View rootView = findViewById(android.R.id.content);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mSwipeGestureDetector.getView().dispatchTouchEvent(event);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onSwipeRight() {
        // Handle swipe right action
        onBackPressed();
    }

    // this method will called automatically when user click on back button in the toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // return the status of music button whether it is On or Off
    // fetch the status from the database
    public boolean getMusicStatus() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        boolean isMusicOn = databaseHelper.isMusicOn();
        databaseHelper.close();

        return isMusicOn;
    }

    // fetch the status of sensor button from the database,
    // based on whether user want to use the shake to shuffle feature or not
    public boolean getSensorStatus() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        boolean isSensorOn = databaseHelper.isSensorOn();
        databaseHelper.close();

        return isSensorOn;
    }

    // change the music button status to ON or OFF in the database based on the user activity
    private void changeMusicStatus(boolean musicStatus) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        databaseHelper.updateMusicState(musicStatus);

        databaseHelper.close();

        isMusicOn = musicStatus;

        if (isMusicOn) {
            mediaPlayer = MediaPlayer.create(this, R.raw.settings_background);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
        else{
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    //     change the sensor button status to ON or OFF in the database based on the user activity
    private void changeSensorStatus(boolean sensorStatus) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        databaseHelper.updateSensorState(sensorStatus);

        databaseHelper.close();
    }
}