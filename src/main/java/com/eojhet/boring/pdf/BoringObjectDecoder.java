package com.eojhet.boring.pdf;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class BoringObjectDecoder {
    private String jsonObject;
    private JSONParser parser = new JSONParser();
    private Object obj;
    private JSONObject boringData;

    public BoringObjectDecoder(String jsonObject) {
        this.jsonObject = jsonObject;

        try {
            obj = parser.parse(jsonObject);
            boringData = (JSONObject) obj;

        } catch (ParseException pe) {
            pe.printStackTrace();
        }
    }

    public String getId() {
        return boringData.get("id").toString();
    }

    public String getLogBy() {
        return boringData.get("logBy").toString();
    }

    public String getCompany() {
        return boringData.get("company").toString();
    }

    public String getLocation() {
        return boringData.get("location").toString();
    }

    public String getSiteName() {return boringData.get("siteName").toString(); }

    public String getEquipment() {
        return boringData.get("equip").toString();
    }

    public String getDate() {
        return boringData.get("date").toString();
    }

    public String getTime() {
        return boringData.get("time").toString();
    }

    public ArrayList<Float> getDepths() {
        ArrayList<Float> depthsArray = new ArrayList<>();
        JSONArray objectArray = (JSONArray) boringData.get("depths");

        for (Object depth : objectArray) {
            depthsArray.add(Float.parseFloat(depth.toString()));
        }

        return depthsArray;
    }

    public ArrayList<String> getTypes() {
        ArrayList<String> typesArray = new ArrayList<>();
        JSONArray objectArray = (JSONArray) boringData.get("types");

        for (Object type : objectArray) {
            typesArray.add(type.toString());
        }

        return typesArray;
    }

    public ArrayList<String> getDescriptions() {
        ArrayList<String> descriptionArray = new ArrayList<>();
        JSONArray objectArray = (JSONArray) boringData.get("descriptions");

        for (Object type : objectArray) {
            descriptionArray.add(type.toString());
        }

        return descriptionArray;
    }
}
