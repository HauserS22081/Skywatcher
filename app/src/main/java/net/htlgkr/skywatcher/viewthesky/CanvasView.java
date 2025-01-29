package net.htlgkr.skywatcher.viewthesky;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.List;

public class CanvasView extends View {
    private List<Planet> planets;
    private double phoneAzimuth = 0.0;
    private double phonePitch = 0.0;
    private OnDrawCallback onDrawCallback;

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

    public void updateOrientation(double azimuth, double pitch) {
        this.phoneAzimuth = azimuth;
        this.phonePitch = pitch;
//        invalidate(); // Damit der View neu gezeichnet wird
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



//        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.mercury);
//        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
//
//        float centerX = (float) getWidth() / 2 - scaledBitmap.getWidth() / 2;
//        float centerY = (float) getHeight() / 2 - scaledBitmap.getHeight() / 2;
//
//// Bitmap zeichnen, sodass es zentriert ist
//        canvas.drawBitmap(scaledBitmap, centerX, centerY, null);


        // Zeichne Planeten, falls verfügbar
        if (planets != null && !planets.isEmpty()) {
            Log.d("CanvasView", "Zeichne " + planets.size() + " Planeten.");
            drawPlanets(canvas, planets);
        } else {
            Log.d("CanvasView", "Keine Planeten zum Zeichnen.");
        }

        // Rufe den OnDrawCallback auf, falls gesetzt
        if (onDrawCallback != null) {
            onDrawCallback.onDraw(canvas);
        }
    }

    public void drawPlanets(Canvas canvas, List<Planet> visiblePlanets) {

        if (visiblePlanets != null && !visiblePlanets.isEmpty()) {
            // Bildschirmgröße für die Darstellung auf der Canvas holen
            int canvasWidth = canvas.getWidth();
            int canvasHeight = canvas.getHeight();

            // Ein Puffer, um Planeten auch teilweise außerhalb der Grenzen anzuzeigen
            int margin = 50;  // Anzahl der Pixel, um die der Planet außerhalb des sichtbaren Bereichs erscheinen kann

            for (Planet planet : visiblePlanets) {
                // Berechne, ob der Planet sichtbar ist
                //boolean isVisible = isPlanetVisible(planet, deviceAzimuth, devicePitch, observerLatitude, observerLongitude, screenWidth, screenHeight);

                //if (isVisible) {
                    // Berechne die Differenzen zwischen Planeten-Azimut, Elevation und den aktuellen Ausrichtungen des Handys
                    double azimuthOffset = planet.getAzimuth() - phoneAzimuth;  // Differenz zwischen Planeten-Azimut und Handy-Azimut
                    double elevationOffset = planet.getElevation() - phonePitch;  // Differenz zwischen Planeten-Elevation und Handy-Elevation

                    // Berechne Azimut- und Elevationswerte für den Bildschirm
                    float x = (float) ((azimuthOffset + 180) / 360 * canvasWidth);  // Azimut-Werte auf den Bildschirmbereich abbilden (von -180 bis +180)
                    float y = (float) ((elevationOffset + 90) / 180 * canvasHeight);  // Elevation-Werte auf den Bildschirmbereich abbilden (von -90 bis +90)

                    // Sicherstellen, dass die Koordinaten innerhalb des Canvas-Bereichs liegen, auch teilweise außerhalb
                    x = Math.max(-margin, Math.min(canvasWidth + margin, x)); // Planeten können leicht außerhalb des sichtbaren Bereichs erscheinen
                    y = Math.max(-margin, Math.min(canvasHeight + margin, y)); // Planeten können leicht außerhalb des sichtbaren Bereichs erscheinen

                    // Zeichne den Planeten (Bitmap)
                    Bitmap planetBitmap = planet.bitmap;
                    if (planetBitmap != null) {
                        // Skaliere das Bild auf eine bestimmte Größe (z.B. 50x50 Pixel)
                        int planetSize = 300;
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(planetBitmap, planetSize, planetSize, true);

                        // Zeichne die Bitmap mit einer Verschiebung, sodass sie teilweise außerhalb sichtbar ist
                        canvas.drawBitmap(scaledBitmap, x - planetSize / 2, y - planetSize / 2, null); // Planet mittig an den berechneten Koordinaten zeichnen
                    }
                //}
            }
        }
    }


    public void drawPlanetsREALLYOLD(Canvas canvas, List<Planet> visiblePlanets) {

        if (visiblePlanets != null && !visiblePlanets.isEmpty()) {
            // Bildschirmgröße für die Darstellung auf der Canvas holen
            int canvasWidth = canvas.getWidth();
            int canvasHeight = canvas.getHeight();

            // Ein Puffer, um Planeten auch teilweise außerhalb der Grenzen anzuzeigen
            int margin = 50;  // Anzahl der Pixel, um die der Planet außerhalb des sichtbaren Bereichs erscheinen kann

            for (Planet planet : visiblePlanets) {
                // Berechne die Differenzen zwischen Planeten-Azimut, Elevation und den aktuellen Ausrichtungen des Handys
                double azimuthOffset = planet.getAzimuth() - phoneAzimuth;  // Differenz zwischen Planeten-Azimut und Handy-Azimut
                double elevationOffset = planet.getElevation() - phonePitch;  // Differenz zwischen Planeten-Elevation und Handy-Elevation


                // elevationoffset muss nicht größer 0 sein
                // Nur Planeten zeichnen, wenn ihre Elevation positiv ist (über dem Horizont)
                // if (elevationOffset > 0) {
                    // Normierung der Azimuth- und Elevation-Werte auf den Bildschirmbereich
                    float x = (float) (canvasWidth / 2 + azimuthOffset * (canvasWidth / 360));  // X-Position auf der Canvas
                    float y = (float) (canvasHeight / 2 - elevationOffset * (canvasHeight / 180));  // Y-Position auf der Canvas

                    // Sicherstellen, dass die Koordinaten innerhalb des Canvas-Bereichs liegen, auch teilweise außerhalb
                    x = Math.max(-margin, Math.min(canvasWidth + margin, x)); // Planeten können leicht außerhalb des sichtbaren Bereichs erscheinen
                    y = Math.max(-margin, Math.min(canvasHeight + margin, y)); // Planeten können leicht außerhalb des sichtbaren Bereichs erscheinen

                    // Zeichne den Planeten (Bitmap)
                    Bitmap planetBitmap = planet.bitmap;
                    if (planetBitmap != null) {
                        // Skaliere das Bild auf eine bestimmte Größe (z.B. 50x50 Pixel)
                        int planetSize = 300;
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(planetBitmap, planetSize, planetSize, true);

                        // Zeichne die Bitmap mit einer Verschiebung, sodass sie teilweise außerhalb sichtbar ist
                        canvas.drawBitmap(scaledBitmap, x - planetSize / 2, y - planetSize / 2, null); // Planet mittig an den berechneten Koordinaten zeichnen
                    }
                // }
            }
        }
    }

    public void drawPlanetsNew(Canvas canvas, List<Planet> visiblePlanets) {
        if (visiblePlanets == null || visiblePlanets.isEmpty()) return;

        // Bildschirmgröße holen
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        // Zeichne alle sichtbaren Planeten
        for (Planet planet : visiblePlanets) {
            // Berechne Differenz zwischen Planeten- und Handy-Orientierung
            float azimuthDiff = (float) (planet.getAzimuth() - phoneAzimuth);
            float elevationDiff = (float) (planet.getElevation() - phonePitch);

            // Azimut-Werte normalisieren (damit -180° und 180° dasselbe sind)
            if (azimuthDiff > 180) azimuthDiff -= 360;
            if (azimuthDiff < -180) azimuthDiff += 360;

            // Position auf dem Bildschirm berechnen (sanfte Übergänge)
            float x = (azimuthDiff / 60.0f) * canvasWidth / 2 + canvasWidth / 2;
            float y = canvasHeight - ((elevationDiff / 30.0f) * canvasHeight / 2 + canvasHeight / 2);

            // Begrenzung der Werte (damit Planeten am Rand bleiben)
            x = Math.max(0, Math.min(canvasWidth, x));
            y = Math.max(0, Math.min(canvasHeight, y));

            // Planeten-Bitmap holen
            Bitmap planetBitmap = planet.bitmap;
            if (planetBitmap != null) {
                int planetSize = 300; // Konstante Größe für alle Planeten
                RectF destRect = new RectF(
                        x - planetSize / 2, y - planetSize / 2,
                        x + planetSize / 2, y + planetSize / 2
                );

                // Zeichne die Bitmap an der berechneten Position
                canvas.drawBitmap(planetBitmap, null, destRect, null);
            }
        }
    }


    public void drawPlanetsNEWOLD(Canvas canvas, List<Planet> visiblePlanets) {

        if (visiblePlanets == null || visiblePlanets.isEmpty()) return;

        // Bildschirmgröße holen
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        for (Planet planet : visiblePlanets) {
            // Berechne Differenz zwischen Planeten- und Handy-Orientierung
            float azimuthDiff = (float) (planet.getAzimuth() - phoneAzimuth);
            float elevationDiff = (float) (planet.getElevation() - phonePitch);

            // Azimut-Werte normalisieren (damit -180° und 180° dasselbe sind)
            if (azimuthDiff > 180) azimuthDiff -= 360;
            if (azimuthDiff < -180) azimuthDiff += 360;

            // Position auf dem Bildschirm berechnen (sanfte Übergänge)
            float x = (azimuthDiff / 60.0f) * canvasWidth / 2 + canvasWidth / 2;
            float y = canvasHeight - ((elevationDiff / 30.0f) * canvasHeight / 2 + canvasHeight / 2);

            // Begrenzung der Werte (damit Planeten am Rand bleiben)
            x = Math.max(0, Math.min(canvasWidth, x));
            y = Math.max(0, Math.min(canvasHeight, y));

            // Planeten-Bitmap holen
            Bitmap planetBitmap = planet.bitmap;
            if (planetBitmap != null) {
                int planetSize = 100; // Konstante Größe für alle Planeten
                RectF destRect = new RectF(
                        x - planetSize / 2, y - planetSize / 2,
                        x + planetSize / 2, y + planetSize / 2
                );

                // Zeichne die Bitmap an der berechneten Position
                canvas.drawBitmap(planetBitmap, null, destRect, null);
            }
        }
    }


    // lösung:
    // die berechnung von onDrawPlanet in isVisible verwenden
    // die planetSize miteinbeziehn (Konstante machen)



    public static boolean isPlanetVisible(double planetAzimuth, double planetElevation, float deviceAzimuth, float devicePitch) {
        // Definiere eine Toleranz für den Azimut und die Elevation
        double azimuthTolerance = 10.0; // +-10 Grad für den Azimut
        double elevationTolerance = 10.0; // +-10 Grad für die Elevation

        // Überprüfe, ob der Azimut des Planeten im Bereich des Geräteazimuts liegt
        boolean azimuthInRange = Math.abs(planetAzimuth - deviceAzimuth) < azimuthTolerance;

        // Überprüfe, ob die Elevation des Planeten im Bereich der Geräte-Neigung liegt
        boolean elevationInRange = Math.abs(planetElevation - devicePitch) < elevationTolerance;

        return azimuthInRange && elevationInRange;
    }


    public interface OnDrawCallback {
        void onDraw(Canvas canvas);
    }
}

