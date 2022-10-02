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

    public String getLabel() {
        return boringData.get("label").toString();
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

    public static void main(String[] args) throws ParseException {
        String jsonObject = "{" +
                "\"label\":\"MW-1\"," +
                "\"logBy\":\"Joe Gonzalez\"," +
                "\"company\":\"Bay Environmental inc.\"," +
                "\"location\":\"2048 Nags Head Rd\"," +
                "\"equip\":\"Hand Auger\"," +
                "\"date\":\"2022-03-17\"," +
                "\"time\":\"10:30\"," +
                "\"depths\":[\"1\",\"3\",\"8\",\"12\",\"16\"]," +
                "\"types\":[\"topSoil\",\"sandyClay\",\"clay\",\"sand\",\"siltySand\"]," +
                "\"descriptions\":[\"Top soil\",\"Sandy clay\",\"Dark red clay\",\"Beige sand\",\"Light beige silty sand\"]}";

        BoringObjectDecoder decode = new BoringObjectDecoder(jsonObject);
        ArrayList<Float> depths = decode.getDepths();
        ArrayList<String> types = decode.getTypes();
        ArrayList<String> descriptions = decode.getDescriptions();

        System.out.println(decode.getLabel());
        System.out.println(decode.getLogBy());
        System.out.println(decode.getCompany());
        System.out.println(decode.getLocation());
        System.out.println(decode.getEquipment());
        System.out.println(decode.getDate());
        System.out.println(decode.getTime());

        for (Float depth : depths) {
            System.out.println(depth);
        }

        for (String type : types) {
            System.out.println(type);
        }

        for (String desc : descriptions) {
            System.out.println(desc);
        }


    }

}
