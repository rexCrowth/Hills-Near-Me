package com.example.rex.myapplication;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import static com.example.rex.myapplication.R.id.filterRatingsButton;
import static com.example.rex.myapplication.R.id.filterRevRating;
import static com.example.rex.myapplication.R.id.parent;

/**
 * Created by Rex on 7/05/2017.
 */

public class ViewReviewListDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener,
        RatingBar.OnRatingBarChangeListener{
    private ListView mReviewList;
    private ArrayList<Review> mReviews = new ArrayList<Review>();
    private ReviewListAdapter adapter;
    private Spinner filterOptions;
    private String selectedFilter;
    private EditText reviewTitle;
    private RatingBar reviewMeanRating;
    private RatingBar reviewTrafficRating;
    private Button searchFilterButton;

    private String filteredTitle;
    private float filteredRating;
    private float filteredTraffic;
    private boolean filterOnRating;
    private boolean filterOnTitle;


    public ViewReviewListDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ViewReviewListDialogFragment newInstance(ArrayList<Review> reviews) {
        ViewReviewListDialogFragment frag = new ViewReviewListDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("Review", reviews);
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
        View view = inflater.inflate(R.layout.review_list_view, null);
        mReviewList = (ListView) view.findViewById(R.id.reviewList);

        //initialise listview header
        filterOptions = (Spinner) view.findViewById(R.id.filterOptions);

        reviewTitle = (EditText) view.findViewById(R.id.filterReviewDetailTitle);
        reviewMeanRating = (RatingBar) view.findViewById(R.id.filterRevRating);
        reviewTrafficRating = (RatingBar) view.findViewById(R.id.filterTrafficRating);
        searchFilterButton = (Button) view.findViewById(R.id.filterRatingsButton);

        reviewMeanRating.setOnRatingBarChangeListener(this);
        reviewTrafficRating.setOnRatingBarChangeListener(this);

        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.review_filter_options,android.R.layout.simple_spinner_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterOptions.setAdapter(filterAdapter);
        filterOptions.setOnItemSelectedListener(this);



        mReviews = getArguments().getParcelableArrayList("Review");

        //initialise adapter to this dialog fragment and to review arraylist
        adapter = new ReviewListAdapter(this.getContext(),mReviews);
        //set the listview adapter to this adapter
        mReviewList.setAdapter(adapter);
        //handle clicks on listview items
        mReviewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                //set review object to review at adapter position
                Review review = adapter.getItem(position);
                //run the review detail dialog fragment
                showReviewDetailDialog(review);
            }
        });
        reviewTitle.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                filteredTitle  = reviewTitle.getText().toString().toLowerCase(Locale.getDefault());
                filterOnTitle = true;
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }
        });

        searchFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filterOnTitle == true){
                    adapter.filter(filteredTitle,"Title");
                    reviewTitle.setText("");
                    filteredTitle = "";
                }else if (filterOnRating == true) {
                    String reviewNumStars = Float.toString(filteredRating);
                    adapter.filter(reviewNumStars, "Rating");
                    reviewMeanRating.setRating(0);
                    filteredRating = 0;
                }else if (filterOnRating == false){
                    String trafficNumStars = Float.toString(filteredTraffic);
                    adapter.filter(trafficNumStars, "Traffic");
                    reviewTrafficRating.setRating(0);
                    filteredTraffic = 0;
                }
            }
        });

        builder.setView(view);
        return builder.create();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
        switch (parent.getId()){
            case R.id.filterOptions:
                selectedFilter = parent.getItemAtPosition(pos).toString();
                break;
        }
        if(selectedFilter.equals("Title")){
            reviewTitle.setEnabled(true);
            reviewTitle.setVisibility(View.VISIBLE);
            searchFilterButton.setEnabled(true);
            searchFilterButton.setVisibility(View.VISIBLE);
            reviewMeanRating.setHapticFeedbackEnabled(false);
            reviewMeanRating.setVisibility(View.INVISIBLE);
            reviewTrafficRating.setHapticFeedbackEnabled(false);
            reviewTrafficRating.setVisibility(View.INVISIBLE);
        } else if (selectedFilter.equals("Rating")){
            reviewMeanRating.setHapticFeedbackEnabled(true);
            reviewMeanRating.setVisibility(View.VISIBLE);
            searchFilterButton.setEnabled(true);
            searchFilterButton.setVisibility(View.VISIBLE);
            reviewTitle.setEnabled(false);
            reviewTitle.setVisibility(View.INVISIBLE);
            reviewTitle.setText(null);
            reviewTrafficRating.setHapticFeedbackEnabled(false);
            reviewTrafficRating.setVisibility(View.INVISIBLE);
        } else if(selectedFilter.equals("Traffic")){
            reviewTrafficRating.setHapticFeedbackEnabled(true);
            reviewTrafficRating.setVisibility(View.VISIBLE);
            searchFilterButton.setEnabled(true);
            searchFilterButton.setVisibility(View.VISIBLE);
            reviewTitle.setEnabled(false);
            reviewTitle.setVisibility(View.INVISIBLE);
            reviewTitle.setText(null);
            reviewMeanRating.setHapticFeedbackEnabled(false);
            reviewMeanRating.setVisibility(View.INVISIBLE);
        } else if (selectedFilter.equals("All")){
            reviewTitle.setEnabled(false);
            reviewTitle.setVisibility(View.INVISIBLE);
            reviewTitle.setText(null);
            reviewMeanRating.setHapticFeedbackEnabled(false);
            reviewMeanRating.setVisibility(View.INVISIBLE);
            reviewTrafficRating.setHapticFeedbackEnabled(false);
            reviewTrafficRating.setVisibility(View.INVISIBLE);
            searchFilterButton.setEnabled(false);
            searchFilterButton.setVisibility(View.INVISIBLE);
            adapter.filter("", "All");
        }
    }
    public void onNothingSelected(AdapterView<?> parent){

    }

    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromTouch) {

        switch (ratingBar.getId()) {
            case R.id.filterRevRating:
                filterOnTitle = false;
                filterOnRating = true;
                filteredRating = reviewMeanRating.getRating();
                break;
            case R.id.filterTrafficRating:
                filterOnTitle = false;
                filterOnRating = false;
                filteredTraffic = reviewTrafficRating.getRating();
                break;
        }

    }

    public void showReviewDetailDialog(Review review) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = ViewReviewDetailDialogFragment.newInstance(review);
        dialog.show(getActivity().getSupportFragmentManager(),"view_review_detail_frag");
    }
}
