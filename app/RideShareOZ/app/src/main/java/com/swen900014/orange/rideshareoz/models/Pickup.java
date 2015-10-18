package com.swen900014.orange.rideshareoz.models;

import java.io.Serializable;


/**
 * Created by George on 16/09/2015.
 * It is used in the join request, where each
 * passenger is bound with a pickup location
 */
public class Pickup implements Serializable
{
    private User user;
    private Location location;
    private boolean ratedByDriver; // Whether this passenger has been rated by driver
    private boolean driverRated;   // Whether this ride(driver) has been rated

    public Pickup(User user, Location location)
    {
        this.user = user;
        this.location = location;
    }

    public Pickup(User user, Location location, boolean ratedByDriver, boolean driverRated)
    {
        this.user = user;
        this.location = location;
        this.ratedByDriver = ratedByDriver;
        this.driverRated = driverRated;
    }

    public void setRatedByDriver(boolean ratedByDriver)
    {
        this.ratedByDriver = ratedByDriver;
    }

    public void setDriverRated(boolean driverRated)
    {
        this.driverRated = driverRated;
    }

    public Location getLocation()
    {
        return location;
    }

    public User getUser()
    {
        return user;
    }

    public boolean isDriverRated()
    {
        return driverRated;
    }

    public boolean isRatedByDriver()
    {
        return ratedByDriver;
    }
}
