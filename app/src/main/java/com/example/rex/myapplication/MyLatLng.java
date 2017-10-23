package com.example.rex.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rex on 17/05/2017.
 */

public class MyLatLng implements Parcelable{
    private Double latitude;
    private Double longitude;

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }

    //reads monster data passed to monster class
    protected MyLatLng(Parcel in){
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    //generates instances of Monster Parcelable class from a Parcel.
    public static final Creator <MyLatLng> CREATOR = new Creator<MyLatLng>() {
        @Override
        public MyLatLng createFromParcel(Parcel in) {
            return new MyLatLng(in);
        }

        @Override
        public MyLatLng[] newArray(int size) {
            return new MyLatLng[size];
        }
    };

    public MyLatLng(double mlatitude, double mlongitude) {
        latitude = mlatitude;
        longitude = mlongitude;
    }

    public MyLatLng(){}

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override
    //parent class returns 0
    public int describeContents() {
        return 0;
    }
}
