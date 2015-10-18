package com.swen900014.orange.rideshareoz.views;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.swen900014.orange.rideshareoz.utils.MyRequestQueue;
import com.swen900014.orange.rideshareoz.R;
import com.swen900014.orange.rideshareoz.models.Group;
import com.swen900014.orange.rideshareoz.models.User;

import java.util.HashMap;
import java.util.Map;

import static com.swen900014.orange.rideshareoz.utils.Resources.JOIN_GROUP_URL;
import static com.swen900014.orange.rideshareoz.utils.Resources.LEAVE_GROUP_URL;


/**
 * Created by Sangzhuoyang Yu on 10/2/15.
 * Display group info, users are allowed
 * to join or leave a group.
 */
public class ViewGroupActivity extends AppCompatActivity
{
    private Group mGroup;
    private Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group);

        // Receive group object from MyGroup Activity
        Intent intent = getIntent();
        mGroup = (Group) intent.getSerializableExtra("SelectedGroup");

        updateView();

        thisActivity = this;
    }

    public void updateView()
    {
        Button join_leave_button = (Button) findViewById(R.id.join_leave_groupButton);

        // Display Button correctly based on the state of group
        if (mGroup.getGroupState() == Group.GroupState.REQUESTING)
        {
            join_leave_button.setVisibility(View.GONE);
        }
        else if (mGroup.getGroupState() == Group.GroupState.JOINED)
        {
            join_leave_button.setText(R.string.LeaveButton);
        }
        else
        {
            join_leave_button.setText(R.string.joinButton);
        }

        // Display group name and description
        TextView groupNameLabel = (TextView) findViewById(R.id.groupNameText);
        groupNameLabel.setText(mGroup.getName());

        TextView groupDesLabel = (TextView) findViewById(R.id.groupDescriptionText);
        groupDesLabel.setText(mGroup.getDescription());
    }

    public void onClick(View view)
    {
        // User haven't sent a join request
        if (mGroup.getGroupState() == Group.GroupState.NEW)
        {
            sendJoinGroupRequest();
        }
        // User have joined the group
        else if (mGroup.getGroupState() == Group.GroupState.JOINED)
        {
            sendLeaveGroupRequest();
        }
    }

    public void sendJoinGroupRequest()
    {
        StringRequest joinRequest = new StringRequest(Request.Method.POST,
                JOIN_GROUP_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                System.out.println("response: " + s);

                thisActivity.finish();
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

                params.put("username", User.getCurrentUser().getUsername());
                params.put("group_id", mGroup.getGroupId());

                return params;
            }
        };

        MyRequestQueue.getInstance(thisActivity).addToRequestQueue(joinRequest);
    }

    public void sendLeaveGroupRequest()
    {
        StringRequest leaveRequest = new StringRequest(Request.Method.POST,
                LEAVE_GROUP_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                System.out.println("response: " + s);

                // Get back to the my groups page
                thisActivity.finish();
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

                // User name and ride id
                params.put("username", User.getCurrentUser().getUsername());
                params.put("group_id", mGroup.getGroupId());

                return params;
            }
        };

        MyRequestQueue.getInstance(this).addToRequestQueue(leaveRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
