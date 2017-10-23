package com.example.rex.myapplication;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by Rex on 8/05/2017.
 */

public class ViewReviewDetailDialogFragment extends DialogFragment {
    private TextView reviewTitle;
    private RatingBar reviewRating;
    private RatingBar trafficRevRating;
    private TextView descripText;
    private Review review;

    public ViewReviewDetailDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ViewReviewDetailDialogFragment newInstance(Review review) {
        ViewReviewDetailDialogFragment frag = new ViewReviewDetailDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("Review", review);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.review_details, null);

        // Get field from view
        reviewTitle = (TextView) view.findViewById(R.id.reviewDetailTitle);
        reviewRating = (RatingBar) view.findViewById(R.id.reviewRating);
        trafficRevRating = (RatingBar) view.findViewById(R.id.trafficRevRating);
        descripText = (TextView) view.findViewById(R.id.descripText);

        review = getArguments().getParcelable("Review");

        reviewTitle.setText(review.getmTitle());
        reviewRating.setRating(review.getmRating());
        trafficRevRating.setRating(review.getmTraffic());
        descripText.setText(review.getmDescription());

        builder.setView(view);
        return builder.create();
    }



}
