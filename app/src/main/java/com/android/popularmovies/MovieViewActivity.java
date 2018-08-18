package com.android.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.popularmovies.adapter.MoviesAdapter;
import com.android.popularmovies.model.MovieItem;
import com.android.popularmovies.repository.AppDatabase;
import com.android.popularmovies.utilities.JsonUtils;
import com.android.popularmovies.utilities.MovieSorter;
import com.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class will handle the landing page
 */
public class MovieViewActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity.class";
    private RecyclerView mRecyclerView;
    public final static String MOVIEITEM_KEY = "MOVIEITEM_KEY";
    private List<MovieItem> movieList = new ArrayList<MovieItem>();
    private MoviesAdapter favMoviesAdapter, moviesAdapter;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_view);
        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIEITEM_KEY)) {
            movieList = Arrays.asList((MovieItem[]) savedInstanceState.getParcelableArray(MOVIEITEM_KEY));
            Log.d(TAG, "Retrieved data from SaveInstanceStance");
        }
        mRecyclerView = findViewById(R.id.rv_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
        mRecyclerView.setHasFixedSize(true);
        mDb = AppDatabase.getsInstance(getApplicationContext());
        //Display popular by default
        buildAndExecute(MovieSorter.popular.name());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(MOVIEITEM_KEY, (Parcelable[]) movieList.toArray());
        Log.v(TAG, "Saving the movie item bundle");
    }

    private void showFavoriteView() {
        Log.d("Display ", " FAVORITE UI");
        setupViewModel();
        Toast.makeText(this, "Displaying your favorite list of movies", Toast.LENGTH_LONG).show();
    }

    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<MovieItem>>() {
            @Override
            public void onChanged(@Nullable List<MovieItem> movies) {
                Log.d(TAG, "Retrieving DB update as LiveData using Rooms in ViewModel");
                favMoviesAdapter = new MoviesAdapter(movies, getApplicationContext());
                mRecyclerView.setAdapter(favMoviesAdapter);
            }
        });
    }

    //Downloading data asynchronously
    public class AsyncHttpTaskForMovieDataDownload extends AsyncTask<URL, Void, List<MovieItem>> {
        @Override
        protected List<MovieItem> doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String movieSearchResults;
            try {
                movieSearchResults = NetworkUtils.getResponseFromHTTPUrl(searchUrl);
                movieList = JsonUtils.parseMovieJson(movieSearchResults);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return movieList;
        }

        @Override
        protected void onPostExecute(List<MovieItem> gridItems) {
            // Lets update UI now
            if (gridItems != null && gridItems.size() > 0) {
                moviesAdapter = new MoviesAdapter(movieList, getApplicationContext());
                mRecyclerView.setAdapter(moviesAdapter);
                moviesAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(MovieViewActivity.this, "Failed to retrieve data!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.movie_sorter, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_toprated) {
            buildAndExecute(MovieSorter.top_rated.name());
            return true;
        } else if (id == R.id.action_mostpopular) {
            buildAndExecute(MovieSorter.popular.name());
            return true;
        } else if (id == R.id.action_favourite) {
            buildAndExecute(MovieSorter.favourite.name());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * build an URL and execute in  background
     *
     * @param name
     */
    private void buildAndExecute(String name) {
        if (name.equals(MovieSorter.favourite.name())) {
            showFavoriteView();
            return;
        }
        URL movieSearchUrl = NetworkUtils.buildUrl(name);
        new AsyncHttpTaskForMovieDataDownload().execute(movieSearchUrl);
    }
}