package com.android.popularmovies.repository;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.android.popularmovies.model.MovieItem;

import java.util.List;

@Dao
public interface MovieDAO {

    @Query("SELECT * FROM movieItem")
    LiveData<List<MovieItem>> loadAllMovies();

    @Insert
    void insertMovie(MovieItem movie);

    @Delete
    void deleteMovie(MovieItem movie);
}
