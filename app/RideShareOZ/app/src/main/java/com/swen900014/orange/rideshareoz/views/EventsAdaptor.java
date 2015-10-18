package com.swen900014.orange.rideshareoz.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.swen900014.orange.rideshareoz.R;
import com.swen900014.orange.rideshareoz.models.Event;

import java.util.ArrayList;

/**
 * Created by George on 15/09/2015.
 * Adapter attached to the list view in
 * MyRides activity, displaying all rides
 * info retrieved from the server
 */
public class EventsAdaptor extends ArrayAdapter<Event>
{

    public EventsAdaptor(Context context, ArrayList<Event> events)
    {
        super(context, 0, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Get the data item for this position
        Event event = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_events_linear, parent, false);
        }

        // Lookup view for data population
        TextView tvStart = (TextView) convertView.findViewById(R.id.textViewStartDate);
        TextView tvEnd = (TextView) convertView.findViewById(R.id.textViewEndDate);
        TextView tvName = (TextView) convertView.findViewById(R.id.textViewName);
        TextView tvDesc = (TextView) convertView.findViewById(R.id.textViewDesc);
        TextView tvLoc = (TextView) convertView.findViewById(R.id.textViewLocation);

        // Populate the data into the template view using the data object
        tvStart.setText(event.getStart_time());
        tvEnd.setText(event.getEnd_time());
        tvName.setText(event.getName());
        tvDesc.setText(event.getDescription());
        tvLoc.setText(event.getEventLocation().getAddress());
        // Return the completed view to render on screen
        return convertView;
    }
}
