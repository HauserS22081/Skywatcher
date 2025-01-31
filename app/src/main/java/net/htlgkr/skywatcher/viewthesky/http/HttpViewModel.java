package net.htlgkr.skywatcher.viewthesky.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import net.htlgkr.skywatcher.R;
import net.htlgkr.skywatcher.viewthesky.Planet;
import net.htlgkr.skywatcher.viewthesky.helper.Converter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HttpViewModel extends ViewModel {
    private RequestQueue queue;
    private Context context;
    public static final String[] PLANETNAMES = new String[]{"Mercury", "Venus", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune"};
    public static final int[] PLANETPNGS = new int[]{R.drawable.mercury, R.drawable.venus, R.drawable.mars,
            R.drawable.jupiter, R.drawable.saturn, R.drawable.neptune, R.drawable.uranus};

    public void init(Context context) {
        queue = Volley.newRequestQueue(context);
        this.context = context;
    }

    public void requestData(HttpListener<List<Planet>> listener, String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onSuccess(getPlanets(response));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error.getMessage());
            }
        });

        queue.add(jsonObjectRequest);
    }

    private List<Planet> getPlanets(JSONObject response) {
        List<Planet> planets = new ArrayList<>();
        try {
            // Die Daten zu den Himmelskörpern befinden sich im "bodies"-Array
            JSONArray bodies = response.getJSONArray("bodies");

            for (int i = 0; i < bodies.length(); i++) {
                JSONObject body = bodies.getJSONObject(i);

                // Wir interessieren uns nur für Planeten
                if (body.optBoolean("isPlanet", false)) {
                    String name = body.optString("englishName", "Unbekannt");

                    int idx = getPlanetIdx(name);
                    if (idx == -1 ) {
                        continue;
                    }




//                    double latitude = body.optDouble("latitude", 0.0); // Standardwert 0.0
//                    double longitude = body.optDouble("longitude", 0.0); // Standardwert 0.0
//                    double altitude = body.optDouble("altitude", 0.0); // Standardwert 0.0

                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), PLANETPNGS[idx]);

                    // Extrahiere neue Daten aus der API
                    double semiMajorAxis = body.optDouble("semimajorAxis", 0.0);
                    double eccentricity = body.optDouble("eccentricity", 0.0);
                    double inclination = body.optDouble("inclination", 0.0);
                    double longAscNode = body.optDouble("longAscNode", 0.0);
                    double argPeriapsis = body.optDouble("argPeriapsis", 0.0);
                    double mainAnomaly = body.optDouble("mainAnomaly", 0.0);

                    // Erstelle den Planeten mit dem neuen Konstruktor
                    // String name, Bitmap bitmap, double mainAnomaly, double eccentricity,
                    // double inclination, double longAscNode, double argPeriapsis, double semiMajorAxis

                    Planet planet = new Planet(
                            name, bitmap, mainAnomaly, eccentricity, inclination, longAscNode,
                            argPeriapsis, semiMajorAxis
                    );

                    // Füge den Planeten zur Liste hinzu
                    planets.add(planet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return planets;
    }

    private int getPlanetIdx(String name) {
        for (int i = 0; i < PLANETNAMES.length; i++) {
            if (PLANETNAMES[i].equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;

    }
}
