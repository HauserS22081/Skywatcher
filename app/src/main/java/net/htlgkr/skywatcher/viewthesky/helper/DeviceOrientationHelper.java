package net.htlgkr.skywatcher.viewthesky.helper;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class DeviceOrientationHelper implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];

    private double pitch; // Rotation around the X-axis
    private double roll;  // Rotation around the Y-axis
    private double yaw;   // Rotation around the Z-axis (yaw)

    private OnOrientationChangedListener listener;

    public DeviceOrientationHelper(Context context, OnOrientationChangedListener listener) {
        this.listener = listener;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void start() {
        if (accelerometer != null && magnetometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values.clone(); // Store accelerometer data
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values.clone(); // Store magnetometer data
        }

        // If both gravity and geomagnetic data are available, calculate orientation
        if (gravity != null && geomagnetic != null) {
            float[] rotationMatrix = new float[9];
            float[] inclinationMatrix = new float[9];

            // Compute the rotation matrix
            boolean success = SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, gravity, geomagnetic);
            if (success) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(rotationMatrix, orientation);

                // Extract yaw (around Z-axis), pitch (around X-axis), and roll (around Y-axis)
                yaw = Math.toDegrees(orientation[0]);   // yaw in degrees
                pitch = Math.toDegrees(orientation[1]); // pitch in degrees
                roll = Math.toDegrees(orientation[2]);  // roll in degrees

                // Send updated yaw, pitch, and roll values to the listener
                listener.onOrientationChanged(yaw, pitch, roll);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No action needed
    }

    public interface OnOrientationChangedListener {
        void onOrientationChanged(double yaw, double pitch, double roll);
    }
}