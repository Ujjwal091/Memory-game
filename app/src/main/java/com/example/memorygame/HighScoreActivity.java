package com.example.memorygame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class HighScoreActivity extends AppCompatActivity implements SwipeGestureDetector.OnSwipeListener{

    boolean isMusicOn = true;
    private MediaPlayer mediaPlayer;
    private SwipeGestureDetector mSwipeGestureDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);


        //  Retrieve score from intent extra
        int score = getIntent().getIntExtra("score", 0);


        // Initialize a DatabaseHelper object to interact with the database and retrieve the high score and music status
        DatabaseHelper dbHelper;
        dbHelper = new DatabaseHelper(this);

        // retrieve the high score from the database
        int highScore = dbHelper.getHighScore();

        // retrieve the music status from the database
        isMusicOn = dbHelper.isMusicOn();


        // Compare the retrieved high score with the user's score, if the user's score is higher then update the high score in the database
        if (score > highScore) {
            highScore = score;
            dbHelper.updateHighScore(highScore);

            // Get a reference to the "New High Score" text view
            TextView newHighScoreText = findViewById(R.id.new_high_score_text);

            // Make the text view visible
            newHighScoreText.setVisibility(View.VISIBLE);
        }
        dbHelper.close();

        // Display score and high score in TextViews
        TextView yourScoreValue = findViewById(R.id.your_score_value);
        yourScoreValue.setText(String.valueOf(score));

        TextView highScoreValue = findViewById(R.id.high_score_value);
        highScoreValue.setText(String.valueOf(highScore));

        if (isMusicOn) {
            // Initialize the MediaPlayer and set the background music
            mediaPlayer = MediaPlayer.create(this, R.raw.high_score_background);
            mediaPlayer.setLooping(true); // Set looping to true to play the music continuously
            mediaPlayer.start(); // Start playing the music
        }

        // Initialize SwipeGestureDetector
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
    public void onSwipeRight() {
        // Handle swipe right action
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release the MediaPlayer resources when the activity is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Resume playing the music when the activity is resumed
        if (mediaPlayer != null && !mediaPlayer.isPlaying() && isMusicOn) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause the music when the activity is paused
        if (mediaPlayer != null && mediaPlayer.isPlaying() && isMusicOn) {
            mediaPlayer.pause();
        }
    }

    public void share(View view) {
        // Create a Bitmap of the app's current screen
        View rootView = getWindow().getDecorView().getRootView();
        rootView.setDrawingCacheEnabled(true);
        Bitmap screenshot = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);

        // Save the screenshot to the device's storage
        String fileName = "screenshot.jpg";
        File file = new File(getExternalFilesDir(null), fileName);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            screenshot.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

// Create an Intent to share the saved image
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(HighScoreActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider", file));
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out my score in the Memory Game app! \nDownload the app from Play store");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share screenshot"));

    }
}
