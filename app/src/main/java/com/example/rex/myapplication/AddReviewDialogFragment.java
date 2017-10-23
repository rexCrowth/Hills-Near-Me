package com.example.rex.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by Rex on 18/05/2017.
 */

public class AddReviewDialogFragment extends DialogFragment implements RatingBar.OnRatingBarChangeListener {
    AddReviewToDataBaseListener mCallback;
    private EditText reviewTitle;
    private RatingBar addReviewRating;
    private RatingBar addTrafficRevRating;
    private EditText descripText;
    private Button saveButton;

    private Hill hill;
    private String hillId;
    private Review review;
    private String title;
    private float revRating;
    private float trafficRating;
    private String description;


    public AddReviewDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static AddReviewDialogFragment newInstance(Hill hill) {
        AddReviewDialogFragment frag = new AddReviewDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("Hill", hill);
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
        final View view = inflater.inflate(R.layout.add_review_dialog, null);

        // Get field from view
        reviewTitle = (EditText) view.findViewById(R.id.addReviewDetailTitle);
        addReviewRating = (RatingBar) view.findViewById(R.id.addReviewRating);
        addTrafficRevRating = (RatingBar) view.findViewById(R.id.addTrafficRevRating);
        descripText = (EditText) view.findViewById(R.id.addDescripText);
        saveButton = (Button) view.findViewById(R.id.saveRevButton);

        addReviewRating.setOnRatingBarChangeListener(this);
        addTrafficRevRating.setOnRatingBarChangeListener(this);

        hill = getArguments().getParcelable("Hill");
        hillId = hill.getHillId();

        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                title = reviewTitle.getText().toString();
                description = descripText.getText().toString();
                if(title.equals("")){
                    Toast.makeText(getActivity(), "Add a title to your review!",
                            Toast.LENGTH_SHORT).show();
                }else if(revRating < 0.5){
                    Toast.makeText(getActivity(), "Select a rating for the hill!",
                            Toast.LENGTH_SHORT).show();
                }else if(trafficRating < 0.5){
                    Toast.makeText(getActivity(), "Select a rating for amount of traffic!",
                            Toast.LENGTH_SHORT).show();
                }else if(description.equals("")){
                    Toast.makeText(getActivity(), "Add your thoughts on the hill!",
                            Toast.LENGTH_SHORT).show();
                }else {
                    review = new Review(hillId, title, revRating, trafficRating, description);
                    mCallback.AddReviewToDataBaseListener(hill,review);
                    dismiss();
                }
            }
        });

        builder.setView(view);
        return builder.create();
    }

    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromTouch) {

        switch (ratingBar.getId()) {
            case R.id.addReviewRating:
                final int hillNumStars = addReviewRating.getNumStars();
                revRating = addReviewRating.getRating();
                final float hillRatingBarStepSize = addReviewRating.getStepSize();
            case R.id.addTrafficRevRating:
                final int trafficNumStars = addTrafficRevRating.getNumStars();
                trafficRating = addTrafficRevRating.getRating();
                final float trafficRatingBarStepSize = addTrafficRevRating.getStepSize();
        }
    }


    public interface AddReviewToDataBaseListener {
        public void AddReviewToDataBaseListener(Hill hill,Review review);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (AddReviewToDataBaseListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement AddReviewToDataBaseListener");
        }
    }

}
