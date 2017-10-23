package com.example.rex.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Rex on 8/06/2017.
 */

public class HillListAdapter extends ArrayAdapter<Hill> {

    public HillListAdapter(Context context, ArrayList<Hill> hills){
        super(context, 0, hills);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Hill hill = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.hill_list_item, parent, false);
        }

        RatingBar queriedRating = (RatingBar) convertView.findViewById(R.id.queriedRating);
        TextView queriedSlope = (TextView) convertView.findViewById(R.id.queriedSlope);
        TextView queriedTerrain = (TextView) convertView.findViewById(R.id.queriedTerrain);

        queriedRating.setRating(hill.getmMeanRating());
        queriedSlope.setText(hill.getmSlope());
        queriedTerrain.setText(hill.getmTerrain());

        return convertView;
    }

}
