package com.swen900014.orange.rideshareoz.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.swen900014.orange.rideshareoz.utils.MyRequestQueue;
import com.swen900014.orange.rideshareoz.R;
import com.swen900014.orange.rideshareoz.models.*;
import com.swen900014.orange.rideshareoz.models.Group;
import com.swen900014.orange.rideshareoz.models.User;
import com.swen900014.orange.rideshareoz.utils.GPSTracker;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.swen900014.orange.rideshareoz.utils.Resources.BOUNDS_GREATER_MELBOURNE;
import static com.swen900014.orange.rideshareoz.utils.Resources.OFFER_RIDE_URL;

/**
 * Created by Qianwen Zhang on 9/12/15.
 * The view activity where users are able to
 * offer a ride as a driver
 */
public class OfferRide extends FragmentActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener
{
    ArrayList<Group> selectGroups = null;
    ArrayList<Event> selectEvents = null;

    private final String TAG = "OfferRide";
    private String eventId = "";
    private String groupId = "";
    private String groupName = "";
    private String tempDate = "", SeatNo = "1";
    private String EditStartTime = "";
    private String EditEndTime = "";
    private String month;
    private String day;
    private String hours = "";
    private String mins = "";
    private String houra = "";
    private String mina = "";

    private TextView textStartTime;

    private CheckBox FromCurrentLocation, ToCurrentLocation;

    private AutoCompleteTextView EditStart;
    private AutoCompleteTextView EditEnd;

    private Button btnSubmit;
    private Button btnSelectEvent;
    private Button btnSelectGroup;
    private String tempLat = "", tempLon = "";
    private String latS = "";
    private String lonS = "";
    private String latE = "";
    private String lonE = "";

    //current GPS location
    private double latC = 0;
    private double lonC = 0;
    private String currentAddress = "";
    private String startAddress = "";
    private String endAddress = "";
    private GPSTracker gps;
    private boolean isFind = false;
    private boolean isGroup = false;
    private boolean isEvent = false;
    private boolean isToEvent = false;
    private boolean isFromEvent = false;
    private boolean isCancelled;
    private String eventLocation;
    protected GoogleApiClient mGoogleApiClient;

    Calendar calendar = Calendar.getInstance();
    private TextView displayDate, displayStartTime, displayArrivalTime;
    private DialougState dialougState = DialougState.NONE;

    private enum DialougState
    {
        NONE, GPS
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        switch (dialougState)
        {
            case GPS:
                dialougState = DialougState.NONE;
                finish();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offerride);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
        btnSubmit = (Button) findViewById(R.id.button1);
        Button btnReset = (Button) findViewById(R.id.button2);
        Button btnDate = (Button) findViewById(R.id.setDateButton);
        Button btnStartTime = (Button) findViewById(R.id.setStartTimeButton);
        Button btnArrivalTime = (Button) findViewById(R.id.setEndTimeButton);
        btnSelectEvent = (Button) findViewById(R.id.buttonEvent);
        btnSelectGroup = (Button) findViewById(R.id.buttonGroup);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        displayDate = (TextView) findViewById(R.id.displayDate);
        displayStartTime = (TextView) findViewById(R.id.displayStartTime);
        displayArrivalTime = (TextView) findViewById(R.id.displayArrivalTime);

        FromCurrentLocation = (CheckBox) findViewById(R.id.current1);
        ToCurrentLocation = (CheckBox) findViewById(R.id.current2);
        TextView textSN = (TextView) findViewById(R.id.txtSeatNo);
        TextView textTitle = (TextView) findViewById(R.id.textView4);
        TextView textSearchEvent = (TextView) findViewById(R.id.textView5);
        TextView textSearchGroup = (TextView) findViewById(R.id.textView6);

        FromCurrentLocation.setEnabled(true);
        ToCurrentLocation.setEnabled(true);
        //GPS
        

       /* check if it is offer or find  */
        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra("type"))
        {
            String type = intent.getStringExtra("type");
            if (type.equals("find"))
            {
                // SpinSN.setVisibility(View.INVISIBLE);
                textSN.setVisibility(View.GONE);
                btnStartTime.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);
                textTitle.setText("                   Search For Ride");
                textSearchEvent.setText("Search To An Event!");
                textSearchGroup.setText("Search To A Group!");
                isFind = true;
            }
        }
        EditStart = (AutoCompleteTextView)
                findViewById(R.id.Start);

        EditEnd = (AutoCompleteTextView)
                findViewById(R.id.End);

        //auto-complete adapter
        PlaceAutoCompleteAdapter adapterS = new PlaceAutoCompleteAdapter(this,
                android.R.layout.simple_expandable_list_item_1, mGoogleApiClient,
                BOUNDS_GREATER_MELBOURNE, null, EditStart);
        EditStart.setAdapter(adapterS);

        PlaceAutoCompleteAdapter adapterE = new PlaceAutoCompleteAdapter(this,
                android.R.layout.simple_expandable_list_item_1, mGoogleApiClient,
                BOUNDS_GREATER_MELBOURNE, null, EditEnd);
        EditEnd.setAdapter(adapterE);

        //spinner adapter
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.seats, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                SeatNo = String.valueOf(position + 1);
                if (position > 0)
                {
                    Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) +
                            " selected", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        btnSelectEvent.setOnClickListener(this);
        btnSelectGroup.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnDate.setOnClickListener(this);
        btnStartTime.setOnClickListener(this);
        btnArrivalTime.setOnClickListener(this);
        FromCurrentLocation.setOnClickListener(this);
        ToCurrentLocation.setOnClickListener(this);

        getIntent();
    }

    public void finish(Activity activity)
    {
        activity.finish();
    }

    public void getGps()
    {
        gps = new GPSTracker(this);
    }

    public void checkGps()
    {
        // check if GPS enabled
        if (gps.canGetLocation())
        {
            latC = gps.getLatitude();
            lonC = gps.getLongitude();
        }
        else
        {
            // can't get location
            // GPS is not enabled
            // Ask user to enable GPS/network in settings
            dialougState = DialougState.GPS;
            gps.showSettingsAlert();
            isCancelled = gps.getState();
            System.out.print("get the gps state" + isCancelled);
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.button2)
        {
            EditStart.setText("");
            EditEnd.setText("");
            displayDate.setText("");
            displayStartTime.setText("");
            displayArrivalTime.setText("");
            eventId = "";
            groupId = "";
            btnSelectEvent.setEnabled(true);
            btnSelectGroup.setEnabled(true);
            EditStart.setHint("");
            EditEnd.setHint("");
            FromCurrentLocation.setChecked(false);
            ToCurrentLocation.setChecked(false);
            FromCurrentLocation.setEnabled(true);
            ToCurrentLocation.setEnabled(true);
        }
        if (v.getId() == R.id.buttonEvent)
        {
            selectEvent();
        }
        if (v.getId() == R.id.buttonGroup)
        {
            selectGroup();
        }
        if (v.getId() == R.id.current1 || v.getId() == R.id.current2)
        {
            getGps();
            checkGps();
            if (isCancelled)
            {
                System.out.println("isCancelled is cancelled");
                FromCurrentLocation.setChecked(false);
                ToCurrentLocation.setChecked(false);
                ToCurrentLocation.setEnabled(false);
                FromCurrentLocation.setEnabled(false);
            }
            if (!isCancelled)
            {
                System.out.println("isCancelled is on ");
                reverseAddress(OfferRide.this);
            }
        }

        if (v.getId() == R.id.button1)
        {
            offerRide(v);
        }
        if (v.getId() == R.id.setDateButton)
        {
            setDate(v);
        }
        if (v.getId() == R.id.setStartTimeButton)
        {
            setStartTime(v);
        }
        if (v.getId() == R.id.setEndTimeButton)
        {
            setArrivalTime(v);
        }
    }

    private void selectGroup()
    {
        //receive a list of group
        selectGroups = Group.getMyGroups();
        final String[] groupsArray = new String[selectGroups.size()];
        for (int i = 0; i < selectGroups.size(); i++)
        {
            groupsArray[i] = selectGroups.get(i).getName();
        }

        AlertDialog.Builder builder1 = new AlertDialog.Builder(OfferRide.this);
        builder1.setTitle("Select Group");
        builder1.setItems(groupsArray, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int position)
            {
                btnSelectEvent.setEnabled(false);
                isGroup = true;
                //Toast.makeText(getApplicationContext(), "You have selected" + groupsArray[position], Toast.LENGTH_SHORT).show();
                groupId = selectGroups.get(position).getGroupId();
                groupName = selectGroups.get(position).getName();
                Toast.makeText(getApplicationContext(), "You have selected " + groupName, Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = builder1.create();
        alertDialog.show();
    }


    private void selectEvent()
    {
        /* receive a list of event */
        selectEvents = Event.getAllEvents();
        final String[] eventsArray = new String[selectEvents.size()];
        for (int i = 0; i < selectEvents.size(); i++)
        {
            eventsArray[i] = selectEvents.get(i).getName();
        }

        AlertDialog.Builder builder1 = new AlertDialog.Builder(OfferRide.this);
        builder1.setTitle("Select Event");
        builder1.setItems(eventsArray, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int position)
            {
                isEvent = true;
                btnSelectGroup.setEnabled(false);
                Toast.makeText(getApplicationContext(), "You have selected" + eventsArray[position], Toast.LENGTH_SHORT).show();
                eventId = selectEvents.get(position).getEventId();
                eventLocation = selectEvents.get(position).getEventLocation().getAddress();
                endAddress = selectEvents.get(position).getEventLocation().getAddress();
                tempLat = Double.toString(selectEvents.get(position).getEventLocation().getLat());
                tempLon = Double.toString(selectEvents.get(position).getEventLocation().getLon());
                AlertDialog.Builder builder3 = new AlertDialog.Builder(OfferRide.this);
                builder3.setTitle("Select type");
                builder3.setPositiveButton("To this event!", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        isToEvent = true;
                        latE = tempLat;
                        lonE = tempLon;
                        EditEnd.setHint(eventLocation);
                        // EditEnd.setKeyListener(null);
                        ToCurrentLocation.setEnabled(false);
                    }
                });
                builder3.setNegativeButton("From this event!", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        isFromEvent = true;
                        latS = tempLat;
                        lonS = tempLon;
                        EditStart.setHint(eventLocation);
                        // EditStart.setKeyListener(null);
                        FromCurrentLocation.setEnabled(false);
                        // Toast.makeText(getApplicationContext(), "You have choose to from  " + isFromEvent, Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder3.create();
                alertDialog.show();

            }
        });

        AlertDialog alertDialog = builder1.create();
        alertDialog.show();
        System.out.println("fallie testing" + latS + lonS);
    }


    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    //reverse Address
    public void reverseAddress(final Activity activity)
    {
        final String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="
                + latC + "," + lonC + "&key=AIzaSyBhEI1X-PMslBS2Ggq35bOncxT05mWO9bs";
        final StringRequest getCurrentAddressRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject jsonResponse = new JSONObject(response);
                            System.out.println(jsonResponse.toString());

                            currentAddress = jsonResponse.getJSONArray("results").getJSONObject(0)
                                    .getString("formatted_address");
                            if (FromCurrentLocation.isChecked())
                            {
                                EditStart.setHint(currentAddress);
                            }
                            if (ToCurrentLocation.isChecked())
                            {
                                EditEnd.setHint(currentAddress);
                            }

                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        volleyError.printStackTrace();
                        System.out.println("it doesn't work");
                    }
                });

        MyRequestQueue.getInstance(activity).addToRequestQueue(getCurrentAddressRequest);
    }

    public void sendRequest(final Activity activity)
    {
        if (isFromEvent)
        {
            getEndpointLoc(activity);
        }
        else
        {
            String startAddressToGoogle = startAddress.replaceAll(" ", "+");

            final String url = "https://maps.googleapis.com/maps/api/geocode/json?" +
                    "address=" + startAddressToGoogle + ",+Australia&" +
                    "key=AIzaSyBhEI1X-PMslBS2Ggq35bOncxT05mWO9bs";

            final StringRequest getStartLocRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>()
                    {
                        public void onResponse(String response)
                        {
                            try
                            {
                                JSONObject jsonResponse = new JSONObject(response);
                                System.out.println(jsonResponse.toString());

                                latS = jsonResponse.getJSONArray("results").getJSONObject(0).
                                        getJSONObject("geometry").getJSONObject("location").
                                        getString("lat");
                                lonS = jsonResponse.getJSONArray("results").getJSONObject(0).
                                        getJSONObject("geometry").getJSONObject("location").
                                        getString("lng");

                                // Check response whether it's accurate, if not remind user

                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }

                            getEndpointLoc(activity);

                        }
                    },
                    new Response.ErrorListener()
                    {
                        public void onErrorResponse(VolleyError volleyError)
                        {
                            volleyError.printStackTrace();
                            System.out.println("it doesn't work");
                        }
                    });

            MyRequestQueue.getInstance(activity).addToRequestQueue(getStartLocRequest);
        }
    }

    private void getEndpointLoc(final Activity activity)
    {
        if (isToEvent)
        {
            if (!isFind)
            {
                sendRideInfo(activity);
            }
            else
            {
                sendSearchRideRequest();
            }
        }
        else
        {
            final String endAddressToGoogle = endAddress.replaceAll(" ", "+");

            final String url = "https://maps.googleapis.com/maps/api/geocode/json?" +
                    "address=" + endAddressToGoogle + ",+Australia&" +
                    "key=AIzaSyBhEI1X-PMslBS2Ggq35bOncxT05mWO9bs";

            final StringRequest getEndLocRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>()
                    {
                        public void onResponse(String response)
                        {
                            try
                            {
                                JSONObject jsonResponse = new JSONObject(response);
                                System.out.println(jsonResponse.toString());

                                latE = jsonResponse.getJSONArray("results").getJSONObject(0).
                                        getJSONObject("geometry").getJSONObject("location").
                                        getString("lat");
                                lonE = jsonResponse.getJSONArray("results").getJSONObject(0).
                                        getJSONObject("geometry").getJSONObject("location").
                                        getString("lng");

                                // Check response whether it's accurate, if not remind user

                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }

                        /* check if it offer or find  */
                            if (!isFind)
                            {
                                sendRideInfo(activity);
                            }
                            else
                            {
                                sendSearchRideRequest();
                            }

                            // check response, whether it received
                        }
                    },
                    new Response.ErrorListener()
                    {
                        public void onErrorResponse(VolleyError volleyError)
                        {
                            volleyError.printStackTrace();
                            System.out.println("it doesn't work");
                        }
                    });

            MyRequestQueue.getInstance(activity).addToRequestQueue(getEndLocRequest);
        }
    }

    private void sendSearchRideRequest()
    {
        Intent searchResultsIntent = new Intent(OfferRide.this, MyRidesActivity.class);
        searchResultsIntent.putExtra("type", "find");
        searchResultsIntent.putExtra("s_lon", ((FromCurrentLocation.isChecked()) ? Double.toString(lonC) : lonS));
        searchResultsIntent.putExtra("s_lat", ((FromCurrentLocation.isChecked()) ? Double.toString(latC) : latS));
        searchResultsIntent.putExtra("group_id", groupId);
        searchResultsIntent.putExtra("event_id", eventId);
        searchResultsIntent.putExtra("isGroup", isGroup);
        searchResultsIntent.putExtra("e_lon", ((ToCurrentLocation.isChecked()) ? Double.toString(lonC) : lonE));
        searchResultsIntent.putExtra("e_lat", ((ToCurrentLocation.isChecked()) ? Double.toString(latC) : latE));
        searchResultsIntent.putExtra("arrival_time", EditEndTime);
        searchResultsIntent.putExtra("origin", ((FromCurrentLocation.isChecked()) ? currentAddress : startAddress));
        searchResultsIntent.putExtra("destination", ((ToCurrentLocation.isChecked()) ? currentAddress : endAddress));
        startActivity(searchResultsIntent);
        finish();
    }

    private void sendRideInfo(final Activity activity)
    {
        StringRequest OfferRequest = new StringRequest(Request.Method.POST,
                OFFER_RIDE_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                activity.finish();
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

                System.out.println("fallie testing" + latS + lonS);

                if (FromCurrentLocation.isChecked())
                {
                    params.put("s_lat", Double.toString(latC));
                    params.put("s_lon", Double.toString(lonC));
                    params.put("start_add", currentAddress);
                }
                else
                {
                    params.put("s_lat", latS);
                    params.put("s_lon", lonS);
                    params.put("start_add", startAddress);
                }

                if (ToCurrentLocation.isChecked())
                {
                    params.put("e_lat", Double.toString(latC));
                    params.put("e_lon", Double.toString(lonC));
                    params.put("destination", currentAddress);
                }
                else
                {
                    params.put("e_lat", latE);
                    params.put("e_lon", lonE);
                    params.put("destination", endAddress);
                }

                if (isGroup)
                {
                    params.put("group_id", groupId);
                }
                if (isEvent)
                {
                    params.put("event_id", eventId);
                }

                params.put("seat", SeatNo);
                params.put("start_time", EditStartTime);
                params.put("arrival_time", EditEndTime);
                params.put("username", User.getCurrentUser().getUsername());
                //params.put("token", MainActivity.getAuthToken(activity.getApplicationContext()));
                return params;
            }
        };

        MyRequestQueue.getInstance(activity).addToRequestQueue(OfferRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    DatePickerDialog.OnDateSetListener listener1 = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            if (monthOfYear < 9)
            {
                month = "0" + String.valueOf(monthOfYear + 1);
            }
            else
            {
                month = String.valueOf(monthOfYear + 1);
            }

            if (dayOfMonth < 10)
            {
                day = "0" + String.valueOf(dayOfMonth);
            }
            else
            {
                day = String.valueOf(dayOfMonth);
            }

            displayDate.setText(dayOfMonth + "-" + month + "-" + year);
            tempDate = String.valueOf(year) + "-" + month + "-" + day + "T";
        }
    };

    TimePickerDialog.OnTimeSetListener listener2 = new TimePickerDialog.OnTimeSetListener()
    {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            if (hourOfDay < 10)
            {
                hours = "0" + String.valueOf(hourOfDay);
            }
            else
            {
                hours = String.valueOf(hourOfDay);
            }
            if (minute < 10)
            {
                mins = "0" + String.valueOf(minute);
            }
            else
            {
                mins = String.valueOf(minute);
            }

            displayStartTime.setText(hourOfDay + ":" + minute);
            EditStartTime = tempDate + hours + ":" + mins + ":00.000Z";
        }
    };

    TimePickerDialog.OnTimeSetListener listener4 = new TimePickerDialog.OnTimeSetListener()
    {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            if (hourOfDay < 10)
            {
                houra = "0" + String.valueOf(hourOfDay);
            }
            else
            {
                houra = String.valueOf(hourOfDay);
            }

            if (minute < 10)
            {
                mina = "0" + String.valueOf(minute);
            }
            else
            {
                mina = String.valueOf(minute);
            }

            displayArrivalTime.setText(hourOfDay + ":" + minute);
            EditEndTime = tempDate + houra + ":" + mina + ":00.000Z";
            checkTime(hours, mins, houra, mina);
        }
    };

    public void checkTime(String hours, String mins, String houra, String mina)
    {
        if (hours.compareTo(houra) == 0)
        {
            if (mins.compareTo(mina) >= 0)
            {
                Toast.makeText(getApplicationContext(), "Arrival time must be later than start time!", Toast.LENGTH_SHORT).show();
                btnSubmit.setEnabled(false);
            }
            else
            {
                btnSubmit.setEnabled(true);
            }
        }
        else if (hours.compareTo(houra) > 0)
        {
            Toast.makeText(getApplicationContext(), "Arrival time must be later than start time!", Toast.LENGTH_SHORT).show();
            btnSubmit.setEnabled(false);
        }
        else
        {
            btnSubmit.setEnabled(true);
        }
    }

    public void setDate(View view)
    {
        new DatePickerDialog(OfferRide.this, listener1, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    public void setStartTime(View view)
    {
        new TimePickerDialog(OfferRide.this, listener2, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true).show();
    }

    public void setArrivalTime(View view)
    {
        new TimePickerDialog(OfferRide.this, listener4, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true).show();
    }

    //Check whether all information needed for offering a ride
    // has been typed in by user
    public boolean inputValid()
    {
        boolean checkBelong = false, checkStart = false, checkEnd = false;
        if (isGroup || isEvent)
        {
            checkBelong = true;
        }
        else
        {
            System.out.println("Have not choose any group or event!!");
            Toast.makeText(getApplicationContext(), "Must select a group or an event!", Toast.LENGTH_SHORT).show();
        }
        if (!EditStart.getText().toString().isEmpty() || isFromEvent || FromCurrentLocation.isChecked())
        {
            checkStart = true;
        }
        else
        {
            System.out.println("Have not input start point!!");
            Toast.makeText(getApplicationContext(), "Must set start point!", Toast.LENGTH_SHORT).show();
        }
        if (!EditEnd.getText().toString().isEmpty() || isToEvent || ToCurrentLocation.isChecked())
        {
            checkEnd = true;
        }
        else
        {
            System.out.println("Have not input end point!!");
            Toast.makeText(getApplicationContext(), "Must set end point!", Toast.LENGTH_SHORT).show();
        }

        return !((!checkBelong) || displayDate.getText().toString().isEmpty() ||
                (displayStartTime.getText().toString().isEmpty() && !isFind) ||
                displayArrivalTime.getText().toString().isEmpty() ||
                (!checkStart) || (!checkEnd));
    }

    public void offerRide(View view)
    {
        if (inputValid())
        {
            if (isEvent)
            {
                if (isToEvent)
                {
                    startAddress = EditStart.getText().toString();
                    endAddress = eventLocation;
                }
                else if (isFromEvent)
                {
                    startAddress = eventLocation;
                    endAddress = EditEnd.getText().toString();
                }
            }
            if (isGroup)
            {
                startAddress = EditStart.getText().toString();
                endAddress = EditEnd.getText().toString();
                //  Toast.makeText(getApplicationContext(),"Group info"+startAddress,Toast.LENGTH_SHORT).show();
            }
            sendRequest(this);
        }
        else
        {
            System.out.println("Invalid Input!!!");
            Toast.makeText(getApplicationContext(), "input invalid!!", Toast.LENGTH_SHORT).show();
        }
    }
}
