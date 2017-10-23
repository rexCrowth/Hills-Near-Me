package com.example.rex.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Rex on 7/05/2017.
 */

public class ReviewListAdapter extends ArrayAdapter<Review> implements Filterable {
    private ArrayList<Review> originalList;
    private ArrayList<Review> restoreList;
    private ArrayList<Review> filteredList = null;

    public ReviewListAdapter(Context context, ArrayList<Review> reviews) {
        super(context, 0, reviews);
        this.originalList = reviews;
        this.restoreList = new ArrayList<Review>();
        this.restoreList.addAll(originalList);
        this.filteredList = new ArrayList<Review>();
        this.filteredList.addAll(originalList);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Review review = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_list_item, parent, false);
        }
        // Lookup view for data population
        RatingBar hillRevRating = (RatingBar) convertView.findViewById(R.id.hillRevRating);
        TextView hillTitle = (TextView) convertView.findViewById(R.id.hillTitle);
        // Populate the data into the template view using the data object
        hillRevRating.setRating(review.getmRating());
        hillTitle.setText(review.getmTitle());
        // Return the completed view to render on screen
        return convertView;
    }
    public void filter(String filterValue, String filterType) {
       originalList.clear();
        if (filterType.equals("All")) {
            originalList.addAll(filteredList);
        }
        else {
            if(filterType.equals("Title")) {
                originalList.clear();
                filterValue = filterValue.toLowerCase(Locale.getDefault());
                for (Review review : filteredList) {
                    if (review.getmTitle().toLowerCase(Locale.getDefault()).contains(filterValue)) {
                        originalList.add(review);
                    }
                }
                if(originalList.size() == 0){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                    alertDialogBuilder.setMessage("No results returned for search.");
                    alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            originalList.addAll(restoreList);
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            } if(filterType.equals("Rating")){
                originalList.clear();
                float filterResult = Float.valueOf(filterValue);
                for (Review review : filteredList) {
                    if (review.getmRating() == filterResult) {
                        originalList.add(review);
                    }
                }
                if(originalList.size() == 0) {
                    originalList.addAll(restoreList);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                    alertDialogBuilder.setMessage("No results returned for search.");
                    alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            } if(filterType.equals("Traffic")){
                originalList.clear();
                float filterResult = Float.valueOf(filterValue);
                for (Review review : filteredList) {
                    if (review.getmTraffic() == filterResult) {
                        originalList.add(review);
                    }
                }
                if(originalList.size() == 0) {
                    originalList.addAll(restoreList);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                    alertDialogBuilder.setMessage("No results returned for search.");
                    alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        }
        notifyDataSetChanged();
    }
}
