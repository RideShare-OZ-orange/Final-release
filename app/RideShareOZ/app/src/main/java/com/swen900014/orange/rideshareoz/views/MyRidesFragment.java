package com.swen900014.orange.rideshareoz.views;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.swen900014.orange.rideshareoz.utils.MyRequestQueue;
import com.swen900014.orange.rideshareoz.R;
import com.swen900014.orange.rideshareoz.models.Ride;
import com.swen900014.orange.rideshareoz.models.User;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.swen900014.orange.rideshareoz.utils.Resources.*;


/**
 * Created by George on 6/09/2015.
 * Display a single ride info in the Myrides activity
 */

public class MyRidesFragment extends Fragment
{
    private RidesAdaptor mRidesAdapter;
    private boolean isSearchResults = false;
    private Intent intent;
    private Activity thisActivity;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);

        thisActivity = this.getActivity();

         /* check if it offer or find  */
        intent = this.getActivity().getIntent();
        if (intent != null && intent.hasExtra("type"))
        {
            String type = intent.getStringExtra("type");
            if (type.equals("find"))
            {
                isSearchResults = true;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        //inflater.inflate(R.menu.myridesfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        List<Ride> currentRides = new ArrayList<>();

        // Now that we have some dummy  data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy data) and
        // use it to populate the ListView it's attached to.
        mRidesAdapter = new RidesAdaptor(getActivity(), (ArrayList<Ride>) currentRides);

        /* ignore the test data and load the actual data from server */
        if (isSearchResults)
        {
            //sendSearchRequest();
            sendGetRidesRequest(SEARCH_RIDE_URL);
        }
        else
        {
            sendGetRidesRequest(GETUSER_RELAVENT_RIDE_URL);
        }

        View rootView = inflater.inflate(R.layout.fragment_myrides, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_myrides);
        listView.setAdapter(mRidesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                Intent intent;
                Ride selectedRide = mRidesAdapter.getItem(position);
                if (selectedRide.getDriver().getUsername()
                        .equals(User.getCurrentUser().getUsername()))
                {
                    intent = new Intent(getActivity(), DriverViewRideActivity.class);
                }
                else
                {
                    intent = new Intent(getActivity(), PassViewRideActivity.class);
                }

                intent.putExtra("SelectedRide", position);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void sendGetRidesRequest(String url)
    {
        StringRequest getRidesRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                storeRides(s);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                volleyError.printStackTrace();

                System.out.println("Sending post failed!");
            }
        }){
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();

                //params.put("username", User.getCurrentUser().getUsername());
                params.put("token", MainActivity.getAuthToken(getActivity().getApplicationContext()));
                if(isSearchResults){
                    params.put("origins", intent.getStringExtra("origin"));
                    params.put("e_lat",intent.getStringExtra("e_lat"));
                    params.put("e_lon", intent.getStringExtra("e_lon"));
                    params.put("arrival_time", intent.getStringExtra("arrival_time"));
                    if(intent.getBooleanExtra("isGroup", true) ){
                        params.put("group_id", intent.getStringExtra("group_id"));
                    }else {
                        params.put("event_id" ,intent.getStringExtra("event_id"));
                    }
                }

                return params;
            }
        };

        MyRequestQueue.getInstance(thisActivity).addToRequestQueue(getRidesRequest);
    }


    private void storeRides(String response)
    {
        if (response != null)
        {
            try
            {
                Ride.allRides = Ride.fromJson(new JSONArray(response), isSearchResults);
                mRidesAdapter.clear();

                for (Ride listItemRide : Ride.allRides)
                {
                    mRidesAdapter.add(listItemRide);
                }
            } catch (JSONException e)
            {
                e.printStackTrace();
            }

            // New data is back from the server.  Hooray!
        }
    }
}
