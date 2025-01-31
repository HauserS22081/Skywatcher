package net.htlgkr.skywatcher.viewthesky.helper;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class DeviceOrientationHelper implements SensorEventListener {

    private static final double THRESHOLD = 1;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] gravity;
    private float[] geomagnetic;
    private double pitch;
    private double roll;

    private OnOrientationChangedListener listener;

    public DeviceOrientationHelper(Context context, OnOrientationChangedListener listener) {
        this.listener = listener;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void start() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values;
        }

        if (gravity != null && geomagnetic != null) {
            float[] R = new float[9];
            float[] I = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
            if (success) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);

                double tempPitch = Math.toDegrees(orientation[1]);
                double tempRoll = Math.toDegrees(orientation[2]);

                if (Math.abs(pitch - tempPitch) > THRESHOLD || Math.abs(roll - tempRoll) > THRESHOLD) {
                    pitch = tempPitch;
                    roll = tempRoll;

                    listener.onOrientationChanged(pitch, roll);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Keine Aktion nötig
    }

    public interface OnOrientationChangedListener {
        void onOrientationChanged(double pitch, double roll);
    }
}
