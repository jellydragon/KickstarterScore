package com.fitec.crowdfunding.kickstarterscore;

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

import static com.fitec.crowdfunding.kickstarterscore.ProjectActivity.LOG_TAG;

/**
 * Helper methods related to requesting and receiving project data from the webservice.
 */
final class QueryUtils {

    private static final String SAMPLE_JSON_RESPONSE = "{\"projects\": [" +
            "{\"properties\":{\"name\": \"Pinball City Tokyo\", \"score\": 5.74, \"url\": \"https://www.kickstarter.com/projects/822278161/pinball-city-tokyo\"}}," +
            "{\"properties\":{\"name\": \"No Place For A Woman - A New Play with Music and Movement\", \"score\": 1.47, \"url\": \"https://www.kickstarter.com/projects/1997051736/no-place-for-a-woman-a-new-play-with-music-and-mov\"}}," +
            "{\"properties\":{\"name\": \"Your Appraisal & Necessary Evils\", \"score\": 9.27, \"url\": \"https://www.kickstarter.com/projects/1268497136/your-appraisal-and-necessary-evils\"}}," +
            "{\"properties\":{\"name\": \"NicoNine: small-batch handmade shoes in various sizes/widths\", \"score\": 9.85, \"url\": \"https://www.kickstarter.com/projects/niconine/niconine-shoes-for-women-by-women-in-various-sizes\"}}," +
            "{\"properties\":{\"name\": \"NAP-RITE: Your Travel Buddy\", \"score\": 1.72, \"url\": \"https://www.kickstarter.com/projects/1772112634/nap-rite-your-travel-buddy\"}}," +
            "{\"properties\":{\"name\": \"TOO MANY PJS\", \"score\": 1.48, \"url\": \"https://www.kickstarter.com/projects/toomanypjs/organic-sustainable-and-beautifully-designed-patch\"}}," +
            "{\"properties\":{\"name\": \"BURYING YASMEEN: a Post-Mortem Love Story\", \"score\": 2.87, \"url\": \"https://www.kickstarter.com/projects/1900092341/burying-yasmeen-a-post-mortem-love-story\"}}," +
            "{\"properties\":{\"name\": \"WRITTEN (Chicago Independent Film) Feature Film, Movie\", \"score\": 8.37, \"url\": \"https://www.kickstarter.com/projects/polyblack/written-chicago-independent-film-feature-film-movi\"}}," +
            "{\"properties\":{\"name\": \"Piano & Santoor - A World Music Orchestral Album!\", \"score\": 2.57, \"url\": \"https://www.kickstarter.com/projects/nikhilkmusic/piano-and-santoor-a-world-music-orchestral-album\"}}," +
            "{\"properties\":{\"name\": \"ADVENTURE CAPITAL Pilot\", \"score\": 9.68, \"url\": \"https://www.kickstarter.com/projects/1717555510/adventure-capital-pilot\"}}" +
            "]}";

    /*
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link Project} objects that has been built up from
     * parsing a JSON response.
     */
    /**
     * Query the USGS dataset and return a list of {@link Project} objects.
     */
    public static List<Project> fetchProjectData(String requestUrl) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Project}s
        //List<Project> projects = extractFeatureFromJson(jsonResponse);
        List<Project> projects = extractFeatureFromJson(SAMPLE_JSON_RESPONSE);

        // Return the list of {@link Project}s
        return projects;
    }

    /**
     * Return a list of {@link Project} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Project> extractFeatureFromJson(String projectJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(projectJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding projects to
        List<Project> projects = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(projectJSON);

            // Extract the JSONArray associated with the key called "projects",
            // which represents a list of features (or projects).
            JSONArray projectArray = baseJsonResponse.getJSONArray("projects");

            // For each earthquake in the projectArray, create an {@link Project} object
            for (int i = 0; i < projectArray.length(); i++) {

                // Get a single project at position i within the list of projects
                JSONObject currentProject = projectArray.getJSONObject(i);

                // For a given project, extract the JSONObject associated with the
                // key called "properties", which represents a list of all properties
                // for that project.
                JSONObject properties = currentProject.getJSONObject("properties");

                // Extract the value for the key called "score"
                double score = properties.getDouble("score");
                // Extract the value for the key called "name"
                String name = properties.getString("name");
                // Extract the value for the key called "url"
                String url = properties.getString("url");

                // Create a new {@link Project} object with the magnitude, location, time,
                // and url from the JSON response.
                Project project = new Project(name, score, url);
                // Add the new {@link Project} to the list of projects.
                projects.add(project);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the project JSON results", e);
        }
        // Return the list of projects
        return projects;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
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
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
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
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
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
}
