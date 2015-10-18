package com.swen900014.orange.rideshareoz.models;

import java.io.Serializable;
import java.util.StringTokenizer;


/**
 * Created by Sangzhuoyang Yu & George on 9/11/15.
 * Encapsulate location data.
 */
public class Location implements Serializable
{
    private Double lat;
    private Double lon;
    private String address;
    private String suburb;

    /*
    Display name is the suburb unless it is a landmark
     */
    private String displayName;

    private String extractDisplayName(String address)
    {
        //fix the address retained from current location
        if ((!address.contains(", VIC")) && (address.contains(" VIC")))
        {
            address = address.replace(" VIC", ", VIC");
        }

        String name = "";
        StringTokenizer st = new StringTokenizer(address, ",");
        int count = st.countTokens();


        //get the suburb name
        for (int i = 0; i < count - 2; i++)
        {
            name = st.nextToken();
        }

        return name;
    }

    public Location(Double lat, Double lon, String address)
    {
        this.lat = lat;
        this.lon = lon;
        this.address = address;
        this.displayName = extractDisplayName(address);
    }

    public Location(Double lat, Double lon)
    {
        this.lat = lat;
        this.lon = lon;
    }

    public void setLat(Double lat)
    {
        this.lat = lat;
    }

    public void setLon(Double lon)
    {
        this.lon = lon;
    }

    public Double getLat()
    {
        return lat;
    }

    public Double getLon()
    {
        return lon;
    }

    public String getSuburb()
    {
        return suburb;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public void setSuburb(String suburb)
    {
        this.suburb = suburb;
    }

    public String getDisplayName()
    {
        return displayName;
    }
}
