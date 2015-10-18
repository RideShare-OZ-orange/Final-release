package com.swen900014.orange.rideshareoz.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by Sangzhuoyang Yu on 9/22/15.
 * Urls, IDs, Boundaries
 */
public class Resources
{
    public final static String SERVER_CLIENT_ID =
            "728068031979-l803m9527jv2ks6hh4qm8sg6nqr8thgl.apps.googleusercontent.com";
    public static final LatLngBounds BOUNDS_GREATER_MELBOURNE = new LatLngBounds(
            new LatLng(-38.260720, 144.394492), new LatLng(-37.459846, 145.764740));

    public final static String GETALL_RIDE_URL = "http://144.6.226.237/ride/getall";
    public final static String GETALL_USER_URL = "http://144.6.226.237/user/getall";
    public final static String GETUSER_RELAVENT_RIDE_URL = "http://144.6.226.237/user/getRides";
    public final static String LOGIN_URL = "http://144.6.226.237/user/login";

    public final static String SEARCH_RIDE_URL = "http://144.6.226.237/ride/search";
    public final static String OFFER_RIDE_URL = "http://144.6.226.237/ride/create?";
    public final static String CANCEL_RIDE_URL = "http://144.6.226.237/ride/cancel";
    public final static String ACCEPT_REQUEST_URL = "http://144.6.226.237/ride/accept";
    public final static String REJECT_REQUEST_URL = "http://144.6.226.237/ride/reject";
    public final static String JOIN_REQUEST_URL = "http://144.6.226.237/ride/request";
    public final static String LEAVE_RIDE_URL = "http://144.6.226.237/ride/leave";

    public final static String JOIN_GROUP_URL = "http://144.6.226.237/group/request";
    public final static String LEAVE_GROUP_URL = "http://144.6.226.237/group/leave";

    //public final static String GET_USER_RELEVANT_GROUP_URL = "http://144.6.226.237/user/getGroups";
    public final static String GETALL_GROUP_URL = "http://144.6.226.237/user/getAllGroups";
    public final static String GETALL_EVENT_URL = "http://144.6.226.237/event/getall";

    public final static String RATE_USER_URL = "http://144.6.226.237/credit/rate";
    public final static String UPDATE_USER = "http://144.6.226.237/user/update";
}
