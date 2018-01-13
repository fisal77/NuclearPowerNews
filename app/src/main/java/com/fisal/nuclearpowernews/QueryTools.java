package com.fisal.nuclearpowernews;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fisal on 13/01/2018.
 * Helper methods related to requesting and receiving Nuclear Power news data from web url.
 */

public final class QueryTools {

    private static final String LOG_TAG = QueryTools.class.getSimpleName();

    private QueryTools(){
    }

    /**
     * Query the URL dataset and return a list of {@link NuclearPower} objects.
     */
    public static List<NuclearPower> fetchNuclearPowerData(String webURL) {

        Log.i(LOG_TAG, "TEST: fetchNuclearPowerData() called ...");

        /**
         * forcing the background thread to pause execution and wait for 2 seconds (which is 2000 milliseconds),
         * before proceeding to execute the rest of lines of code in this method.
         * If you try to add the Thread.sleep(2000); method call by itself,
         * Android Studio will complain that there is an uncaught exception,
         * so we need to surround that statement with a try/catch block.
         */
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Generate URL object from given URL string
        URL url = createURL(webURL);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link nuclearPower}s
        List<NuclearPower> nuclearPowers = extractResultsFromJson(jsonResponse);

        // Return the list of {@link nuclearPower}s
        return nuclearPowers;
    }

    private static URL createURL(String stringURL) {
        URL url = null;
        try {
            url = new URL(stringURL);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem generating the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // If the request was successful (response code 200),
            // then read the input stream and parse the response.

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the nuclear power news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<NuclearPower> extractResultsFromJson(String nuclearPowerNewsJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(nuclearPowerNewsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding nuclearPowers to
        List<NuclearPower> nuclearPowers = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(nuclearPowerNewsJSON);

            // Extract the JSON object associated with the key called "response",
            // which represent the "results" JSON array.
            JSONObject response = baseJsonResponse.getJSONObject("response");

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of results (or nuclearPowers).
            JSONArray nuclearPowerArray = response.getJSONArray("results");

            for (int i = 0; i < nuclearPowerArray.length(); i++) {
                JSONObject currentNuclearPowerNews = nuclearPowerArray.getJSONObject(i);

                // Extract the value for the key called "sectionName"
                String section = currentNuclearPowerNews.getString("sectionName");

                // Extract the value for the key called "webTitle"
                String title = currentNuclearPowerNews.getString("webTitle");

                // Extract the value for the key called "webPublicationDate"
                String date = currentNuclearPowerNews.getString("webPublicationDate");

                // Extract the value for the key called "webUrl"
                String url = currentNuclearPowerNews.getString("webUrl");

                // Create a new {@link NuclearPower} object with the section, title, date
                // and url from the JSON response.
                NuclearPower nuclearPower = new NuclearPower(section, title, date, url);
                nuclearPowers.add(nuclearPower);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryTools", "Problem parsing the nuclear power news JSON results", e);
        }

        return nuclearPowers;
    }

}
