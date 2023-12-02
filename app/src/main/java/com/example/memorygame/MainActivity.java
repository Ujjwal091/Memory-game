package com.example.memorygame;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button startGame;
    ImageButton settingsButton;
    ImageView logoImage;
    private MediaPlayer mediaPlayer;
    private boolean isMusicOn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startGame = findViewById(R.id.start_button);
        settingsButton = findViewById(R.id.settings_button);
        logoImage = findViewById(R.id.logo_image);


        // Animate the buttons and logo image
        animateButton(startGame, 0); // Animate the Start Game button with no delay
        animateButton(settingsButton, 200); // Animate the Settings button with a delay of 200ms
        animateImage(logoImage, 400); // Animate the logo image with a delay of 400ms


//         Retrieve music button state from the database
        DatabaseHelper db = new DatabaseHelper(this);
        isMusicOn = db.isMusicOn();
        db.close();

//         Play or pause background music according to the music button state
        if (isMusicOn) {
            mediaPlayer = MediaPlayer.create(this, R.raw.candy_crush_theme);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
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
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        DatabaseHelper db = new DatabaseHelper(this);
        isMusicOn = db.isMusicOn();
        if (isMusicOn) {
            mediaPlayer = MediaPlayer.create(this, R.raw.candy_crush_theme);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
        db.close();
    }


    // this method will be called when user click on Settings button
    public void goSettings(View view) {
        Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(i);
    }


    // this method will be called when user click on Start game button
    public void startGame(View view) {
        Intent i = new Intent(getApplicationContext(), GameActivity.class);
        startActivity(i);
    }

    // Animate a Button view with a specified delay
    private void animateButton(Button button, long delay) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, View.SCALE_X, 0.9f, 1.0f);
        scaleX.setDuration(500);
        scaleX.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, View.SCALE_Y, 0.9f, 1.0f);
        scaleY.setDuration(500);
        scaleY.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator alpha = ObjectAnimator.ofFloat(button, View.ALPHA, 0f, 1f);
        alpha.setDuration(500);
        alpha.setInterpolator(new AccelerateDecelerateInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, alpha);
        animatorSet.setStartDelay(delay);
        animatorSet.start();
    }

    // Animate an ImageButton view with a specified delay
    private void animateButton(ImageButton button, long delay) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, View.SCALE_X, 0.9f, 1.0f);
        scaleX.setDuration(500);
        scaleX.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, View.SCALE_Y, 0.9f, 1.0f);
        scaleY.setDuration(500);
        scaleY.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator alpha = ObjectAnimator.ofFloat(button, View.ALPHA, 0f, 1f);
        alpha.setDuration(500);
        alpha.setInterpolator(new AccelerateDecelerateInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, alpha);
        animatorSet.setStartDelay(delay);
        animatorSet.start();
    }

    // Animate an ImageView with a specified delay
    private void animateImage(View view, long delay) {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, View.ROTATION, 0, 360);
        rotation.setDuration(1000);
        rotation.setInterpolator(new AccelerateDecelerateInterpolator());
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 0.8f, 1.0f);
        scaleX.setDuration(500);
        scaleX.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.8f, 1.0f);
        scaleY.setDuration(500);
        scaleY.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f);
        alpha.setDuration(500);
        alpha.setInterpolator(new AccelerateDecelerateInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(rotation, scaleX, scaleY, alpha);
        animatorSet.setStartDelay(delay);
        animatorSet.start();
    }

}
