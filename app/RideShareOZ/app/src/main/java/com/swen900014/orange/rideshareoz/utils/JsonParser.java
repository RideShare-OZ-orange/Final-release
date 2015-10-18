package com.swen900014.orange.rideshareoz.utils;

import com.swen900014.orange.rideshareoz.models.Group;
import com.swen900014.orange.rideshareoz.models.Location;
import com.swen900014.orange.rideshareoz.models.Pickup;
import com.swen900014.orange.rideshareoz.models.Ride;
import com.swen900014.orange.rideshareoz.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Sangzhuoyang Yu on 10/12/15.
 * Parse JSON obj get from server.
 */
public class JsonParser
{
    public static void parsePassenger(JSONObject jsonUser, User user) throws JSONException
    {
        String username = jsonUser.getString("username");
        String credit = jsonUser.getString("passenger_rate");
        String phone = jsonUser.getString("phone");
        //String about = jsonUser.getString("note");

        user.setName(username);
        // Currently username is the same as email
        user.setEmail(username);
        user.setCredit(credit);
        user.setPhone(phone);
        //user.setAbout(about);
    }

    public static void parseDriver(JSONObject jsonUser, User user) throws JSONException
    {
        String username = jsonUser.getString("username");
        String credit = jsonUser.getString("driver_rate");
        String phone = jsonUser.getString("phone");
        //String about = jsonUser.getString("note");

        user.setName(username);
        // Currently username is the same as email
        user.setEmail(username);
        user.setCredit(credit);
        user.setPhone(phone);
        //user.setAbout(about);
    }

    public static void parseRide(JSONObject jsonRide, Ride ride) throws JSONException
    {
        JSONObject tempObj;
        JSONArray tempArray;
        JSONArray tempLocationArray;

        try
        {
            // get group event name
            if (jsonRide.has("group"))
            {
                tempObj = jsonRide.getJSONObject("group");
                ride.setGourpOrEventName(tempObj.getString("groupname"));
            }
            else if (jsonRide.has("events"))
            {
                tempObj = jsonRide.getJSONObject("events");
                ride.setGourpOrEventName(tempObj.getString("eventName"));
            }
            else
            {
                ride.setGourpOrEventName("");
            }

            // Get start and end address
            String startAddress = jsonRide.getString("start_add");
            String endAddress = jsonRide.getString("destination");

            // Get start lat and lon
            tempArray = jsonRide.getJSONArray("start_point");
            Location start = new Location(tempArray.getDouble(0), tempArray.getDouble(1), startAddress);
            ride.setStart(start);

            // Get end lat and lon
            tempArray = jsonRide.getJSONArray("end_point");
            Location end = new Location(tempArray.getDouble(0), tempArray.getDouble(1), endAddress);
            ride.setEnd(end);

            // Get ride id
            String rideId = jsonRide.getString("_id");
            ride.setRideId(rideId);

            // Get driver info
            tempObj = jsonRide.getJSONObject("driver");
            User driver = new User();
            JsonParser.parseDriver(tempObj, driver);
            ride.setDriver(driver);

            // Get seat number, start time and arrival time
            int seats = jsonRide.getInt("seats");
            String arriving_time = DateFormatter.format(jsonRide.getString("arrival_time"));
            String start_time = DateFormatter.format(jsonRide.getString("start_time"));

            ride.setSeats(seats);
            ride.setArrivingTime(arriving_time);
            ride.setStartTime(start_time);

            // Is this ride already finished?
            boolean finished = jsonRide.getBoolean("finished");

            /* get the list of requests */
            tempArray = jsonRide.getJSONArray("requests");

            for (int i = 0; i < tempArray.length(); i++)
            {
                tempObj = tempArray.getJSONObject(i);
                JSONObject requestingPassObj = tempObj.getJSONObject("user");

                User passWaiting = new User();
                JsonParser.parsePassenger(requestingPassObj, passWaiting);

                if (User.getCurrentUser().getUsername().equals(passWaiting.getUsername()))
                {
                    ride.setState(Ride.RideState.VIEWING);
                }

                tempLocationArray = tempObj.getJSONArray("pickup_point");
                Location loc = new Location(tempLocationArray.getDouble(0), tempLocationArray.getDouble(1));
                loc.setAddress(tempObj.getString("pickup_add"));

                ride.addWaiting(new Pickup(passWaiting, loc));
            }

            /* get the list of joins */
            tempArray = jsonRide.getJSONArray("passengers");

            if (tempArray != null)
            {
                for (int i = 0; i < tempArray.length(); i++)
                {
                    tempObj = tempArray.getJSONObject(i);
                    JSONObject joinedPassObj = tempObj.getJSONObject("user");
                    boolean isRatedByDriver = tempObj.getBoolean("rated_by_driver");
                    boolean hasRatedDriver = tempObj.getBoolean("rated");

                    User passJoined = new User();
                    JsonParser.parsePassenger(joinedPassObj, passJoined);

                    if (User.getCurrentUser().getUsername().equals(passJoined.getUsername()))
                    {
                        ride.setState(Ride.RideState.JOINED);
                    }

                    tempLocationArray = tempObj.getJSONArray("pickup_point");
                    Location loc = new Location(tempLocationArray.getDouble(0), tempLocationArray.getDouble(1));
                    loc.setAddress(tempObj.getString("pickup_add"));
                    ride.addJoin(new Pickup(passJoined, loc, isRatedByDriver, hasRatedDriver));
                }
            }

            if (finished)
            {
                /* add to the offering list*/
                ride.setState(Ride.RideState.PAST);
                ride.clearRequests();
            }
            else if (ride.getDriver().getUsername().equals(User.getCurrentUser().getUsername()))
            {
                /* add to the passed list*/
                ride.setState(Ride.RideState.OFFERING);
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public static void parseGroup(JSONObject groupJson, Group group) throws JSONException
    {
        JSONObject groupJsonObj = groupJson.getJSONObject("group");

        group.setGroupId(groupJsonObj.getString("_id"));
        group.setName(groupJsonObj.getString("groupname"));
        group.setDescription(groupJsonObj.getString("introduction"));

        String state = groupJson.getString("state");

        switch (state)
        {
            case "joined":
                group.setGroupState(Group.GroupState.JOINED);
                break;

            case "request":
                group.setGroupState(Group.GroupState.REQUESTING);
                break;

            default:
                group.setGroupState(Group.GroupState.NEW);
                break;
        }
    }
}
