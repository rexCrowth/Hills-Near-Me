package com.example.rex.myapplication;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Rex on 22/04/2017.
 */

public class ViewHillDialogFragment extends DialogFragment {
    private RatingBar hillRating;
    private ImageView addFavourite;
    private TextView slopeValue;
    private TextView lengthValue;
    private TextView terrainValue;
    private RatingBar trafficRating;
    private TextView viewReviewButton;
    private Button addReviewButton;
    private Hill hill;

    private ArrayList<Review> mReviews = new ArrayList<Review>();

    public ViewHillDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ViewHillDialogFragment newInstance(Hill hill, ArrayList<Review> reviews) {
        ViewHillDialogFragment frag = new ViewHillDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("Hill", hill);
        args.putParcelableArrayList("Reviews", reviews);
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
        View view = inflater.inflate(R.layout.view_hill_dialog, null);

        // Get field from view
        hillRating = (RatingBar) view.findViewById(R.id.hillRating);
        slopeValue = (TextView) view.findViewById(R.id.slopeValue);
        lengthValue = (TextView) view.findViewById(R.id.lengthValue);
        terrainValue = (TextView) view.findViewById(R.id.terrainValue);
        trafficRating = (RatingBar) view.findViewById(R.id.trafficRating);
        viewReviewButton = (TextView) view.findViewById(R.id.viewReviewButton);
        addReviewButton = (Button) view.findViewById(R.id.addReviewButton);

        hill = getArguments().getParcelable("Hill");
        mReviews = getArguments().getParcelableArrayList("Reviews");




        hillRating.setRating(hill.getmMeanRating());
        slopeValue.setText(hill.getmSlope());
        lengthValue.setText(hill.getmDistance());
        terrainValue.setText(hill.getmTerrain());
        trafficRating.setRating(hill.getmTraffic());
        viewReviewButton.setText(mReviews.size() + " reviews of this hill");
        viewReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mReviews.isEmpty()) {
                    showViewReviewDialog(mReviews);
                }else{
                    viewReviewButton.setClickable(false);
                }
            }
        });
        addReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReviewDialog(hill);
                dismiss();
            }
        });
        builder.setView(view);
        return builder.create();
    }

    public void showViewReviewDialog(ArrayList<Review> reviews) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = ViewReviewListDialogFragment.newInstance(reviews);
        dialog.show(getActivity().getSupportFragmentManager(),"view_review_list_frag");
    }
    public void addReviewDialog(Hill hill) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = AddReviewDialogFragment.newInstance(hill);
        dialog.show(getActivity().getSupportFragmentManager(),"add_review_frag");
    }
}
