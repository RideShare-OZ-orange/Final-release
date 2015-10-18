package com.swen900014.orange.rideshareoz.views;

/**
 * Created by Geroge on 6/09/2015.
 * Display a list of rides relevant to the current user,
 * or a list of search results
 */


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.swen900014.orange.rideshareoz.R;

public class MyRidesActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myrides);
        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, (new MyRidesFragment()))
                    .commit();
        }

        getIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Menu is not required for search
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
