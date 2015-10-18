package com.swen900014.orange.rideshareoz.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by George on 9/10/2015.
 */
public class DateFormatter
{
    private static SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat outputDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");

    public static String format(String date)
    {
        //clean date
        if (date.length() > 0)
        {
            date = date.substring(0, 10) + " " + date.substring(11, 19);
        }
        try
        {
            return outputDateFormat.format(inputDateFormat.parse(date));
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
