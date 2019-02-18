package com.leme.movieguideapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Video implements Parcelable {

    private int id;
    private List<VideoResult> results;

    public Video(List<VideoResult> results) {
        this.results = results;
    }

    private Video(Parcel in) {
        id = in.readInt();
        results = new ArrayList<VideoResult>();
        in.readList(results, null);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<VideoResult> getResults() {
        return results;
    }

    public void setResults(List<VideoResult> results) {
        this.results = results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeList(results);
    }

    static Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel parcel) {
            return new Video(parcel);
        }

        @Override
        public Video[] newArray(int i) {
            return new Video[i];
        }
    };

}
