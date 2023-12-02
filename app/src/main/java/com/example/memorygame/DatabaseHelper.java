package com.example.memorygame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jetbrains.annotations.NotNull;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "memory_game_db";
    private static final int DATABASE_VERSION = 1;


    private static final String COLUMN_ID = "id";
    private static final String TABLE_NAME = "app_data";
    private static final String COLUMN_SCORE = "score";
    private static final String COLUMN_MUSIC = "music";
    private static final String COLUMN_SENSOR = "sensor";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(@NotNull SQLiteDatabase db) {
        // Create the app_data table if it doesn't exist
        String createTable = "CREATE TABLE if not exists " + TABLE_NAME + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_SCORE + " INTEGER," + COLUMN_MUSIC + " INTEGER," + COLUMN_SENSOR + " INTEGER" + ")";
        db.execSQL(createTable);
    }


    @Override
    public void onUpgrade(@NotNull SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the app_data table and recreate it on upgrade
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public void updateHighScore(int newScore) {
        // Update the high score in the database
        int currentHighScore = getHighScore();
        boolean currentMusicState = isMusicOn();
        boolean currentSensorState = isSensorOn();

        if (newScore <= currentHighScore) {
            return;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        // Update high score and music button state in database
        ContentValues values = new ContentValues();
        values.put(COLUMN_SCORE, newScore);
        values.put(COLUMN_MUSIC, currentMusicState ? 1 : 0);
        values.put(COLUMN_SENSOR, currentSensorState ? 1 : 0);

        long result = db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{"1"});

        if (result == 0) {
            // If no rows were affected, insert a new row
            values.put(COLUMN_ID, 1);
            db.insert(TABLE_NAME, null, values);
        }
        db.close();
    }

    public void updateMusicState(Boolean musicState) {
        // Update the music button state in the database
        int currentHighScore = getHighScore();
        boolean currentSensorState = isSensorOn();

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SCORE, currentHighScore);
        values.put(COLUMN_MUSIC, musicState ? 1 : 0);
        values.put(COLUMN_SENSOR, currentSensorState ? 1 : 0);

        long result = db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{"1"});

        if (result == 0) {
            values.put(COLUMN_ID, 1);
            db.insert(TABLE_NAME, null, values);
        }
        db.close();
    }

    public void updateSensorState(Boolean sensorState) {
        // Update the sensor button state in the database
        int currentHighScore = getHighScore();
        boolean currentMusicState = isMusicOn();

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SCORE, currentHighScore);
        values.put(COLUMN_MUSIC, currentMusicState ? 1 : 0);
        values.put(COLUMN_SENSOR, sensorState ? 1 : 0);

        long result = db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{"1"});

        if (result == 0) {
            values.put(COLUMN_ID, 1);
            db.insert(TABLE_NAME, null, values);
        }

        db.close();
    }

    public int getHighScore() {

        int currentHighScore = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        // Get current high score from database
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            int scoreColumnIndex = cursor.getColumnIndex(COLUMN_SCORE);
            if (scoreColumnIndex != -1) {
                currentHighScore = cursor.getInt(scoreColumnIndex);
            }
        }

        cursor.close();
        db.close();

        return currentHighScore;
    }

    public boolean isSensorOn() {
        boolean isSensorOn = true;
        SQLiteDatabase db = this.getReadableDatabase();

        // Get music button state from database
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            int sensorColumnIndex = cursor.getColumnIndex(COLUMN_SENSOR);
            if (sensorColumnIndex != -1) {
                isSensorOn = cursor.getInt(sensorColumnIndex) == 1;
            }
        }
        cursor.close();
        db.close();

        return isSensorOn;
    }


    public boolean isMusicOn() {
        boolean isMusicOn = true;
        SQLiteDatabase db = this.getReadableDatabase();

        // Get music button state from database
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            int musicColumnIndex = cursor.getColumnIndex(COLUMN_MUSIC);
            if (musicColumnIndex != -1) {
                isMusicOn = cursor.getInt(musicColumnIndex) == 1;
            }
        }
        cursor.close();
        db.close();

        return isMusicOn;
    }
}
