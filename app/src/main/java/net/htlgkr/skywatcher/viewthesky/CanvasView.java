package net.htlgkr.skywatcher.viewthesky;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.List;

public class CanvasView extends View {
    private List<Planet> planets;
    private double phoneYaw = 0.0;
    private double phoneRoll = 0.0;
    private double phonePitch = 0.0;
    private OnDrawCallback onDrawCallback;
    private double phoneAzimuth = 0.0;
    private double phoneElevation = 0.0;

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnDrawCallback(OnDrawCallback callback) {
        this.onDrawCallback = callback;
    }

    public void updateVisiblePlanets(List<Planet> planets) {
        this.planets = planets;
        invalidate(); // Forces redraw
        Log.d("CanvasView", "(updateVisiblePlanets) Updated planets: " + planets.size());
    }

    public void updateOrientation(double yaw, double pitch, double roll) {
        this.phoneYaw = yaw;
        this.phonePitch = pitch;
        this.phoneRoll = roll;

        // Compute device azimuth and elevation
        double[] deviceAzimuthElevation = calculateDeviceAzimuthElevation(phoneYaw, phonePitch, phoneRoll);
        this.phoneAzimuth = deviceAzimuthElevation[0];
        this.phoneElevation = deviceAzimuthElevation[1];

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (planets != null && !planets.isEmpty()) {
            Log.d("CanvasView", "Drawing " + planets.size() + " planets.");
            drawPlanets(canvas, planets);
        } else {
            Log.d("CanvasView", "No planets to draw.");
        }

        if (onDrawCallback != null) {
            onDrawCallback.onDraw(canvas);
        }
    }

    /**
     * Berechnet Azimuth und Elevation unter Berücksichtigung von Yaw, Pitch und Roll.
     */
    public double[] calculateDeviceAzimuthElevation(double yaw, double pitch, double roll) {
        double azimuth = (yaw + 360) % 360; // Yaw als Basis für Azimuth
        double elevation = Math.max(-90, Math.min(90, pitch)); // Pitch entspricht der Elevation

        return new double[]{azimuth, elevation};
    }

    /**
     * Zeichnet die sichtbaren Planeten auf das Canvas.
     */
    public void drawPlanets(Canvas canvas, List<Planet> visiblePlanets) {
        if (visiblePlanets == null || visiblePlanets.isEmpty()) return;

        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        double FOV_AZIMUTH = 90.0;
        double FOV_ELEVATION = 60.0;

        double centerAzimuth = phoneAzimuth;
        double centerElevation = phoneElevation;

        for (Planet planet : visiblePlanets) {
            double relativeAzimuth = planet.getAzimuth() - centerAzimuth;
            double relativeElevation = planet.getElevation() - centerElevation;

            // Normalize azimuth (-180 to 180)
            while (relativeAzimuth > 180) relativeAzimuth -= 360;
            while (relativeAzimuth < -180) relativeAzimuth += 360;

            // Filter planets outside of FOV
            if (Math.abs(relativeAzimuth) > FOV_AZIMUTH / 2) continue;
            if (Math.abs(relativeElevation) > FOV_ELEVATION / 2) continue;

            // Convert to screen coordinates
            float x = (float) (((relativeAzimuth + (FOV_AZIMUTH / 2)) / FOV_AZIMUTH) * canvasWidth);
            float y = (float) (((-(relativeElevation) + (FOV_ELEVATION / 2)) / FOV_ELEVATION) * canvasHeight);

            Log.d("CanvasView", "Planet " + planet.getName() + " position: x=" + x + ", y=" + y);

            Bitmap planetBitmap = planet.bitmap;
            if (planetBitmap != null) {
                int planetSize = 100;
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(planetBitmap, planetSize, planetSize, true);
                canvas.drawBitmap(scaledBitmap, x - planetSize / 2, y - planetSize / 2, null);
            }
        }
    }

    public interface OnDrawCallback {
        void onDraw(Canvas canvas);
    }
}
