package com.project.ams.automatedmess;

/**
 * Created by Amuri Bonface on 3/20/2018.
 */

public class CordinatesModel {
    private double mProviderLatitude;
    private double mProviderLongitude;
    private String mProviderBrandName;

public CordinatesModel(){

}

    public CordinatesModel(double mProviderLatitude, double mProviderLongitude, String mProviderBrandName) {
        this.mProviderLatitude = mProviderLatitude;
        this.mProviderLongitude = mProviderLongitude;
        this.mProviderBrandName = mProviderBrandName;
    }

    public double getmProviderLatitude() {
        return mProviderLatitude;
    }

    public void setmProviderLatitude(double mProviderLatitude) {
        this.mProviderLatitude = mProviderLatitude;
    }

    public double getmProviderLongitude() {
        return mProviderLongitude;
    }

    public void setmProviderLongitude(double mProviderLongitude) {
        this.mProviderLongitude = mProviderLongitude;
    }

    public String getmProviderBrandName() {
        return mProviderBrandName;
    }

    public void setmProviderBrandName(String mProviderBrandName) {
        this.mProviderBrandName = mProviderBrandName;
    }
}

