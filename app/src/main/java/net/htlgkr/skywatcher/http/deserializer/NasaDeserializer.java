package net.htlgkr.skywatcher.http.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.htlgkr.skywatcher.http.ExtendedNews;

import java.lang.reflect.Type;

public class NasaDeserializer implements JsonDeserializer<ExtendedNews> {

    @Override
    public ExtendedNews deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String title = "Daily News";
        String subtitle = jsonObject.get("title").getAsString();
        String description = jsonObject.get("explanation").getAsString();

        return new ExtendedNews(title, subtitle, description);
    }
}
