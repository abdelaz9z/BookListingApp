package com.example.booklistingapp;

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

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    public QueryUtils() {
    }

    /**
     * Query the USGS dataset and return a list of {@link Book} objects.
     */
    static List<Book> fetchBookData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Book}s
        // Return the list of {@link Book}s
        return extractFeatureFromJson(jsonResponse);
    }

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Book> extractFeatureFromJson(String bookJSON) {

        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        List<Book> books = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(bookJSON);
            JSONArray itemsJsonArray = baseJsonResponse.getJSONArray("items");
            for (int i = 0; i < itemsJsonArray.length(); i++) {
                JSONObject currentBook = itemsJsonArray.getJSONObject(i);

                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                // Title
                String title = volumeInfo.getString("title");

                // Authors
                String author;
                if (volumeInfo.has("authors")) {
                    JSONArray authorsJsonArray = volumeInfo.getJSONArray("authors");

                    if (!volumeInfo.isNull("authors")) {
                        author = (String) authorsJsonArray.get(0);
                    } else {
                        author = "unknown author";
                    }

                } else {
                    author = "missing info of authors";
                }

                // Published date
                String publishedDate;
                if (volumeInfo.has("publishedDate")) {
                    publishedDate = volumeInfo.getString("publishedDate");
                } else {
                    publishedDate = "unknown published date";
                }

                // Ratings count
                double ratingsCount;
                if (!volumeInfo.isNull("ratingsCount")) {
                    ratingsCount = volumeInfo.getDouble("ratingsCount");
                } else {
                    ratingsCount = 0;
                }

                JSONObject saleInfo = currentBook.getJSONObject("saleInfo");

                // Amount & Currency code
                double amount;
                String currencyCode;
                if (saleInfo.has("listPrice")) {
                    JSONObject listPrice = saleInfo.getJSONObject("listPrice");
                    if (!saleInfo.isNull("listPrice")) {
                        amount = listPrice.getDouble("amount");
                        currencyCode = listPrice.getString("currencyCode");
                    } else {
                        amount = 0;
                        currencyCode = "";
                    }
                } else {
                    amount = 0.0;
                    currencyCode = "Free";
                }

                // Language
                String language = volumeInfo.getString("language");

                // buy link
                String buyLink;
                if (saleInfo.has("buyLink")) {
                    buyLink = saleInfo.getString("buyLink");
                } else {
                    buyLink = null;
                }

                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                String smallThumbnail = imageLinks.getString("smallThumbnail").replace("http", "https");

                Book book = new Book(smallThumbnail, title, author, publishedDate, ratingsCount, language, amount, currencyCode, buyLink);
                books.add(book);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the books JSON results", e);
        }

        // Return the list of books
        return books;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
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
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
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

}
