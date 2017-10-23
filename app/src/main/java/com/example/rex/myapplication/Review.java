package com.example.rex.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rex on 20/04/2017.
 */

public class Review implements Parcelable {
    private String hillId;
    private String mTitle;
    private float mRating;
    private float mTraffic;
    private String mDescription;

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(hillId);
        parcel.writeString(mTitle);
        parcel.writeFloat(mRating);
        parcel.writeFloat(mTraffic);
        parcel.writeString(mDescription);
    }

    //reads review data passed to review class
    protected Review(Parcel in){
        hillId = in.readString();
        mTitle = in.readString();
        mRating = in.readFloat();
        mTraffic = in.readFloat();
        mDescription = in.readString();
    }

    //generates instances of review Parcelable class from a Parcel.
    public static final Creator <Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };


    public Review(){
        hillId = "";
        mTitle = "";
        mRating = 0;
        mTraffic = 0;
        mDescription = "";
    }

    public Review(String mHillId,String title, float rating, float traffic, String description){
        hillId = mHillId;
        mTitle = title;
        mRating = rating;
        mTraffic = traffic;
        mDescription = description;
    }

    public void setHillId(String hillId) {
        this.hillId = hillId;
    }

    public String getHillId() {
        return hillId;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmRating(float mRating) {
        this.mRating = mRating;
    }

    public float getmRating() {
        return mRating;
    }

    public void setmTraffic(float mTraffic) {
        this.mTraffic = mTraffic;
    }

    public float getmTraffic() {
        return mTraffic;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmDescription() {
        return mDescription;
    }

    @Override
    //parent class returns 0
    public int describeContents() {
        return 0;
    }
}
