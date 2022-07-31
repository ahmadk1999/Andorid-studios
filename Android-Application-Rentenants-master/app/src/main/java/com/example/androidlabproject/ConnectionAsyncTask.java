package com.example.androidlabproject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

public class ConnectionAsyncTask extends AsyncTask<String, String,
        String> {
    Activity activity;
    public ConnectionAsyncTask(Activity activity) {
        this.activity = activity;
    }
    @Override
    protected void onPreExecute() {
        ((MainActivity) activity).setButtonText("Connecting");
        super.onPreExecute();
        ((MainActivity) activity).setProgress(true);
    }
    @Override
    protected String doInBackground(String... params) {
        String data = HttpManager.getData(params[0]);
        return data;
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        ((MainActivity) activity).setProgress(false);
        ((MainActivity) activity).setButtonText("Connected");
        Toast.makeText(((MainActivity) activity), "Connect Successfully!, Redirecting to Home page", Toast.LENGTH_LONG).show();

    }
}