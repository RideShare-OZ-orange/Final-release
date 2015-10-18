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
import com.swen900014.orange.rideshareoz.models.Group;
import com.swen900014.orange.rideshareoz.models.User;
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

public class GroupFragment extends Fragment
{
    private GroupsAdaptor mGroupAdaptor;
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
        //inflater.inflate(R.menu.myridesfragment, menu);
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
        List<Group> currentGroups = new ArrayList<>();

        // Now that we have some dummy data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy data) and
        // use it to populate the ListView it's attached to.
        mGroupAdaptor = new GroupsAdaptor(getActivity(), (ArrayList<Group>) currentGroups);

        /* load the actual data from server */
        sendGetGroupsRequest();


        View rootView = inflater.inflate(R.layout.fragment_groups, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_groups);
        listView.setAdapter(mGroupAdaptor);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                Group selectedGroup = mGroupAdaptor.getItem(position);
                Intent intent = new Intent(getActivity(), ViewGroupActivity.class);
                intent.putExtra("SelectedGroup", selectedGroup);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void sendGetGroupsRequest()
    {
        StringRequest getGroupsRequest = new StringRequest(Request.Method.POST,
                Resources.GETALL_GROUP_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                storeGroups(s);
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

                params.put("username", User.getCurrentUser().getUsername());
                //params.put("token", MainActivity.getAuthToken(getActivity().getApplicationContext()));

                return params;
            }
        };

        MyRequestQueue.getInstance(thisActivity).addToRequestQueue(getGroupsRequest);
    }

    private void storeGroups(String response)
    {
        if (response != null)
        {
            ArrayList<Group> serverGroups = null;

            try
            {
                Group.storeGroups(new JSONArray(response));
                serverGroups = Group.getAllGroups();
            } catch (JSONException e)
            {
                e.printStackTrace();
            }

            mGroupAdaptor.clear();

            if (serverGroups != null)
            {
                for (Group listItemGroup : serverGroups)
                {
                    mGroupAdaptor.add(listItemGroup);
                }
            }

            // Group data is back from the server.  Hooray!
        }
    }
}
