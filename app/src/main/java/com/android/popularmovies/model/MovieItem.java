package com.android.popularmovies.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "movieItem")
public class MovieItem implements Parcelable {

    @NonNull
    @PrimaryKey
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("poster_path")
    private String poster;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("vote_average")
    private float rating;
    @SerializedName("overview")
    private String description;

    public MovieItem(){}

    public MovieItem (@NonNull int id, String mTitle, String mPoster_path, String mDescription, float mRating, String mReleaseDate){
        this.id = id;
        this.title = mTitle;
        this.poster = mPoster_path;
        this.rating = mRating;
        this.description = mDescription;
        this.releaseDate = mReleaseDate;
    }

    protected MovieItem(Parcel source) {
        this.id = source.readInt();
        this.title = source.readString();
        this.poster = source.readString();
        this.rating = source.readFloat();
        this.description = source.readString();
        this.releaseDate = source.readString();
    }

    // interface Parcelable callback to parcel
    public static final Parcelable.Creator CREATOR = new  Parcelable.Creator(){
        @Override
        public MovieItem createFromParcel(Parcel source) {
            return new MovieItem(source);
        }
        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.poster);
        dest.writeFloat(this.rating);
        dest.writeString(this.description);
        dest.writeString(this.releaseDate);
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getPoster() {
        return poster;
    }
    public void setPoster(String poster) {
        this.poster = poster;
    }
    public String getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
    public float getRating() {
        return rating;
    }
    public void setRating(float rating) {
        this.rating = rating;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
