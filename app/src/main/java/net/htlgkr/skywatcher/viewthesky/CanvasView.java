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
        invalidate(); // Damit der View neu gezeichnet wird
        Log.d("CanvasView", "(updateVisiblePlanets) Planeten aktualisiert: " + planets.size());
    }

    public void updateOrientation(double roll, double pitch) {
        this.phoneRoll = roll;
        this.phonePitch = pitch;

        // Berechne Azimut und Elevation des Geräts
        double[] deviceAzimuthElevation = calculateDeviceAzimuthElevation(phoneRoll, phonePitch);
        this.phoneAzimuth = deviceAzimuthElevation[0];
        this.phoneElevation = deviceAzimuthElevation[1];
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Zeichne Planeten, falls verfügbar
        if (planets != null && !planets.isEmpty()) {
            Log.d("CanvasView", "Zeichne " + planets.size() + " Planeten.");
            drawPlanetsRollPitch(canvas, planets);
        } else {
            Log.d("CanvasView", "Keine Planeten zum Zeichnen.");
        }

        // Rufe den OnDrawCallback auf, falls gesetzt
        if (onDrawCallback != null) {
            onDrawCallback.onDraw(canvas);
        }
    }

    // Berechnung von Azimut und Elevation basierend auf Pitch & Roll
    public double[] calculateDeviceAzimuthElevation(double phoneRoll, double phonePitch) {
        double azimuth = (phonePitch + 180) % 360;
        if (azimuth < 0) azimuth += 360;

        double elevation = Math.max(-90, Math.min(90, phoneRoll));

        return new double[]{azimuth, elevation};
    }

    public void drawPlanetsRollPitch(Canvas canvas, List<Planet> visiblePlanets) {
        if (visiblePlanets != null && !visiblePlanets.isEmpty()) {
            int canvasWidth = canvas.getWidth();
            int canvasHeight = canvas.getHeight();
            int margin = 50;

            for (Planet planet : visiblePlanets) {
                double azimuthOffset = planet.getAzimuth() - phonePitch;
                double elevationOffset = planet.getElevation() - phoneRoll;

                float x = (float) ((azimuthOffset + 180) / 360 * canvasWidth);
                float y = (float) ((elevationOffset + 90) / 180 * canvasHeight);

                x = Math.max(-margin, Math.min(canvasWidth + margin, x));
                y = Math.max(-margin, Math.min(canvasHeight + margin, y));

                Bitmap planetBitmap = planet.bitmap;
                if (planetBitmap != null) {
                    int planetSize = 100;
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(planetBitmap, planetSize, planetSize, true);
                    canvas.drawBitmap(scaledBitmap, x - planetSize / 2, y - planetSize / 2, null);
                }
            }
        }
    }

    public interface OnDrawCallback {
        void onDraw(Canvas canvas);
    }
}
