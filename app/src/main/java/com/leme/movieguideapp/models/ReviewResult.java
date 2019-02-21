package com.leme.movieguideapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ReviewResult implements Parcelable {

    private String author;
    private String content;
    private String id;
    private String url;

    public ReviewResult() {}

    private ReviewResult(Parcel in) {
        author = in.readString();
        content = in.readString();
        id = in.readString();
        url = in.readString();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(author);
        parcel.writeString(content);
        parcel.writeString(id);
        parcel.writeString(url);
    }

    static Parcelable.Creator<ReviewResult> CREATOR = new Parcelable.Creator<ReviewResult>() {
        @Override
        public ReviewResult createFromParcel(Parcel parcel) {
            return new ReviewResult(parcel);
        }

        @Override
        public ReviewResult[] newArray(int i) {
            return new ReviewResult[i];
        }
    };

}
