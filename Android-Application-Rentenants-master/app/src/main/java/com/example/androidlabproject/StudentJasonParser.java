package com.example.androidlabproject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
public class StudentJasonParser {
    public static List<Student> getObjectFromJason(String jason) {
        List<Student> students;
        try {
            JSONArray jsonArray = new JSONArray(jason);
            students = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject = (JSONObject) jsonArray.get(i);
                Student student = new Student();
                student.setID(jsonObject.getInt("id"));
                student.setName(jsonObject.getString("name"));
                student.setAge(jsonObject.getDouble("age"));
                students.add(student);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return students;
    }
}
