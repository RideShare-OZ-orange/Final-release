package com.swen900014.orange.rideshareoz.utils;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


/**
 * Created by Sangzhuoyang Yu on 9/12/15.
 * It contains a static request queue managing post
 * and get requests. It is instantiated once the
 * program starts, and destroyed at the end of
 * the whole program lifetime. Every time when you
 * need to send request, just create a new request
 * object, and add it to the queue. Notice that you
 * need to rewrite the method creating the request object,
 * it is because in most cases we need to do something
 * with the respond.
 */
public class MyRequestQueue
{
    private static MyRequestQueue mInstance;
    private static Context mCtx;
    private RequestQueue mRequestQueue;

    private MyRequestQueue(Context context)
    {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized MyRequestQueue getInstance(Context context)
    {
        if (mInstance == null)
        {
            mInstance = new MyRequestQueue(context);
        }

        return mInstance;
    }

    public RequestQueue getRequestQueue()
    {
        if (mRequestQueue == null)
        {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req)
    {
        getRequestQueue().add(req);
    }
}
