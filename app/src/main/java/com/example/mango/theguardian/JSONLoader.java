package com.example.mango.theguardian;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class JSONLoader extends AsyncTaskLoader<String> {
    private String url;
    public JSONLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public String loadInBackground() {
        return makeHttpRequest();
    }

    // This function perform HTTP connection with server and return JSON response from server.
    private String makeHttpRequest() {
        String response = "";
        HttpURLConnection urlConnection = null;

        try {
            // Creating HTTPConnection to the given URL.
            urlConnection = (HttpURLConnection) new URL(this.url).openConnection();
            // As We don't want to post any data. setting request method as GET.
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            // Connecting to server.
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();

            // Converting InputStream to String with help of StringBuilder.
            StringBuilder stringBuilder = new StringBuilder();
            if(inputStream != null){
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = bufferedReader.readLine();
                // Iterating to each line in output and appending to string.
                while(line != null){
                    stringBuilder.append(line);
                    line = bufferedReader.readLine();
                }

                // Getting String from StringBuilder object.
                response = stringBuilder.toString();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
