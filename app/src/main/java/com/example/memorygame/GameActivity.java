package com.example.memorygame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class GameActivity extends AppCompatActivity implements CardAdapter.OnCardClickListener, ShakeDetector.OnShakeListener {

    private int score = 0;
    private int remainingPairs;
    private TextView scoreValue;
    private CardAdapter cardAdapter;
    private Card firstCard;
    private boolean isProcessing;
    private long lastBackPressTime = 0;
    private static final long BACK_PRESS_INTERVAL = 500; // .5 seconds
    private TextView mTimerTextView;
    private CountDownTimer mCountDownTimer;

    private SensorManager sensorManager;
    private boolean isShaking = false;
    List<Card> cards;

    boolean isGestureOn = true;
    boolean isMusicOn = true;
    private long timeRemainingMillis;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Initialize the RecyclerView and set up the adapter
        RecyclerView cardRecyclerView = findViewById(R.id.card_recycler_view);
        cardRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        // Create the list of cards and the adapter
        cards = createCardList();
        for (Card card : cards) {
            card.setFlipped(false);
        }
        CardAdapter adapter = new CardAdapter(cards, this);
        cardAdapter = adapter;

        // Set the adapter for the RecyclerView
        cardRecyclerView.setAdapter(adapter);

        // Set up the score TextView
        scoreValue = findViewById(R.id.score_value);
        scoreValue.setText(String.valueOf(score));
        remainingPairs = cards.size() / 2;

        // Get the game settings from the database
        DatabaseHelper db = new DatabaseHelper(this);
        isGestureOn = db.isSensorOn();
        isMusicOn = db.isMusicOn();
        db.close();


        // Set up the "End Game" button and its click listener
        Button endGameButton = findViewById(R.id.end_game_btn);
        endGameButton.setOnClickListener(v -> new AlertDialog.Builder(GameActivity.this).setTitle("Confirm").setMessage("Are you sure you want to end the game?").setPositiveButton("Yes", (dialog, which) -> {
            Intent intent = new Intent(GameActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear the activity stack
            startActivity(intent);
            finish(); // Finish the current activity
        }).setNegativeButton("No", null).show());


        // Initialize the timer TextView
        mTimerTextView = findViewById(R.id.timer);

        // start the countdown timer with 2 minutes (120000 ms)
        startCountDownTimer(120000);


        // Get the sensor manager and accelerometer sensor
        if (isGestureOn) {
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            // Create a shake detector and register it to listen for shake events
            ShakeDetector shakeDetector = new ShakeDetector(this);
            sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }

        if (isMusicOn) {
            // Initialize the MediaPlayer and set the background music
            mediaPlayer = MediaPlayer.create(this, R.raw.high_score_background);
            mediaPlayer.setLooping(true); // Set looping to true to play the music continuously
            mediaPlayer.start(); // Start playing the music
        }

        // Apply animations to the RecyclerView and score TextView
        Animation slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in); // Create the animation object
        cardRecyclerView.startAnimation(slideInAnimation); // Apply animation to the RecyclerView
        scoreValue.startAnimation(slideInAnimation); // Apply animation to the score TextView


    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onCardClick(Card card) {
        // Check if the card is already flipped or game is processing
        if (card.isFlipped() || isProcessing) {
            return;
        }

        // Flip the card
        card.setFlipped(true);
        cardAdapter.flipCard(card);


        // Check if there is already a card flipped
        if (firstCard == null) {
            firstCard = card;
            return;
        }

        // Set game processing flag to true
        isProcessing = true;

        // Increase score if the cards match, otherwise flip the cards back
        if (firstCard.getId() == card.getId()) {
            score += 20;
            scoreValue.setText(String.valueOf(score));
            remainingPairs--;
            if (remainingPairs == 0) {
                // Game over, show to result
                goHighScoreActivity();
            }
            firstCard = null;
            isProcessing = false;
        } else {
            new Handler().postDelayed(() -> {
                firstCard.setFlipped(false);
                card.setFlipped(false);
                cardAdapter.notifyDataSetChanged();
                firstCard = null;
                isProcessing = false;
            }, 1000);
        }
    }

    public void goHighScoreActivity() {
        // Navigate to the HighScoreActivity and pass the score
        Intent intent = new Intent(this, HighScoreActivity.class);
        score += getRemainingSeconds() * 5;
        intent.putExtra("score", score);
        startActivity(intent);

        // Finish the current activity
        finish();
    }


    @NotNull
    private List<Card> createCardList() {
        List<Card> cards = new ArrayList<>();

        // Add the cards to the list
        cards.add(new Card(1, R.drawable.card1));
        cards.add(new Card(1, R.drawable.card1_desc));

        cards.add(new Card(2, R.drawable.card2));
        cards.add(new Card(2, R.drawable.card2_desc));

        cards.add(new Card(3, R.drawable.card3));
        cards.add(new Card(3, R.drawable.card3_desc));

        cards.add(new Card(4, R.drawable.card4));
        cards.add(new Card(4, R.drawable.card4_desc));

        cards.add(new Card(5, R.drawable.card5));
        cards.add(new Card(5, R.drawable.card5_desc));

        cards.add(new Card(6, R.drawable.card6));
        cards.add(new Card(6, R.drawable.card6_desc));

        // Shuffle the cards
        Collections.shuffle(cards);

        return cards;
    }

    // Get the remaining seconds from the countdown timer
    private long getRemainingSeconds() {
        return timeRemainingMillis / 1000;
    }

    @Override
    public void onBackPressed() {
        // Handle the back button press event
        if (lastBackPressTime + BACK_PRESS_INTERVAL > System.currentTimeMillis()) {
            // The back button has been pressed twice in quick succession\
            // Show a confirmation dialog when the back button is pressed twice in quick succession
            new AlertDialog.Builder(GameActivity.this).setTitle("Confirm").setMessage("Are you sure you want to end the game?").setPositiveButton("Yes", (dialog, which) -> {
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear the activity stack
                startActivity(intent);
                finish(); // Finish the current activity
            }).setNegativeButton("No", null).show();
        } else {
            lastBackPressTime = System.currentTimeMillis();
        }
    }

    private void startCountDownTimer() {
        // Start the countdown timer with the default duration
        startCountDownTimer(0L);
    }

    private void startCountDownTimer(long millisInFuture) {
        // Start the countdown timer with the specified duration
        mCountDownTimer = new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the remaining time on each tick
                timeRemainingMillis = millisUntilFinished;
                long minutes = millisUntilFinished / 1000 / 60;
                long seconds = millisUntilFinished / 1000 % 60;
                mTimerTextView.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                // handle the countdown timer finished event
                goHighScoreActivity();
            }
        };
        mCountDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel the countdown timer
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        if (isGestureOn) {
            sensorManager.unregisterListener(sensorEventListener);
        }
        // Stop and release the MediaPlayer
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    // SensorEventListener implementation

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        private float acceleration;
        private float currentAcceleration;

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (isGestureOn) {
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                float previousAcceleration = currentAcceleration;
                currentAcceleration = (float) Math.sqrt(x * x + y * y + z * z);
                float delta = currentAcceleration - previousAcceleration;
                acceleration = acceleration * 0.9f + delta;

                if (acceleration > 12 && !isShaking) {
                    shuffleCards();
                    isShaking = true;
                }
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            // Not used
        }
    };


    @SuppressLint("NotifyDataSetChanged")
    // Shuffle the cards and notify the adapter
    private void shuffleCards() {
        if (isGestureOn) {
            // Code to shuffle the cards goes here
            Collections.shuffle(cards);
            cardAdapter.notifyDataSetChanged();
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
        // Unregister the shake detector listener
        if (isGestureOn) {
            sensorManager.unregisterListener(sensorEventListener);
        }
    }


    @Override
    public void onShake() {
        // Handle the shake event by shuffling the cards
        shuffleCards();
    }
}

