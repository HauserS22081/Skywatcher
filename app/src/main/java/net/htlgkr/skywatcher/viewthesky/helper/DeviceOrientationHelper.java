package net.htlgkr.skywatcher.viewthesky.helper;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class DeviceOrientationHelper implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;

    private double pitch;
    private double roll;

    private OnOrientationChangedListener listener;

    public DeviceOrientationHelper(Context context, OnOrientationChangedListener listener) {
        this.listener = listener;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    public void start() {
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            onAccelerometerChanged(event);
        }
        else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            onGyroscopeChanged(event);
        }
    }

    private void onAccelerometerChanged(SensorEvent event) {
        // Berechnung des Pitch und Roll-Werts basierend auf den Beschleunigungswerten
        double x = event.values[0];
        double y = event.values[1];
        double z = event.values[2];

        // Berechnung von Pitch und Roll
        pitch = Math.atan2(y, z) * (180 / Math.PI);
        roll = Math.atan2(x, z) * (180 / Math.PI);

        listener.onOrientationChanged(pitch, roll);
    }

    private void onGyroscopeChanged(SensorEvent event) {
        // Hier könntest du zusätzliche Logik zur Verwendung von Gyroskop-Daten hinzufügen
        // Um Pitch und Roll weiter zu verfeinern, falls nötig
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Keine Aktion erforderlich
    }

    public interface OnOrientationChangedListener {
        void onOrientationChanged(double pitch, double roll);
    }
}
