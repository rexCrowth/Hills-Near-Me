package com.example.rex.myapplication;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

/**
 * Created by Rex on 20/04/2017.
 */

public class Hill implements Parcelable {
    private String hillId;
    private MyLatLng startCoordinates;
    private ArrayList<MyLatLng> mHillPath;
    private float mMeanRating;
    private String mSlope;
    private String mDistance;
    private String mTerrain;
    private float mTraffic;
    private ArrayList<String> reviewIds;


    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(hillId);
        parcel.writeParcelable(startCoordinates,0);
        parcel.writeTypedList(mHillPath);
        parcel.writeFloat(mMeanRating);
        parcel.writeString(mSlope);
        parcel.writeString(mDistance);
        parcel.writeString(mTerrain);
        parcel.writeFloat(mTraffic);
        parcel.writeStringList(reviewIds);
    }

    //reads monster data passed to monster class
    protected Hill(Parcel in){
        hillId = in.readString();
        startCoordinates = in.readParcelable(MyLatLng.class.getClassLoader());
        mHillPath = in.createTypedArrayList(MyLatLng.CREATOR);
        mMeanRating = in.readFloat();
        mSlope = in.readString();
        mDistance = in.readString();
        mTerrain = in.readString();
        mTraffic = in.readFloat();
        reviewIds= in.createStringArrayList();
    }

    //generates instances of Monster Parcelable class from a Parcel.
    public static final Creator <Hill> CREATOR = new Creator<Hill>() {
        @Override
        public Hill createFromParcel(Parcel in) {
            return new Hill(in);
        }

        @Override
        public Hill[] newArray(int size) {
            return new Hill[size];
        }
    };

    public Hill(){
        hillId = "";
        startCoordinates = new MyLatLng();
        mHillPath = new ArrayList<MyLatLng>();
        mMeanRating = 0;
        mSlope = "";
        mDistance = "";
        mTerrain = "";
        mTraffic = 0;
        reviewIds = new ArrayList<String>();
    }

    public Hill(String mHillId,MyLatLng mStartCoordinates,ArrayList<MyLatLng> hillPath, float meanRating, String slope, String distance, String terrain, float traffic, ArrayList<String> reviews){
        hillId = mHillId;
        startCoordinates = mStartCoordinates;
        mHillPath = hillPath;
        mMeanRating = meanRating;
        mSlope = slope;
        mDistance = distance;
        mTerrain = terrain;
        mTraffic = traffic;
        reviewIds = reviews;
    }

    public void setHillId(String hillId) {
        this.hillId = hillId;
    }

    public String getHillId() {
        return hillId;
    }

    public void setStartCoordinates(MyLatLng startCoordinates) {
        this.startCoordinates = startCoordinates;
    }

    public MyLatLng getStartCoordinates() {
        return startCoordinates;
    }

    public void setmHillPath(ArrayList<MyLatLng> mHillPath) {
        this.mHillPath = mHillPath;
    }

    public ArrayList<MyLatLng> getmHillPath() {
        return mHillPath;
    }

    public void setmMeanRating(float mMeanRating) {
        this.mMeanRating = mMeanRating;
    }

    public float getmMeanRating() {
        return mMeanRating;
    }

    public void setmSlope(String mSlope) {
        this.mSlope = mSlope;
    }

    public String getmSlope() {
        return mSlope;
    }

    public void setmDistance(String mDistance) {
        this.mDistance = mDistance;
    }

    public String getmDistance() {
        return mDistance;
    }

    public void setmTerrain(String mTerrain) {
        this.mTerrain = mTerrain;
    }

    public String getmTerrain() {
        return mTerrain;
    }

    public void setmTraffic(float mTraffic) {
        this.mTraffic = mTraffic;
    }

    public float getmTraffic() {
        return mTraffic;
    }

    public void setReviewIds(ArrayList<String> reviewIds) {
        this.reviewIds = reviewIds;
    }

    public ArrayList<String> getReviewIds() {
        return reviewIds;
    }

    @Override
    //parent class returns 0
    public int describeContents() {
        return 0;
    }
}
