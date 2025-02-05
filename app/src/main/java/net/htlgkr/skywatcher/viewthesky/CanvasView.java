package net.htlgkr.skywatcher.viewthesky;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.List;

public class CanvasView extends View {
    private List<Planet> visiblePlanets;
    private OnDrawCallback onDrawCallback;

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnDrawCallback(OnDrawCallback callback) {
        this.onDrawCallback = callback;
    }

    public void updateVisiblePlanets(List<Planet> visiblePlanets) {
        this.visiblePlanets = visiblePlanets;
        invalidate();
        Log.d("CanvasView", "(updateVisiblePlanets) Planeten aktualisiert: " + visiblePlanets.size());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (visiblePlanets != null && !visiblePlanets.isEmpty()) {
            Log.d("CanvasView", "Zeichne " + visiblePlanets.size() + " Planeten.");
            drawPlanetsRollPitch(canvas);
        } else {
            Log.d("CanvasView", "Keine Planeten zum Zeichnen.");
        }

        if (onDrawCallback != null) {
            onDrawCallback.onDraw(canvas);
        }
    }

    public void drawPlanetsRollPitch(Canvas canvas) {
        for (Planet planet : visiblePlanets) {

            Bitmap planetBitmap = planet.bitmap;
            if (planetBitmap != null) {
                int planetSize = 100;
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(planetBitmap, planetSize, planetSize, true);
                canvas.drawBitmap(scaledBitmap, planet.getX() - planetSize / 2, planet.getY() - planetSize / 2, null);
            }
        }

    }

    public interface OnDrawCallback {
        void onDraw(Canvas canvas);
    }
}
