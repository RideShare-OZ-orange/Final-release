package com.swen900014.orange.rideshareoz.views;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.swen900014.orange.rideshareoz.utils.MyRequestQueue;
import com.swen900014.orange.rideshareoz.R;
import com.swen900014.orange.rideshareoz.models.Pickup;
import com.swen900014.orange.rideshareoz.models.Ride;
import com.swen900014.orange.rideshareoz.models.User;

import java.util.HashMap;
import java.util.Map;

import static com.swen900014.orange.rideshareoz.utils.Resources.*;


/**
 * Created by Sangzhuoyang Yu on 9/12/15.
 * The view activity where user information are
 * displayed
 */
public class UserInfoActivity extends AppCompatActivity
{
    private Ride ride;
    private int rideIndex;
    private Pickup pickup;
    private Activity thisActivity;
    private int score;   // Marking passenger

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfoactivity_2);

        Intent intent = getIntent();

        // Get ride index from view Ride activity
        rideIndex = (int) intent.getSerializableExtra("SelectedRide");

        ride = Ride.allRides.get(rideIndex);
        Ride.RideState rideState = ride.getRideState();

        // Display the pick up location of a passenger
        TextView startAddressText = (TextView) findViewById(R.id.startAddressText);
        TextView startAddressLabel =  (TextView) findViewById(R.id.startAddressLabel);

        if (intent.hasExtra("Pickup"))
        {
            pickup = (Pickup) intent.getSerializableExtra("Pickup");

            startAddressText.setText(pickup.getLocation().getAddress());
        }
        else
        {
            pickup = new Pickup(ride.getDriver(), ride.getEnd(), true, true);

            startAddressText.setVisibility(View.GONE);
            startAddressLabel.setVisibility(View.GONE);
        }

        thisActivity = this;

        // Display user information
        TextView nameText = (TextView) findViewById(R.id.ShowName);
        TextView phoneText = (TextView) findViewById(R.id.ShowPhone);
        TextView emailText = (TextView) findViewById(R.id.ShowEmail);
        TextView creditText = (TextView) findViewById(R.id.ShowCredit);
        TextView aboutMeText = (TextView) findViewById(R.id.aboutMeText);

        nameText.setText(pickup.getUser().getUsername());
        phoneText.setText(pickup.getUser().getPhone());
        emailText.setText(pickup.getUser().getEmail());
        creditText.setText(pickup.getUser().getCredit());
        aboutMeText.setText(pickup.getUser().getAbout());

        // Hide accept and reject options if current user is
        // not driver offering the ride
        if (rideState == Ride.RideState.OFFERING && ride.hasRequest(pickup.getUser()))
        {
            Button acceptButton = (Button) findViewById(R.id.acceptButton);
            Button rejectButton = (Button) findViewById(R.id.rejectButton);

            acceptButton.setVisibility(View.VISIBLE);
            rejectButton.setVisibility(View.VISIBLE);

            acceptButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    sendAcceptRequest();
                }
            });
            rejectButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    sendRejectRequest();
                }
            });
        }
        // Show rating options for driver to rate passengers
        else if (rideState == Ride.RideState.PAST &&
                !User.getCurrentUser().getUsername()
                        .equals(pickup.getUser().getUsername()) &&
                User.getCurrentUser().getUsername()
                        .equals(ride.getDriver().getUsername()) &&
                !ride.isPassRated(pickup.getUser().getUsername()))
        {
            Button rateButton = (Button) findViewById(R.id.ratePassButton);
            rateButton.setVisibility(View.VISIBLE);

            // Rating spinner
            Spinner spinnerRate = (Spinner) findViewById(R.id.spinnerRatePass);
            spinnerRate.setVisibility(View.VISIBLE);

            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                    R.array.rate_array, android.R.layout.simple_spinner_item);
            spinnerRate.setAdapter(spinnerAdapter);

            // Set the default option to be 'I enjoy it!'
            spinnerRate.setSelection(4);

            spinnerRate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    score = position + 1;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent)
                {

                }
            });
        }
    }

    public void ratePassenger(View view)
    {
        sendRateRequest();
    }

    private void sendRateRequest()
    {
        StringRequest rateRequest = new StringRequest(Request.Method.POST,
                RATE_USER_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                ride.setPassengerRated(pickup.getUser(), true);

                thisActivity.finish();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                volleyError.printStackTrace();

                System.out.println("Sending rate post failed!");
            }
        })
        {
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();

                params.put("username", User.getCurrentUser().getUsername());
                params.put("rateeName", pickup.getUser().getUsername());
                params.put("ride_id", ride.getRideId());
                params.put("rate", Integer.toString(score));
                params.put("type", "passenger");

                return params;
            }
        };

        MyRequestQueue.getInstance(thisActivity).addToRequestQueue(rateRequest);
    }

    public void sendAcceptRequest()
    {
        StringRequest acceptRequest = new StringRequest(Request.Method.POST,
                ACCEPT_REQUEST_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                ride.acceptJoin(pickup);
                thisActivity.finish();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                volleyError.printStackTrace();

                System.out.println("Sending accept failed!");
            }
        }){
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();

                params.put("username", pickup.getUser().getUsername());
                params.put("ride_id", ride.getRideId());

                return params;
            }
        };

        MyRequestQueue.getInstance(thisActivity).addToRequestQueue(acceptRequest);
    }

    public void sendRejectRequest()
    {
        StringRequest rejectRequest = new StringRequest(Request.Method.POST,
                REJECT_REQUEST_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                ride.rejectJoin(pickup);
                thisActivity.finish();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                volleyError.printStackTrace();

                System.out.println("Sending reject failed!");
            }
        }){
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();

                params.put("username", pickup.getUser().getUsername());
                params.put("ride_id", ride.getRideId());

                return params;
            }
        };

        MyRequestQueue.getInstance(thisActivity).addToRequestQueue(rejectRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_info, menu);
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
