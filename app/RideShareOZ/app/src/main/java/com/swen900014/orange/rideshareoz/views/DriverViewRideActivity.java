package com.swen900014.orange.rideshareoz.views;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.swen900014.orange.rideshareoz.utils.Resources.*;


/**
 * Created by Sangzhuoyang Yu on 9/12/15.
 * It initialize a new activity for the ride
 * from the drivers' view. The driver is able
 * to cancel the ride, accept or reject requests
 * from passengers.
 */
public class DriverViewRideActivity extends AppCompatActivity
{
    private TableLayout passengerList;
    private TableLayout waitingList;
    private Ride ride;
    private int rideIndex;
    private Activity thisActivity;

    @Override
    protected void onStart()
    {
        super.onStart();

        displayPassengers();
        displayRequests();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_view_ride);
        thisActivity = this;

        // Get ride index from my Rides fragment
        rideIndex = (int) getIntent().getIntExtra("SelectedRide", 0);
        ride = Ride.allRides.get(rideIndex);

        TextView startLabel = (TextView) findViewById(R.id.startPointText);
        TextView endLabel = (TextView) findViewById(R.id.endPointText);
        TextView startTimeLabel = (TextView) findViewById(R.id.startTimeText);
        TextView arrivalTimeLabel = (TextView) findViewById(R.id.endTimeText);
        TextView driverText = (TextView) findViewById(R.id.driverText);
        TextView seatsText = (TextView) findViewById(R.id.seatsText);

        passengerList = (TableLayout) findViewById(R.id.passengerList);
        waitingList = (TableLayout) findViewById(R.id.waitingList);

        // Display ride information
        startLabel.setText(ride.getStart().getAddress());
        endLabel.setText(ride.getEnd().getAddress());
        startTimeLabel.setText(ride.getStartTime());
        arrivalTimeLabel.setText(ride.getArrivingTime());
        seatsText.setText(ride.getSeats());
        
        SpannableString content = new SpannableString(ride.getDriver().getUsername());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        driverText.setText(content);

        // Driver is not allowed to cancel a ride already finished
        if (ride.getRideState() == Ride.RideState.PAST)
        {
            Button cancelButton = (Button) findViewById(R.id.cancelButton);
            cancelButton.setVisibility(View.GONE);
        }

        // Click and go to driver user info page
        driverText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(thisActivity, UserInfoActivity.class);
                intent.putExtra("SelectedRide", rideIndex);

                thisActivity.startActivity(intent);
            }
        });
    }

    public void displayRequests()
    {
        ArrayList<Pickup> waitingListArray = ride.getWaiting();
        waitingList.removeAllViews();

        // Display requesting users
        if (ride.getRideState() == Ride.RideState.OFFERING) {
            for (final Pickup lift : waitingListArray) {
                TextView request = new TextView(this);
                SpannableString content = new SpannableString(lift.getUser().getUsername());
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                request.setText(content);
                request.setTextColor(Color.parseColor("#000080"));

                request.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(thisActivity, UserInfoActivity.class);
                        intent.putExtra("SelectedRide", rideIndex);
                        intent.putExtra("Pickup", lift);
                        thisActivity.startActivity(intent);
                    }
                });

                waitingList.addView(request);
            }
        }
    }

    public void displayPassengers()
    {
        ArrayList<Pickup> joinedList = ride.getJoined();
        passengerList.removeAllViews();

        // Display joined passengers
        for (final Pickup lift : joinedList)
        {
            TextView pass = new TextView(this);
            SpannableString content = new SpannableString(lift.getUser().getUsername());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            pass.setText(content);
            pass.setTextColor(Color.parseColor("#000080"));

            pass.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(thisActivity, UserInfoActivity.class);
                    intent.putExtra("SelectedRide", rideIndex);
                    intent.putExtra("Pickup", lift);
                    thisActivity.startActivity(intent);
                }
            });

            passengerList.addView(pass);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pass_view_ride, menu);
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

    public void cancelRide(View view)
    {
        sendCancelRequest();
    }

    public void sendCancelRequest()
    {
        StringRequest cancelRequest = new StringRequest(Request.Method.POST,
                CANCEL_RIDE_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                // Get back to the my rides page
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

                // User name and ride id for the ride to be cancelled
                String accountName = User.getCurrentUser().getUsername();
                params.put("username", accountName);
                params.put("ride_id", ride.getRideId());

                return params;
            }
        };

        MyRequestQueue.getInstance(thisActivity).addToRequestQueue(cancelRequest);
    }
}
