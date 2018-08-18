package com.android.popularmovies.utilities;

import com.android.popularmovies.model.MovieItem;
import com.android.popularmovies.model.Review;
import com.android.popularmovies.model.Video;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonUtils {
    private static String MOVIE_ID = "id";
    private static String MOVIE_RATING = "vote_average";
    private static String MOVIE_TITLE = "title";
    private static String MOVIE_POSTER = "poster_path";
    private static String MOVIE_OVERVIEW = "overview";
    private static String MOVIE_RELEASE_DATE = "release_date";
    private static String MOVIE_KEY = "key";
    private static String MOVIE_NAME = "name";
    private static String MOVIE_SITE = "site";
    private static String MOVIE_SIZE = "size";
    private static String MOVIE_TYPE = "type";
    private static String MOVIE_AUTHOR = "author";
    private static String MOVIE_CONTENT = "content";
    private static String MOVIE_URL = "url";
    private static String MOVIE_RESULTS = "results";
    private List<Video> trailersList = new ArrayList<>();

    public static List<MovieItem> parseMovieJson(String json) throws JSONException {
        JSONObject movieDetails = new JSONObject(json);
        JSONArray moviesResults = movieDetails.getJSONArray(MOVIE_RESULTS);
        MovieItem[] movieArray = new MovieItem[moviesResults.length()];
        for (int i = 0; i <moviesResults.length(); i++){
            JSONObject movieData = moviesResults.getJSONObject(i);
            movieArray[i] = createMovieObject(movieData);
        }
        return Arrays.asList(movieArray);
    }

    private static MovieItem createMovieObject(JSONObject movieData){
        MovieItem movie = new MovieItem(movieData.optInt(MOVIE_ID),
                movieData.optString(MOVIE_TITLE),
                movieData.optString(MOVIE_POSTER),
                movieData.optString(MOVIE_OVERVIEW),
                movieData.optInt(MOVIE_RATING),
                movieData.optString(MOVIE_RELEASE_DATE));
        return movie;
    }

    public static List<Video> parseMovieTrailerJson(String json) throws JSONException {
        JSONObject movieVideoDetails = new JSONObject(json);
        JSONArray movieVideosResults = movieVideoDetails.getJSONArray(MOVIE_RESULTS);
        Video[] movieVideoArray = new Video[movieVideosResults.length()];
        for (int i = 0; i <movieVideosResults.length(); i++){
            JSONObject movieVideoData = movieVideosResults.getJSONObject(i);
            movieVideoArray[i] = createVideoObject(movieVideoData);
        }
        return Arrays.asList(movieVideoArray);
    }

    private static Video createVideoObject(JSONObject movieVideoData){
        Video video = new Video(movieVideoData.optString(MOVIE_ID),
                movieVideoData.optString(MOVIE_KEY),
                movieVideoData.optString(MOVIE_NAME),
                movieVideoData.optString(MOVIE_SITE),
                movieVideoData.optInt(MOVIE_SIZE),
                movieVideoData.optString(MOVIE_TYPE));
        return video;
    }

    public static List<Review> parseMovieReviewJson(String json) throws JSONException {
        JSONObject movieReviewDetails = new JSONObject(json);
        JSONArray movieReviewResults = movieReviewDetails.getJSONArray(MOVIE_RESULTS);
        Review[] movieReviewArray = new Review[movieReviewResults.length()];
        for (int i = 0; i <movieReviewResults.length(); i++){
            JSONObject movieReviewData = movieReviewResults.getJSONObject(i);
            movieReviewArray[i] = createReviewObject(movieReviewData);
        }
        return Arrays.asList(movieReviewArray);
    }

    private static Review createReviewObject(JSONObject movieReviewData){
        Review review = new Review(movieReviewData.optString(MOVIE_AUTHOR),
                movieReviewData.optString(MOVIE_CONTENT),
                movieReviewData.optString(MOVIE_ID),
                movieReviewData.optString(MOVIE_URL));
        return review;
    }
}

