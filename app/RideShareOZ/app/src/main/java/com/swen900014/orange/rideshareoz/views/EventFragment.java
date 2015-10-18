package com.swen900014.orange.rideshareoz.views;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.swen900014.orange.rideshareoz.utils.MyRequestQueue;
import com.swen900014.orange.rideshareoz.R;
import com.swen900014.orange.rideshareoz.models.Event;
import com.swen900014.orange.rideshareoz.utils.Resources;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by George on 6/09/2015.
 * Display a single ride info in the Myrides activity
 */

public class EventFragment extends Fragment
{
    private EventsAdaptor mEventAdaptor;
    private Activity thisActivity;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);

        thisActivity = this.getActivity();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        List<Event> currentEvents = new ArrayList<>();

        // Now that we have some dummy data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy data) and
        // use it to populate the ListView it's attached to.
        mEventAdaptor = new EventsAdaptor(getActivity(), (ArrayList<Event>) currentEvents);

        /* load the actual data from server */
        sendGetEventsRequest();


        View rootView = inflater.inflate(R.layout.fragment_events, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_events);
        listView.setAdapter(mEventAdaptor);

        return rootView;
    }

    public void sendGetEventsRequest()
    {
        StringRequest getGroupsRequest = new StringRequest(Request.Method.GET,
                Resources.GETALL_EVENT_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                storeEvents(s);
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

                return params;
            }
        };

        MyRequestQueue.getInstance(thisActivity).addToRequestQueue(getGroupsRequest);
    }

    private void storeEvents(String response)
    {
        if (response != null)
        {
            ArrayList<Event> serverEvents = null;

            try
            {
                Event.storeEvents(new JSONArray(response));
                serverEvents = Event.getAllEvents();
            } catch (JSONException e)
            {
                e.printStackTrace();
            }

            mEventAdaptor.clear();

            if (serverEvents != null)
            {
                for (Event listItem : serverEvents)
                {
                    mEventAdaptor.add(listItem);
                }
            }

            // Group data is back from the server.  Hooray!
        }
    }
}
