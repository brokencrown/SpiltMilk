package blog.spiltmilk.spiltmilk;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Display;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jeremy on 7/14/2017.
 */

public class Utils {

    // Log tag for debugging
    private static final String LOG_TAG = Utils.class.getSimpleName();
    // URL prefix to construct URL for individual posts
    private static final String URL_PREFIX = "http://www.spiltmilk.blog/blog/";
    private static final String PAGINATION_OFFSET = "?offset=";
    private static final String URL_JSON_QUERY = "&format=json";
    private static ArrayList<Post> mPosts = new ArrayList<>();

    private Utils() {

    }

    /**
     * Use the request URL to make an HTTP connection
     * Convert JSON response to Post objects within the {@link ArrayList}
     * @param requestUrl
     * @return
     */
    public static ArrayList<Post> retrievePostData(String requestUrl) {
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        convertPosts(jsonResponse);

        return mPosts;
    }

    /**
     * Helper method to convert JSON string response into {@link JSONArray}
     * Create Post objects based on JSON objects and add them into {@link ArrayList}
     * @param jsonResponse
     * @return
     */
    private static void convertPosts(String jsonResponse) {
        ArrayList<Post> posts = new ArrayList<>();

        try {
            JSONObject postObject = new JSONObject(jsonResponse);
            JSONObject pagination = postObject.getJSONObject("pagination");
            JSONArray postItems = postObject.getJSONArray("items");

            for (int i = 0; i < postItems.length(); i++) {
                JSONObject currentPost = postItems.getJSONObject(i);

                String body = currentPost.getString("body");

                Document postDoc = Jsoup.parse(body);
                String imageUrl = postDoc.select("img").first().attr("src");

                String title = currentPost.getString("title");

                long date = currentPost.getLong("publishOn");

                Post newPost = new Post(imageUrl, body, title, date);

                posts.add(newPost);
            }

        mPosts.addAll(posts);

        if (pagination.has("nextPage") && pagination.getBoolean("nextPage") == true) {
            String pageOffset = pagination.getString("nextPageOffset");

            retrievePostData(URL_PREFIX + PAGINATION_OFFSET + pageOffset + URL_JSON_QUERY);
        }

        } catch (JSONException e) {
            Log.e("Utils", "Problem parsing the post JSON results", e);
        }
    }

    /**
     * Attempt HTTP connection and return JSON response or error code
     * @param url
     * @return
     * @throws IOException
     */
    private static String makeRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream postInputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                postInputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(postInputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the post JSON results." , e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (postInputStream != null) {
                postInputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Use {@link StringBuilder} to read from {@link InputStream} to create JSON response string
     * @param postInputStream
     * @return
     * @throws IOException
     */
    private static String readFromStream(InputStream postInputStream) throws IOException{
        StringBuilder postOutput = new StringBuilder();
        if (postInputStream != null) {
            InputStreamReader postInputStreamReader = new
                    InputStreamReader(postInputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(postInputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                postOutput.append(line);
                line = reader.readLine();
            }
        }
        return postOutput.toString();
    }

    /**
     * Create URL object from request URL and test for {@link MalformedURLException}
     * @param requestUrl
     * @return
     */
    private static URL createUrl(String requestUrl) {

        URL url = null;

        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL", e);
        }
        return url;
    }

    /**
     * Format body of post using {@link Html} to parse HTML formatting
     * Create drawables out of any <img> tags encountered
     * @param body
     * @return
     */

    public static Spanned formatBody(String body) {

        Document orginalBody = Jsoup.parse(body);
        Elements excessImageTags = orginalBody.select("img");

        for (Element element : excessImageTags) {

            if (element.hasAttr("data-src")) {
                element.remove();
            }
        }

        String modifiedBody = orginalBody.toString();

        Spanned formattedBody = Html.fromHtml(modifiedBody, 0, new Html.ImageGetter() {

            @Override
            public Drawable getDrawable(String source) {
                Drawable image;

                if (source != null) {

                    try {
                        image = Drawable.createFromStream(new URL(source).openStream(), null);

                        image.setBounds(0, 0, image.getIntrinsicWidth() * 6, image.getIntrinsicHeight() * 6);

                        // TODO implement Glide resizing of bitmaps

                        return image;
                    } catch (IOException e) {
                        Log.e("IOException", e.getMessage());
                        return null;
                    }
                } else {
                    return null;
                }
            }
        }, null);

        return formattedBody;
    }

    /**
     * Helper method for converting date
     * @param timeInMilliseconds
     * @return
     */
    public static String formatDate(Long timeInMilliseconds) {
        Date dateObject = new Date(timeInMilliseconds);
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

}
