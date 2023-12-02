package com.example.memorygame;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener {
    // Minimum acceleration needed to register a shake event
    private static final int MIN_SHAKE_ACCELERATION = 10;

    // Minimum time between two shake events
    private static final int MIN_SHAKE_INTERVAL = 500; // in milliseconds

    // Time of the last shake event
    private long lastShakeTime = 0;

    // Listener to notify when a shake event occurs
    private final OnShakeListener listener;

    public ShakeDetector(OnShakeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastShakeTime) > MIN_SHAKE_INTERVAL) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                double acceleration = Math.sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH;
                if (acceleration > MIN_SHAKE_ACCELERATION) {
                    lastShakeTime = currentTime;
                    listener.onShake();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }

    // Interface to notify when a shake event occurs
    public interface OnShakeListener {
        void onShake();
    }
}
