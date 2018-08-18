package com.android.popularmovies.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    private final static String MOVIEDB_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private final static String PARAM_QUERY = "api_key";
    //API_Key
    // TODO KEYIN APIKEY HERE
    private final static String API_KEY = "869ba8ce18967f203b2686832bc98be9";
    private static String paramPath = "";

    public static URL buildUrl(String movieDataSearchQuery){
        switch (movieDataSearchQuery) {
            case "popular" : paramPath = MovieSorter.popular.name();
                             break;
            case "top_rated" : paramPath = MovieSorter.top_rated.name();
                               break;
            case "favourite" : paramPath = MovieSorter.favourite.name();
                break;
            default: paramPath = MovieSorter.popular.name();
                     break;
        }
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL+paramPath).buildUpon()
                .appendQueryParameter(PARAM_QUERY, API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildMoviePropertyURL(String id, String movieProperty){
        switch (movieProperty) {
            case "videos" : paramPath = MovieProperties.videos.name();
                break;
            case "reviews" : paramPath = MovieProperties.reviews.name();
                break;
            default: break;
        }
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL+id+"/"+paramPath).buildUpon()
                .appendQueryParameter(PARAM_QUERY, API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

    //https://api.themoviedb.org/3/movie/5b5b3d24c3a368670c015b12/videos

    public static String getResponseFromHTTPUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
