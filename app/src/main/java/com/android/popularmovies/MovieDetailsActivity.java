package com.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.popularmovies.adapter.ReviewAdapter;
import com.android.popularmovies.adapter.VideoAdapter;
import com.android.popularmovies.model.MovieItem;
import com.android.popularmovies.model.Review;
import com.android.popularmovies.model.Video;
import com.android.popularmovies.repository.AppDatabase;
import com.android.popularmovies.utilities.JsonUtils;
import com.android.popularmovies.utilities.MovieProperties;
import com.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovieDetailsActivity extends AppCompatActivity {
    private final String TAG = MovieDetailsActivity.class.getSimpleName();

    private TextView title;
    private TextView rating;
    private TextView dateRelease;
    private TextView desc;
    private ImageView poster, loveIcon;
    private List<Video> trailersList = new ArrayList<>();
    private VideoAdapter videoAdapter;
    private RecyclerView vRecycleView;
    private List<Review> reviewsList = new ArrayList<>();
    private ReviewAdapter reviewAdapter;
    private RecyclerView rRecycleView;
    private Button favBtn;
    private AppDatabase mDb;
    private SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    private MovieItem mSelected;
    private static final String POSTER_IMAGE_URL = "http://image.tmdb.org/t/p/original/";

    /**
     * This method will create all necessary items for detail activity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Setting details screen layout
        setContentView(R.layout.movie_details_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViews();
        // database
        mDb = AppDatabase.getsInstance(getApplicationContext());
        initRecycleView();
        //Extract data into detail view
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("mSelected")) {
            mSelected = intent.getParcelableExtra("mSelected");
        }
        buildAndExecute(Integer.toString(mSelected.getId()), MovieProperties.videos.name());
        buildAndExecute(Integer.toString(mSelected.getId()), MovieProperties.reviews.name());
        //UI mode
        displayData();

        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (favBtn.getText().toString().equals(getString(R.string.add_to_favorite))) {
                    addToFavorite(editor);
                } else if (favBtn.getText().toString().equals(getString(R.string.remove_from_favorite))) {
                    removeFromFavorite(editor);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Formatting Date
     *
     * @param dateStr
     * @return DateString
     */
    private String formatDate(String dateStr) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "MMM d, yyyy";
        SimpleDateFormat simpleDateInputFormat, simpleDateOutputFormat;
        try {
            simpleDateInputFormat = new SimpleDateFormat(inputPattern);
            Date date = simpleDateInputFormat.parse(dateStr);
            simpleDateOutputFormat = new SimpleDateFormat(outputPattern);
            dateStr = simpleDateOutputFormat.format(date);
            return dateStr;
        } catch (ParseException pe) {

        }
        return null;
    }

    private void removeFromFavorite(SharedPreferences.Editor editor) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.movieDAO().deleteMovie(mSelected);
            }
        });
        favBtn.setText(R.string.add_to_favorite);
        loveIcon.setVisibility(View.GONE);
        editor.remove(mSelected.getTitle());
        editor.commit();
    }

    /**
     * This method will store all movie parameters and works at offline as well.
     *
     * @param editor
     */
    public void addToFavorite(SharedPreferences.Editor editor) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.movieDAO().insertMovie(mSelected);
            }
        });
        favBtn.setText(R.string.remove_from_favorite);
        loveIcon.setVisibility(View.VISIBLE);
        editor.putString(mSelected.getTitle(), "true");
        editor.commit();
    }

    /**
     * Initializing recycleview
     */
    private void initRecycleView() {
        vRecycleView = findViewById(R.id.rv_trailer);
        vRecycleView.setLayoutManager(new LinearLayoutManager(this));
        vRecycleView.setHasFixedSize(false);
        rRecycleView = findViewById(R.id.rv_review);
        rRecycleView.setLayoutManager(new LinearLayoutManager(this));
        rRecycleView.setHasFixedSize(true);
    }


    /**
     * build an URL for trailers, reviews and execute them background
     *
     * @param movieProperty
     */
    private void buildAndExecute(String id, String movieProperty) {
        URL movieSearchUrl = NetworkUtils.buildMoviePropertyURL(id, movieProperty);
        switch (movieProperty) {
            case "videos":
                new MovieDetailsActivity.AsyncHttpTaskForMovieTrailersDownload().execute(movieSearchUrl);
                break;
            case "reviews":
                new MovieDetailsActivity.AsyncHttpTaskForMovieReviewsDownload().execute(movieSearchUrl);
                break;
            default:
                break;
        }
    }

    /**
     * Downloading data asynchronously for trailers
     */
    public class AsyncHttpTaskForMovieTrailersDownload extends AsyncTask<URL, Void, List<Video>> {
        @Override
        protected List<Video> doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String movieSearchResults;
            List<Video> videoList = new ArrayList<Video>();
            try {
                movieSearchResults = NetworkUtils.getResponseFromHTTPUrl(searchUrl);
                //videoRespond = new VideoRespond();
                videoList.clear();
                videoList = JsonUtils.parseMovieTrailerJson(movieSearchResults);
                //if(0 < videoList.size()) videoRespond.setVideoslist(videoList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return videoList;
        }

        @Override
        protected void onPostExecute(List<Video> videoList) {
            // Download complete. Lets update UI
            if (videoList != null && videoList.size() > 0) {
                videoAdapter = new VideoAdapter(videoList, getApplicationContext());
                vRecycleView.setAdapter(videoAdapter);
                videoAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(MovieDetailsActivity.this, "Failed to retrieve movie trailer datas!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Downloading data asynchronously for reviews
     */
    public class AsyncHttpTaskForMovieReviewsDownload extends AsyncTask<URL, Void, List<Review>> {
        @Override
        protected List<Review> doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String movieSearchResults;
            List<Review> reviewList = new ArrayList<Review>();
            try {
                reviewList.clear();
                movieSearchResults = NetworkUtils.getResponseFromHTTPUrl(searchUrl);
                reviewList = JsonUtils.parseMovieReviewJson(movieSearchResults);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return reviewList;
        }

        @Override
        protected void onPostExecute(List<Review> reviewList) {
            // Download complete. Lets update UI
            if (reviewList != null && reviewList.size() > 0) {
                reviewAdapter = new ReviewAdapter(reviewList, getApplicationContext());
                rRecycleView.setAdapter(reviewAdapter);
                reviewAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(MovieDetailsActivity.this, "Failed to retrieve movie review datas!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Initializing all the Views
     */
    private void initViews() {
        title = findViewById(R.id.tv_detailActivity_Title);
        rating = findViewById(R.id.tv_detailActivity_Rating);
        dateRelease = findViewById(R.id.tv_detailActivity_DateRelease);
        desc = findViewById(R.id.tv_detailActivity_desc);
        poster = findViewById(R.id.tv_detailActivity_poster_image);
        favBtn = findViewById(R.id.btn_favorite);
        loveIcon = findViewById(R.id.love_icon);
    }


    /**
     * Display the datas into UI
     */
    private void displayData() {
        title.setText(mSelected.getTitle());
        rating.setText(Float.toString(mSelected.getRating()));
        dateRelease.setText(formatDate(mSelected.getReleaseDate()));
        desc.setText(mSelected.getDescription());
        Picasso.get().load(POSTER_IMAGE_URL + mSelected.getPoster()).into(poster);
        sharedPreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (!sharedPreferences.contains(mSelected.getTitle())) {
            favBtn.setText(R.string.add_to_favorite);
            loveIcon.setVisibility(View.GONE);
        } else {
            favBtn.setText(R.string.remove_from_favorite);
            loveIcon.setVisibility(View.VISIBLE);
        }
    }

}
