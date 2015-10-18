package com.swen900014.orange.rideshareoz.utils;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;

/**
 * Created by Fallie on 2015/10/8.
 */
public class GPSTracker extends Service implements LocationListener, android.location.LocationListener
{

    private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation;

    public static boolean isCancelled;
    Location location; // location
    double latitude = 0; // latitude
    double longitude = 0; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GPSTracker(Context context)
    {
        this.mContext = context;
        getLocation();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public Location getLocation()
    {
        try
        {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled)
            {
                // no network provider is enabled
                this.canGetLocation = false;
            }
            else
            {
                this.canGetLocation = true;
                // First get location from Network Provider
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled)
                {
                    if (location == null)
                    {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        int i = 100;
                        while (i > 0)
                        {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null)
                            {
                                Log.d("Location", "Latitude: " + location.getLatitude());
                                Log.d("Location", "location: " + location.getLongitude());

                                // Stop tracking the location once retrieved it successfully
                                // to avoid power overload
                                locationManager.removeUpdates(this);
                                break;
                            }
                            else
                            {
                                Log.d("Location", "Latitude: " + 0);
                                Log.d("Location", "location: " + 0);
                            }
                            try
                            {
                                Thread.sleep(10000);
                            } catch (InterruptedException e)
                            {
                                Log.e("Location", e.getMessage());
                            }
                            i--;
                        }
                        if (locationManager != null)
                        {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null)
                            {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
                if (isNetworkEnabled)
                {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (location == null)
                    {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("Location is NULL", "omgNULL AGAIN!!!!");
                        int i = 100;
                        while (i > 0)
                        {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null)
                            {
                                Log.d("Location", "Latitude: " + location.getLatitude());
                                Log.d("Location", "location: " + location.getLongitude());
                                locationManager.removeUpdates(this);
                                break;
                            }
                            else
                            {
                                Log.d("Location", "Latitude: " + 0);
                                Log.d("Location", "location: " + 0);
                            }
                            try
                            {
                                Thread.sleep(10000);
                            } catch (InterruptedException e)
                            {
                                Log.e("Location", e.getMessage());
                            }
                            i--;
                        }
                    }
                    Log.d("Network", "Network");
                    if (locationManager != null)
                    {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null)
                        {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                        Toast.makeText(getApplicationContext(), "Lat=" + latitude, Toast.LENGTH_SHORT).show();
                    }
                }

            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void stopUsingGPS()
    {
        if (locationManager != null)
        {


            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.

            Toast.makeText(getApplicationContext(), "Missing ACCESS_FINE_LOCATION",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        locationManager.removeUpdates(GPSTracker.this);
    }


    /**
     * Function to get latitude
     */
    public double getLatitude()
    {
        if (location != null)
        {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude()
    {
        if (location != null)
        {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation()
    {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public void showSettingsAlert()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS setting");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                GPSTracker.isCancelled = false;

                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);

            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                GPSTracker.isCancelled = true;

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public void onLocationChanged(Location location)
    {
        if (location != null)
        {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        else
        {
            Log.d("Location", "location is null!!!");
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    @Override
    public void onProviderEnabled(String provider)
    {

    }

    @Override
    public void onProviderDisabled(String provider)
    {

    }

    public boolean getState()
    {
        return GPSTracker.isCancelled;
    }
}