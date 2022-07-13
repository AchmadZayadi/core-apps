package com.sesolutions.responses.page;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class Locations {

    @SerializedName("showMap")
    private boolean showMap;
    @SerializedName("country")
    private String country;
    @SerializedName("zip")
    private String zip;
    @SerializedName("state")
    private String state;
    @SerializedName("city")
    private String city;
    @SerializedName("address2")
    private String address2;
    @SerializedName("address")
    private String address;
    @SerializedName("venue")
    private String venue;
    @SerializedName("modified_date")
    private String modified_date;
    @SerializedName("resource_type")
    private String resource_type;
    @SerializedName("lng")
    private String lng;
    @SerializedName("lat")
    private String lat;
    @SerializedName("resource_id")
    private int resource_id;
    @SerializedName("location_id")
    private int location_id;


    public String getFullAddress() {

        String str = "";
        if (!TextUtils.isEmpty(venue)) {
            return venue;
        }

        if (!TextUtils.isEmpty(address)) {
            str += address;
        }

        if (!TextUtils.isEmpty(address2)) {
            str += " " + address2;
        }

        if (!TextUtils.isEmpty(city)) {
            str += " " + city;
        }

        if (!TextUtils.isEmpty(state)) {
            str += " " + state;
        }

        if (!TextUtils.isEmpty(country)) {
            str += " " + country;
        }
       /* "venue":"Spaze i-Tech Park, Block S, Sector 49, Gurugram, Haryana",
                "address":"Tower B4, Spaze iTech Park",
                "address2":"Sector 49, Sohna Road",
                "city":"Gurugram",
                "state":"Haryana",
                "zip":"122018",
                "country":"India",*/

        return str;
    }

    public boolean canShowMap() {
        return showMap;
    }

    public String getCountry() {
        return country;
    }

    public String getZip() {
        return zip;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getAddress2() {
        return address2;
    }

    public String getAddress() {
        return address;
    }

    public String getVenue() {
        return venue;
    }

    public String getModified_date() {
        return modified_date;
    }

    public String getResource_type() {
        return resource_type;
    }

    public String getLng() {
        return lng;
    }

    public String getLat() {
        return lat;
    }

    public int getResource_id() {
        return resource_id;
    }

    public int getLocation_id() {
        return location_id;
    }
}
