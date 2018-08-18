package com.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Video implements Parcelable {
    /*
     "id":"5794fffbc3a36829ab00056f",
     "iso_639_1":"en",
     "iso_3166_1":"US",
     "key":"2LqzF5WauAw",
     "name":"Interstellar Movie - Official Trailer",
     "site":"YouTube",
     "size":1080,
     "type":"Trailer"
     */

    @SerializedName("id")
    private String id;
    // missing iso..
    @SerializedName("key")
    private String key;
    @SerializedName("name")
    private String name;
    @SerializedName("site")
    private String site;
    @SerializedName("size")
    private int size;
    @SerializedName("type")
    private String type;

    // constructor
    public Video(String id, String key, String name, String site, int size, String type) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
        this.type = type;
    }

    //getter
    public String getId() { return id; }
    public String getKey() { return key; }
    public String getName() { return name; }
    public String getSite() { return site; }
    public int getSize() { return size; }
    public String getType() { return type; }
    public String getUrl () {
        String url = "https://www.youtube.com/watch?v="+getKey();
        return url;
    }

    //setter
    public void setId(String id) { this.id = id; }
    public void setKey(String key) { this.key = key; }
    public void setName(String name) { this.name = name; }
    public void setSite(String site) { this.site = site; }
    public void setSize(int size) { this.size = size; }
    public void setType(String type) { this.type = type; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.key);
        dest.writeString(this.name);
        dest.writeString(this.site);
        dest.writeInt(this.size);
        dest.writeString(this.type);
    }

    // constructor parcel
    protected Video(Parcel in) {
        this.id = in.readString();
        this.key = in.readString();
        this.name = in.readString();
        this.site = in.readString();
        this.size = in.readInt();
        this.type = in.readString();
    }

    //callback to parcel
    public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel source) {
            return new Video(source);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}
