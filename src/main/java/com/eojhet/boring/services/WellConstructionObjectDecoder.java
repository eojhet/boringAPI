package com.eojhet.boring.services;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class WellConstructionObjectDecoder implements ObjectDecoder{
    private String jsonObject;
    private JSONParser parser = new JSONParser();
    private Object obj;
    private JSONObject boringData;

    public WellConstructionObjectDecoder(String jsonObject) {
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

    // Construction Well Specific Data:

    public float getStandupHeight() {
        float standupHeight = Float.parseFloat(boringData.get("standupHeight").toString());
        return standupHeight;
    }

    public float getCasingDepth() {
        float casingDepth = Float.parseFloat(boringData.get("casingDepth").toString());
        return casingDepth;
    }

    public String getCasingDescription() { return boringData.get("casingDesc").toString(); }

    public float getScreenDepth() {
        float screenDepth = Float.parseFloat(boringData.get("screenDepth").toString());
        return screenDepth;
    }

    public String getScreenDescription() { return boringData.get("screenDesc").toString(); }

    public ArrayList<Float> getMaterialDepths() {
        ArrayList<Float> depthsArray = new ArrayList<>();
        JSONArray objectArray = (JSONArray) boringData.get("materialDepths");

        for (Object depth : objectArray) {
            depthsArray.add(Float.parseFloat(depth.toString()));
        }

        return depthsArray;
    }

    public ArrayList<String> getMaterialTypes() {
        ArrayList<String> typesArray = new ArrayList<>();
        JSONArray objectArray = (JSONArray) boringData.get("materialTypes");

        for (Object type : objectArray) {
            typesArray.add(type.toString());
        }

        return typesArray;
    }

    public ArrayList<String> getMaterialDescriptions() {
        ArrayList<String> descriptionArray = new ArrayList<>();
        JSONArray objectArray = (JSONArray) boringData.get("materialDescriptions");

        for (Object type : objectArray) {
            descriptionArray.add(type.toString());
        }

        return descriptionArray;
    }
}
