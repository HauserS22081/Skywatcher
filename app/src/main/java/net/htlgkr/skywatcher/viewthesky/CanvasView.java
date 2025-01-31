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
    private double phoneRoll = 0.0;
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

    public void updateOrientation(double roll, double pitch) {
        this.phoneRoll = roll;
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
                // Berechne, ob der Planet sichtbar ist (optional, wenn die Sichtbarkeitsprüfung gewünscht wird)
                // boolean isVisible = isPlanetVisible(planet, phoneRoll, phonePitch, observerLatitude, observerLongitude, canvasWidth, canvasHeight);

                // Wenn der Planet sichtbar ist, fahre fort
                // if (isVisible) {
                // Berechne die Differenzen zwischen Planeten-Azimut, Elevation und den aktuellen Ausrichtungen des Handys
                // Berechne den Azimut (horizontal) und die Elevation (vertikal) basierend auf Roll und Pitch
                double azimuthOffset = planet.getAzimuth() - phoneRoll;  // Azimut bezieht sich auf Roll
                double elevationOffset = planet.getElevation() - phonePitch;  // Elevation bezieht sich auf Pitch

                // Berechne die Bildschirmkoordinaten
                float x = (float) ((azimuthOffset + 180) / 360 * canvasWidth);  // Roll-Werte (Azimut) auf den Bildschirm abbilden
                float y = (float) ((elevationOffset + 90) / 180 * canvasHeight);  // Pitch-Werte (Elevation) auf den Bildschirm abbilden

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

    public interface OnDrawCallback {
        void onDraw(Canvas canvas);
    }
}

