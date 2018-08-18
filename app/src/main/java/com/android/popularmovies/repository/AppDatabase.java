package com.android.popularmovies.repository;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import com.android.popularmovies.model.MovieItem;

@Database(entities = {MovieItem.class} , version = 1 , exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "moviefavorDB";
    private static AppDatabase sInstance;

    public static AppDatabase getsInstance(Context context){
        // first time if the sInstance is null then create a sInstance
        if (sInstance == null){
            synchronized (LOCK){
                Log.d(TAG, "First time creating sInstance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class, AppDatabase.DATABASE_NAME)
                            .allowMainThreadQueries()
                            .build();
            }
        }
        Log.d(TAG, "Getting the sInstance");
        return sInstance;
    }
    public abstract MovieDAO movieDAO();

}
