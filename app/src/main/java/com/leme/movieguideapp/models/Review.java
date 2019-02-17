package com.leme.movieguideapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Review implements Parcelable {

    private int id;
    private int page;
    private List<ReviewResult> results;
    private int total_pages;
    private int total_results;

    public Review(List<ReviewResult> results){
        this.results = results;
    }

    private Review(Parcel in){
        id = in.readInt();
        page = in.readInt();
        results = new ArrayList<ReviewResult>();
        total_pages = in.readInt();
        total_results = in.readInt();
        in.readList(results, null);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<ReviewResult> getResults() {
        return results;
    }

    public void setResults(List<ReviewResult> results) {
        this.results = results;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public int getTotal_results() {
        return total_results;
    }

    public void setTotal_results(int total_results) {
        this.total_results = total_results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeInt(page);
        parcel.writeList(results);
        parcel.writeInt(total_pages);
        parcel.writeInt(total_results);
    }

    static Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel parcel) {
            return new Review(parcel);
        }

        @Override
        public Review[] newArray(int i) {
            return new Review[i];
        }
    };

}
