package com.leme.movieguideapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class MoviesResult implements Parcelable {

    private int page;
    private int total_results;
    private int total_pages;
    private List<Movie> results;

    public MoviesResult(List<Movie> results){
        this.results = results;
    }

    private MoviesResult(Parcel in){
        page = in.readInt();
        total_results = in.readInt();
        total_pages = in.readInt();
        results = new ArrayList<Movie>();
        in.readList(results, null);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal_results() {
        return total_results;
    }

    public void setTotal_results(int total_results) {
        this.total_results = total_results;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(page);
        parcel.writeInt(total_results);
        parcel.writeInt(total_pages);
        parcel.writeList(results);
    }

    static Parcelable.Creator<MoviesResult> CREATOR = new Parcelable.Creator<MoviesResult>() {
        @Override
        public MoviesResult createFromParcel(Parcel parcel) {
            return new MoviesResult(parcel);
        }

        @Override
        public MoviesResult[] newArray(int i) {
            return new MoviesResult[i];
        }
    };
}
