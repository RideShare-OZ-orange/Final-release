package com.swen900014.orange.rideshareoz.models;

import com.swen900014.orange.rideshareoz.utils.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.io.Serializable;


/**
 * Created by George & Sangzhuoyang Yu on 9/6/15.
 * Encapsulate ride data, including all info needed
 * for a ride, matching the ride data stored on server
 */
public class Ride implements Serializable
{
    private String rideId;
    private Location start;
    private Location end;
    private Group group;
    private String gourpOrEventName;
    private String arriving_time;
    private String start_time;
    private User driver;
    private int seats;          // Max number of passengers who can join

    private ArrayList<Pickup> joined;   //joined passengers
    private ArrayList<Pickup> waiting;  //passengers who is waiting
    private RideState rideState = RideState.NEW;
    private RideRegularity rideRegularity = RideRegularity.ONCE;

    public static ArrayList<Ride> allRides;

    public enum RideRegularity implements Serializable
    {
        ONCE, DAILY, WEEKLY
    }

    public enum RideState implements Serializable
    {
        OFFERING, JOINED, VIEWING, NEW, PAST
    }

    public Ride(JSONObject jsonRide)
    {
        joined = new ArrayList<>();
        waiting = new ArrayList<>();

        try
        {
            JsonParser.parseRide(jsonRide, this);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    // Get a json array of all rides data received from server,
    // and convert it to an ArrayList of Ride objects
    public static ArrayList<Ride> fromJson(JSONArray ridesJsonArray, boolean isSearchResults)
    {
        ArrayList<Ride> rides = new ArrayList<>();

        for (int i = 0; i < ridesJsonArray.length(); i++)
        {
            try
            {
                Ride nRide = new Ride(ridesJsonArray.getJSONObject(i));

                if (isSearchResults || (nRide.rideState != RideState.NEW))
                {
                    rides.add(nRide);
                }
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return rides;
    }

    public boolean acceptJoin(Pickup lift)
    {
        for (Pickup pickup : waiting)
        {
            if (pickup.getUser().getUsername().equals(lift.getUser().getUsername()))
            {
                joined.add(lift);
                waiting.remove(pickup);

                return true;
            }
        }

        // Accept fails
        return false;
    }

    public boolean rejectJoin(Pickup lift)
    {
        for (Pickup pickup : waiting)
        {
            if (pickup.getUser().getUsername().equals(lift.getUser().getUsername()))
            {
                waiting.remove(pickup);

                return true;
            }
        }

        // Reject fails
        return false;
    }

    public boolean hasPass(User passenger)
    {
        for (Pickup pick : joined)
        {
            if (passenger.getUsername().equals(pick.getUser().getUsername()))
            {
                return true;
            }
        }

        return false;
    }

    public boolean hasRequest(User request)
    {
        for (Pickup pick : waiting)
        {
            if (request.getUsername().equals(pick.getUser().getUsername()))
            {
                return true;
            }
        }

        return false;
    }

    public void setDriverRated(User passenger, boolean driverRated)
    {
        for (Pickup pick : joined)
        {
            if (passenger.getUsername().equals(pick.getUser().getUsername()))
            {
                pick.setDriverRated(driverRated);
            }
        }
    }

    public void setPassengerRated(User passenger, boolean ratedByDriver)
    {
        for (Pickup pick : joined)
        {
            if (passenger.getUsername().equals(pick.getUser().getUsername()))
            {
                pick.setRatedByDriver(ratedByDriver);
            }
        }
    }

    public boolean isDriverRated()
    {
        boolean driverRated = false;

        for (Pickup pickup : joined)
        {
            if (pickup.getUser().getUsername().equals(User.getCurrentUser().getUsername()))
            {
                driverRated = pickup.isDriverRated();
            }
        }

        return driverRated;
    }

    public boolean isPassRated(String passName)
    {
        boolean isPassRated = false;

        for (Pickup pickup : joined)
        {
            if (pickup.getUser().getUsername().equals(passName))
            {
                isPassRated = pickup.isRatedByDriver();
            }
        }

        return isPassRated;
    }

    public void clearRequests(){
        waiting.clear();
    }

    public void setArrivingTime(String arriving_time)
    {
        this.arriving_time = arriving_time;
    }

    public void setStartTime(String start_time)
    {
        this.start_time = start_time;
    }

    public void setDriver(User driver)
    {
        this.driver = driver;
    }

    public void setSeats(int seats)
    {
        this.seats = seats;
    }

    public void setStart(Location start)
    {
        this.start = start;
    }

    public void setEnd(Location end)
    {
        this.end = end;
    }

    public void setRideId(String id)
    {
        rideId = id;
    }

    public void setState(RideState state)
    {
        this.rideState = state;
    }

    public void addWaiting(Pickup lift)
    {
        waiting.add(lift);
    }

    public void addJoin(Pickup pickup)
    {
        joined.add(pickup);
    }

    public String getSeats()
    {
        return Integer.toString(seats);
    }

    public String getArrivingTime()
    {
        return arriving_time;
    }

    public String getStartTime()
    {
        return start_time;
    }

    public Location getStart()
    {
        return start;
    }

    public Location getEnd()
    {
        return end;
    }

    public String getRideId()
    {
        return rideId;
    }

    public User getDriver()
    {
        return driver;
    }

    public Group getGroup()
    {
        return group;
    }

    public RideState getRideState()
    {
        return rideState;
    }

    public ArrayList<Pickup> getJoined()
    {
        return joined;
    }

    public ArrayList<Pickup> getWaiting()
    {
        return waiting;
    }

    public String getGourpOrEventName() {
        return gourpOrEventName;
    }

    public void setGourpOrEventName(String gourpOrEventName) {
        this.gourpOrEventName = gourpOrEventName;
    }
}
