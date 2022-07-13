package com.sesolutions.responses.feed;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by root on 15/11/17.
 */

public class LocationActivity implements Serializable {

    @SerializedName("location_id")
    private int locationId;
    @SerializedName("resource_id")
    private int resourceId;
    @SerializedName("lat")
    private String lat;
    @SerializedName("lng")
    private String lng;
    @SerializedName("resource_type")
    private String resourceType;
    @SerializedName("modified_date")
    private String modifiedDate;
    @SerializedName("venue")
    private String venue;
    private String location;
    private String title;
    private String country;
    private String zip;
    private String city;
    private String state;

    public String getLocation() {
        return location;
    }

    public String getTitle() {
        return title;
    }

    public String getCountry() {
        return country;
    }

    public String getZip() {
        return zip;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }
}
