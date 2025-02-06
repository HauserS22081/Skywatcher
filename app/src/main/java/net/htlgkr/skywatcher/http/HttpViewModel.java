package net.htlgkr.skywatcher.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.lifecycle.ViewModel;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.htlgkr.skywatcher.R;
import net.htlgkr.skywatcher.http.deserializer.SkyObjectDeserializer;
import net.htlgkr.skywatcher.news.ExtendedNews;
import net.htlgkr.skywatcher.http.deserializer.NasaDeserializer;
import net.htlgkr.skywatcher.skyobjectlist.SkyObject;
import net.htlgkr.skywatcher.viewthesky.Planet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HttpViewModel extends ViewModel {
    private RequestQueue queue; // nur 1 mal initialisiert werden
    private Context context;
    public static final String[] PLANETNAMES = new String[]{"Mercury", "Venus", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune"};
    public static final int[] PLANETPNGS = new int[]{R.drawable.mercury, R.drawable.venus, R.drawable.mars,
            R.drawable.jupiter, R.drawable.saturn, R.drawable.neptune, R.drawable.uranus};


    // ladescreen damit nicht null

    public void init(Context context) {
        queue = Volley.newRequestQueue(context);
        this.context = context;
    }



    // News
    public void requestDailyInfos(HttpListener<ExtendedNews> listener) {
        String apiKey = "00UaO6lIPm4VjPd1lzwU0y9N7sEzg63GcAIvq02o";
        String url = "https://api.nasa.gov/planetary/apod?api_key=";

        url += apiKey;


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Gson gson = new GsonBuilder()
                                .registerTypeAdapter(ExtendedNews.class, new NasaDeserializer())
                                .create();

                        ExtendedNews news = gson.fromJson(response, ExtendedNews.class);

                        listener.onSuccess(news);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error.getMessage());
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    public void requestSpaceNews(HttpListener<List<ExtendedNews>> listener) {
        String url = "https://spacenews.p.rapidapi.com/news";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // JSON-Array aus der API-Antwort parsen
                            JSONArray jsonArray = new JSONArray(response);

                            // Liste für ExtendedNews
                            List<ExtendedNews> newsList = new ArrayList<>();

                            // Durch alle Elemente im JSON-Array iterieren
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                // JSON-Felder extrahieren
                                String title = jsonObject.optString("title", "No Title");
                                String subtitle = jsonObject.optString("timestamp", "No Date");
                                String description = jsonObject.optString("news_text", "No Summary");

                                // Neues ExtendedNews-Objekt erstellen
                                ExtendedNews news = new ExtendedNews(title, subtitle, description);

                                // Zur Liste hinzufügen
                                newsList.add(news);
                            }

                            // Liste an den Listener übergeben
                            listener.onSuccess(newsList);

                        } catch (JSONException e) {
                            // Fehler beim Parsen der JSON-Daten
                            listener.onError("JSON Parsing Error: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Fehler an den Listener weitergeben
                listener.onError(error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-rapidapi-key", "551143c2dcmshe92aef6d4158010p183248jsndf852c1743bc");
                headers.put("x-rapidapi-host", "spacenews.p.rapidapi.com");
                return headers;
            }
        };

        // Anfrage zur Warteschlange hinzufügen
        queue.add(stringRequest);
    }


    public void requestSpaceTodaysNews(HttpListener<List<ExtendedNews>> listener) {
        String url = "https://spacenews.p.rapidapi.com/newstoday";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // JSON-Array aus der API-Antwort parsen
                            JSONArray jsonArray = new JSONArray(response);

                            // Liste für ExtendedNews
                            List<ExtendedNews> newsList = new ArrayList<>();

                            // Durch alle Elemente im JSON-Array iterieren
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                // JSON-Felder extrahieren
                                String title = jsonObject.optString("title", "No Title");
                                String subtitle = jsonObject.optString("timestamp", "No Date");
                                String description = jsonObject.optString("news_text", "No Summary");

                                // Neues ExtendedNews-Objekt erstellen
                                ExtendedNews news = new ExtendedNews(title, subtitle, description);

                                // Zur Liste hinzufügen
                                newsList.add(news);
                            }

                            // Liste an den Listener übergeben
                            listener.onSuccess(newsList);

                        } catch (JSONException e) {
                            // Fehler beim Parsen der JSON-Daten
                            listener.onError("JSON Parsing Error: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Fehler an den Listener weitergeben
                listener.onError(error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-rapidapi-key", "551143c2dcmshe92aef6d4158010p183248jsndf852c1743bc");
                headers.put("x-rapidapi-host", "spacenews.p.rapidapi.com");
                return headers;
            }
        };

        // Anfrage zur Warteschlange hinzufügen
        queue.add(stringRequest);
    }



    public void requestSpaceDayArticles(HttpListener<List<ExtendedNews>> listener) {
        String url = "https://spacenews.p.rapidapi.com/datenews/1";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // JSON-Array aus der API-Antwort parsen
                            JSONArray jsonArray = new JSONArray(response);

                            // Liste für ExtendedNews
                            List<ExtendedNews> newsList = new ArrayList<>();

                            // Durch alle Elemente im JSON-Array iterieren
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                // JSON-Felder extrahieren
                                String title = jsonObject.optString("title", "No Title");
                                String subtitle = jsonObject.optString("timestamp", "No Date");
                                String description = jsonObject.optString("news_text", "No Summary");

                                // Neues ExtendedNews-Objekt erstellen
                                ExtendedNews news = new ExtendedNews(title, subtitle, description);

                                // Zur Liste hinzufügen
                                newsList.add(news);
                            }

                            // Liste an den Listener übergeben
                            listener.onSuccess(newsList);

                        } catch (JSONException e) {
                            // Fehler beim Parsen der JSON-Daten
                            listener.onError("JSON Parsing Error: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Fehler an den Listener weitergeben
                listener.onError(error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-rapidapi-key", "551143c2dcmshe92aef6d4158010p183248jsndf852c1743bc");
                headers.put("x-rapidapi-host", "spacenews.p.rapidapi.com");
                return headers;
            }
        };

        // Anfrage zur Warteschlange hinzufügen
        queue.add(stringRequest);
    }





    // View The Sky
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
            JSONArray bodies = response.getJSONArray("bodies");

            for (int i = 0; i < bodies.length(); i++) {
                JSONObject body = bodies.getJSONObject(i);

                if (body.optBoolean("isPlanet", false)) {
                    String name = body.optString("englishName", "Unbekannt");

                    int idx = getPlanetIdx(name);
                    if (idx == -1 ) {
                        continue;
                    }


                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), PLANETPNGS[idx]);

                    double semiMajorAxis = body.optDouble("semimajorAxis", 0.0);
                    double eccentricity = body.optDouble("eccentricity", 0.0);
                    double inclination = body.optDouble("inclination", 0.0);
                    double longAscNode = body.optDouble("longAscNode", 0.0);
                    double argPeriapsis = body.optDouble("argPeriapsis", 0.0);
                    double mainAnomaly = body.optDouble("mainAnomaly", 0.0);


                    Planet planet = new Planet(
                            name, bitmap, mainAnomaly, eccentricity, inclination, longAscNode,
                            argPeriapsis, semiMajorAxis
                    );

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




    // SkyObjectList
//    public void requestPlanetInfo(HttpListener<List<SkyObject>> listener) {
//        String url = "https://api.le-systeme-solaire.net/rest/bodies/mars"; // You can replace "mars" with any planet ID.
//
//        // Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                        Gson gson = new GsonBuilder()
//                                .registerTypeAdapter(SkyObject.class, new SkyObjectDeserializer())
//                                .create();
//
//                        SkyObject skyObject = gson.fromJson(response, SkyObject.class);
//
//                        listener.onSuccess(skyObject);
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                listener.onError(error.getMessage());
//            }
//        });
//        queue.add(stringRequest);
//    }

    public void requestPlanetInfo(HttpListener<ArrayList<SkyObject>> listener) {
        String url = "https://api.le-systeme-solaire.net/rest/bodies"; // Endpoint to fetch all bodies including planets

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Gson gson = new GsonBuilder()
                                    .registerTypeAdapter(SkyObject.class, new SkyObjectDeserializer())
                                    .create();

                            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
                            JsonArray bodies = jsonResponse.getAsJsonArray("bodies");

                            ArrayList<SkyObject> skyObjects = new ArrayList<>();

                            for (JsonElement bodyElement : bodies) {
                                SkyObject skyObject = gson.fromJson(bodyElement, SkyObject.class);
                                if (skyObject != null) {
                                    skyObjects.add(skyObject);
                                }
                            }

                            listener.onSuccess(skyObjects);

                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.onError(e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error.getMessage());
            }
        });
        queue.add(stringRequest);
    }


}
