package com.swen900014.orange.rideshareoz.models;

import android.app.Activity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.swen900014.orange.rideshareoz.utils.*;
import com.swen900014.orange.rideshareoz.views.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sangzhuoyang Yu & George on 10/2/15.
 * Encapsulate event info.
 */
public class Event implements Serializable
{
    private String eventId;
    private String name;
    private String description;

    private Location eventLocation;
    private String start_time;
    private String end_time;

    private static HashMap<String, Event> allEvents = new HashMap<String, Event>();


    public Event(String eventId, String name, String description, Location location, String s_time, String e_time)
    {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.start_time = s_time;
        this.end_time = e_time;
        this.eventLocation = location;
    }

    public static Event getEvent(String Id)
    {
        return allEvents.get(Id);
    }

    public String getName()
    {
        return name;
    }

    public static ArrayList<Event> getAllEvents()
    {

        ArrayList<Event> events = new ArrayList<Event>();
        events.addAll(allEvents.values());
        return events;
    }

    public static void storeEvents(JSONArray eventsJsonArray)
    {

        allEvents.clear();
        for (int i = 0; i < eventsJsonArray.length(); i++)
        {

            try
            {
                Event newEvent = new Event(eventsJsonArray.getJSONObject(i));
                allEvents.put(newEvent.eventId, newEvent);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }

        }

    }


    public static void loadEvents(final Activity activity)
    {
        StringRequest getRidesRequest = new StringRequest(Request.Method.GET,
                Resources.GETALL_EVENT_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                System.out.println("response: " + s);
                try
                {
                    storeEvents(new JSONArray(s));
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                volleyError.printStackTrace();

                System.out.println("Sending post failed!");
            }
        })
        {
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();

                //params.put("username", User.getCurrentUser().getUsername());
                params.put("token", MainActivity.getAuthToken(activity.getApplicationContext()));

                return params;
            }
        };

        MyRequestQueue.getInstance(activity).addToRequestQueue(getRidesRequest);
    }

    public Event(JSONObject eventJson) throws JSONException
    {

        JSONArray tempLocationArray;

        this.eventId = eventJson.getString("_id");
        this.name = eventJson.getString("eventName");
        this.description = eventJson.getString("eventInfo");
        tempLocationArray = eventJson.getJSONArray("eventLocation");
        Location loc = new Location(tempLocationArray.getDouble(1), tempLocationArray.getDouble(0));
        loc.setAddress(eventJson.getString("location"));
        this.eventLocation = loc;
        this.start_time = DateFormatter.format(eventJson.getString("startTime"));
        this.end_time = DateFormatter.format(eventJson.getString("endTime"));
    }

    public String getEventId()
    {
        return eventId;
    }

    public String getDescription()
    {
        return description;
    }

    public Location getEventLocation()
    {
        return eventLocation;
    }

    public String getStart_time()
    {
        return start_time;
    }

    public String getEnd_time()
    {
        return end_time;
    }
}
