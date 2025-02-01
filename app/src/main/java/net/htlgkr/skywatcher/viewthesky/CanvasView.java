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

    public void updateOrientation(double roll, double pitch) {
        this.phoneRoll = roll;
        this.phonePitch = pitch;

        // Compute device azimuth and elevation
        double[] deviceAzimuthElevation = calculateDeviceAzimuthElevation(phoneRoll, phonePitch);
        this.phoneAzimuth = deviceAzimuthElevation[0];
        this.phoneElevation = deviceAzimuthElevation[1];
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

    public double[] calculateDeviceAzimuthElevation(double phoneRoll, double phonePitch) {
        double azimuth = (phonePitch + 180) % 360;
        if (azimuth < 0) azimuth += 360;

        double elevation = Math.max(-90, Math.min(90, phoneRoll));

        return new double[]{azimuth, elevation};
    }

    public void drawPlanets(Canvas canvas, List<Planet> visiblePlanets) {
        if (visiblePlanets == null || visiblePlanets.isEmpty()) return;

        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        double FOV_AZIMUTH = 90.0;
        double FOV_ELEVATION = 60.0;

        double centerAzimuth = phonePitch;
        double centerElevation = phoneRoll;

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
