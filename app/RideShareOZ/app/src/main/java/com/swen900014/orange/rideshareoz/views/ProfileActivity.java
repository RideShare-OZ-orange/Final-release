package com.swen900014.orange.rideshareoz.views;

/**
 * Created by Geroge on 6/09/2015.
 * Display a list of rides relevant to the current user,
 * or a list of search results
 */


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.swen900014.orange.rideshareoz.utils.MyRequestQueue;
import com.swen900014.orange.rideshareoz.R;
import com.swen900014.orange.rideshareoz.models.User;
import com.swen900014.orange.rideshareoz.utils.Resources;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener
{

    private Button btnUpdate;
    private EditText phone;
    private EditText about;
    private EditText licence;
    private ProfileFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        phone = (EditText) findViewById(R.id.editPhone);
        about = (EditText) findViewById(R.id.editInto);
        licence = (EditText) findViewById(R.id.editLicence);

        phone.setText(User.getCurrentUser().getPhone());
        about.setText(User.getCurrentUser().getAbout());
        licence.setText(User.getCurrentUser().getLicence());
        btnUpdate.setOnClickListener(this);
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

    @Override
    public void onRestart()
    {
        super.onRestart();
    }

    @Override
    public void onClick(View v)
    {
        StringRequest getGroupsRequest = new StringRequest(Request.Method.POST,
                Resources.UPDATE_USER, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                //End the activity
                Toast.makeText(getApplicationContext(), "Information Updated", Toast.LENGTH_SHORT).show();
                finish();
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
                params.put("token", MainActivity.getAuthToken(getApplicationContext()));
                params.put("phone", phone.getText().toString());
                params.put("note", about.getText().toString());
                params.put("driver_license", licence.getText().toString());

                return params;
            }
        };

        MyRequestQueue.getInstance(this).addToRequestQueue(getGroupsRequest);

        User.getCurrentUser().setPhone(phone.getText().toString());
        User.getCurrentUser().setLicence(licence.getText().toString());
        User.getCurrentUser().setAbout(about.getText().toString());
        finish();
    }
}
