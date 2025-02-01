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

            double FOV_AZIMUTH = 60.0;  // Sichtfeld in Grad für Azimut (z. B. 60°)
            double FOV_ELEVATION = 40.0; // Sichtfeld in Grad für Elevation (z. B. 40°)

            double centerAzimuth = phonePitch;  // Aktuelle Blickrichtung (Azimut)
            double centerElevation = phoneRoll; // Aktuelle Blickrichtung (Elevation)

            for (Planet planet : visiblePlanets) {
                double relativeAzimuth = planet.getAzimuth() - centerAzimuth;
                double relativeElevation = planet.getElevation() - centerElevation;

                // Normalisiere Azimut-Offset (-180 bis 180)
                while (relativeAzimuth > 180) relativeAzimuth -= 360;
                while (relativeAzimuth < -180) relativeAzimuth += 360;

                // Falls ein Planet außerhalb des FOV liegt, nicht zeichnen
                if (Math.abs(relativeAzimuth) > FOV_AZIMUTH / 2) continue;
                if (Math.abs(relativeElevation) > FOV_ELEVATION / 2) continue;

                // Position auf dem Bildschirm berechnen
                float x = (float) (((relativeAzimuth + (FOV_AZIMUTH / 2)) / FOV_AZIMUTH) * canvasWidth);
                float y = (float) (((-(relativeElevation) + (FOV_ELEVATION / 2)) / FOV_ELEVATION) * canvasHeight);

                Bitmap planetBitmap = planet.bitmap;
                if (planetBitmap != null) {
                    int planetSize = 80;
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(planetBitmap, planetSize, planetSize, true);
                    canvas.drawBitmap(scaledBitmap, x - planetSize / 2, y - planetSize / 2, null);
                }
            }
        }
    }


    public void drawPlanetsRollPitchFOVtoHIGH(Canvas canvas, List<Planet> visiblePlanets) {
        if (visiblePlanets != null && !visiblePlanets.isEmpty()) {
            int canvasWidth = canvas.getWidth();
            int canvasHeight = canvas.getHeight();

            double FOV_AZIMUTH = 90.0;  // Sichtfeld in Grad für Azimut (z. B. 90°)
            double FOV_ELEVATION = 60.0; // Sichtfeld in Grad für Elevation (z. B. 60°)

            double centerAzimuth = phonePitch;  // Aktuelle Blickrichtung (Azimut)
            double centerElevation = phoneRoll; // Aktuelle Blickrichtung (Elevation)

            for (Planet planet : visiblePlanets) {
                double relativeAzimuth = planet.getAzimuth() - centerAzimuth;
                double relativeElevation = planet.getElevation() - centerElevation;

                // Normalisiere Azimut-Offset (-180 bis 180)
                if (relativeAzimuth > 180) relativeAzimuth -= 360;
                if (relativeAzimuth < -180) relativeAzimuth += 360;

                // Falls ein Planet außerhalb des horizontalen FOV liegt, wrappe ihn um
                if (relativeAzimuth > 180 - FOV_AZIMUTH / 2) relativeAzimuth -= 360;
                if (relativeAzimuth < -180 + FOV_AZIMUTH / 2) relativeAzimuth += 360;

                // Falls ein Planet außerhalb des vertikalen FOV liegt, wrappe ihn um
                if (relativeElevation > 90 - FOV_ELEVATION / 2) relativeElevation -= 180;
                if (relativeElevation < -90 + FOV_ELEVATION / 2) relativeElevation += 180;

                // Normiere die Position ins Canvas-Koordinatensystem
                float x = (float) (((relativeAzimuth + (FOV_AZIMUTH / 2)) / FOV_AZIMUTH) * canvasWidth);
                float y = (float) (((-(relativeElevation) + (FOV_ELEVATION / 2)) / FOV_ELEVATION) * canvasHeight);

                Bitmap planetBitmap = planet.bitmap;
                if (planetBitmap != null) {
                    int planetSize = 100;
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(planetBitmap, planetSize, planetSize, true);
                    canvas.drawBitmap(scaledBitmap, x - planetSize / 2, y - planetSize / 2, null);
                }
            }
        }
    }


    public void drawPlanetsRollPitchNEW(Canvas canvas, List<Planet> visiblePlanets) {
        if (visiblePlanets != null && !visiblePlanets.isEmpty()) {
            int canvasWidth = canvas.getWidth();
            int canvasHeight = canvas.getHeight();

            double FOV_AZIMUTH = 90.0;  // Sichtfeld in Grad für Azimut (z. B. 90°)
            double FOV_ELEVATION = 60.0; // Sichtfeld in Grad für Elevation (z. B. 60°)

            double centerAzimuth = phonePitch;  // Aktuelle Blickrichtung (Azimut)
            double centerElevation = phoneRoll; // Aktuelle Blickrichtung (Elevation)

            for (Planet planet : visiblePlanets) {
                double relativeAzimuth = planet.getAzimuth() - centerAzimuth;
                double relativeElevation = planet.getElevation() - centerElevation;

                // Normalisiere Azimut-Offset (-180 bis 180)
                if (relativeAzimuth > 180) relativeAzimuth -= 360;
                if (relativeAzimuth < -180) relativeAzimuth += 360;

                // Falls ein Planet außerhalb des FOV liegt, überspringen
                if (Math.abs(relativeAzimuth) > FOV_AZIMUTH / 2) continue;
                if (Math.abs(relativeElevation) > FOV_ELEVATION / 2) continue;

                // Normiere die Position ins Canvas-Koordinatensystem
                float x = (float) (((relativeAzimuth + (FOV_AZIMUTH / 2)) / FOV_AZIMUTH) * canvasWidth);
                float y = (float) (((-(relativeElevation) + (FOV_ELEVATION / 2)) / FOV_ELEVATION) * canvasHeight);

                Bitmap planetBitmap = planet.bitmap;
                if (planetBitmap != null) {
                    int planetSize = 100;
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(planetBitmap, planetSize, planetSize, true);
                    canvas.drawBitmap(scaledBitmap, x - planetSize / 2, y - planetSize / 2, null);
                }
            }
        }
    }


    public void drawPlanetsRollPitchOLDWORKS(Canvas canvas, List<Planet> visiblePlanets) {
        if (visiblePlanets != null && !visiblePlanets.isEmpty()) {
            int canvasWidth = canvas.getWidth();
            int canvasHeight = canvas.getHeight();

            double centerAzimuth = phonePitch;  // Die aktuelle Blickrichtung (Azimut)
            double centerElevation = phoneRoll; // Die aktuelle Höhe (Elevation)

            for (Planet planet : visiblePlanets) {
                double relativeAzimuth = planet.getAzimuth() - centerAzimuth;
                double relativeElevation = planet.getElevation() - centerElevation;

                // Falls Azimut-Offset über 180° geht, normalisieren (-180 bis 180)
                if (relativeAzimuth > 180) relativeAzimuth -= 360;
                if (relativeAzimuth < -180) relativeAzimuth += 360;

                // Rechne Azimut und Elevation in Pixel-Koordinaten um
                float x = (float) ((relativeAzimuth / 360.0) * canvasWidth + (canvasWidth / 2));
                float y = (float) ((-relativeElevation / 180.0) * canvasHeight + (canvasHeight / 2));

                Bitmap planetBitmap = planet.bitmap;
                if (planetBitmap != null) {
                    int planetSize = 100;
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(planetBitmap, planetSize, planetSize, true);
                    canvas.drawBitmap(scaledBitmap, x - planetSize / 2, y - planetSize / 2, null);
                }
            }
        }
    }


    public void drawPlanetsRollPitchOLD(Canvas canvas, List<Planet> visiblePlanets) {
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
