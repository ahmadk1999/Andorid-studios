package com.example.androidlabproject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class PropertiesJasonParser {
    public static List<Agency> getObjectFromJason(String jason) {
        List<Agency> agencies;
        try {
            JSONArray jsonArray = new JSONArray(jason);
            agencies = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject = (JSONObject) jsonArray.get(i);
                Agency agency = new Agency();
                agency.setName(jsonObject.getString("name"));
                agency.setCountry(jsonObject.getString("country"));
                agency.setPhoneNumber(jsonObject.getString("phone number"));
                agency.setCity(jsonObject.getString("city"));
                agency.setPassword(jsonObject.getString("password"));
                agencies.add(agency);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return agencies;
    }
}