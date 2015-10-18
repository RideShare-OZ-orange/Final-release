package com.swen900014.orange.rideshareoz.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.swen900014.orange.rideshareoz.R;
import com.swen900014.orange.rideshareoz.models.Group;

import java.util.ArrayList;

/**
 * Created by George on 15/09/2015.
 * Adapter attached to the list view in
 * MyRides activity, displaying all rides
 * info retrieved from the server
 */
public class GroupsAdaptor extends ArrayAdapter<Group>
{

    public GroupsAdaptor(Context context, ArrayList<Group> groups)
    {
        super(context, 0, groups);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Get the data item for this position
        Group group = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_groups_linear, parent, false);
        }

        // Lookup view for data population
        TextView tvType = (TextView) convertView.findViewById(R.id.textViewType);
        TextView tvName = (TextView) convertView.findViewById(R.id.textViewName);

        // Populate the data into the template view using the data object
        tvType.setText(group.getGroupState().toString() + " ");
        tvName.setText(group.getName());
        // Return the completed view to render on screen
        return convertView;
    }
}
