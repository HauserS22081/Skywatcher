package net.htlgkr.skywatcher.http.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.htlgkr.skywatcher.skyobjectlist.SkyObject;

import java.lang.reflect.Type;

public class SkyObjectDeserializer implements JsonDeserializer<SkyObject> {

    @Override
    public SkyObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String name = jsonObject.get("englishName").getAsString();
        double gravity = jsonObject.get("gravity").getAsDouble();
        double avgTemp = jsonObject.get("avgTemp").getAsDouble();
        String bodyType = jsonObject.get("bodyType").getAsString();

        JsonElement moonsElement = jsonObject.get("moons");
        int moonsCount = 0;

        if (moonsElement != null && moonsElement.isJsonArray()) {
            moonsCount = moonsElement.getAsJsonArray().size();
        }

        return new SkyObject(name, gravity, avgTemp, moonsCount);
    }
}
