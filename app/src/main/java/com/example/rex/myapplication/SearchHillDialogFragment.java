package com.example.rex.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Rex on 6/06/2017.
 */

public class SearchHillDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener,
        RatingBar.OnRatingBarChangeListener, SeekBar.OnSeekBarChangeListener{

    private RatingBar searchHillRating;
    private Spinner searchSlopeValue;
    private SeekBar searchlengthValue;
    private TextView searchLengthDisplay;
    private Spinner searchTerrainValue;
    private RatingBar searchTrafficRating;
    private SeekBar searchDistanceValue;
    private TextView searchDistanceDisplay;
    private Button searchButton;

    private TextView searchHillRatingTitle;
    private boolean ratingEnabled = true;
    private TextView searchSlopeTitle;
    private boolean slopeEnabled = true;
    private TextView searchLengthTitle;
    private boolean lengthEnabled = true;
    private TextView searchTerrainTitle;
    private boolean terrainEnabled = true;
    private TextView searchTrafficTitle;
    private boolean trafficEnabled = true;
    private boolean noResults;


    SearchQueryDataBaseListener mCallback;
    private ArrayList<Hill> hills;
    private Location location;
    private float meanRating;
    private float traffic;
    private String slope;
    private String terrain;
    private int length;
    private int distance;

    public SearchHillDialogFragment() {
    }

    public static SearchHillDialogFragment newInstance(Location location, ArrayList<Hill> queryHills){
        SearchHillDialogFragment frag = new SearchHillDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("Location", location);
        args.putParcelableArrayList("Hills", queryHills);
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
        final View view = inflater.inflate(R.layout.search_hill_filter_dialog, null);

        searchHillRating = (RatingBar) view.findViewById(R.id.searchHillRating);
        searchSlopeValue = (Spinner) view.findViewById(R.id.searchSlopeValue);
        searchlengthValue = (SeekBar) view.findViewById(R.id.searchLengthValue);
        searchTerrainValue = (Spinner) view.findViewById(R.id.searchTerrainValue);
        searchLengthDisplay = (TextView) view.findViewById(R.id.searchLengthDisplay);
        searchTrafficRating = (RatingBar) view.findViewById(R.id.searchTrafficRating);
        searchDistanceValue = (SeekBar) view.findViewById(R.id.searchDistanceValue);
        searchDistanceDisplay = (TextView) view.findViewById(R.id.searchDistanceDisplay);
        searchButton = (Button) view.findViewById(R.id.searchButton);

        //textview title of each filter option is clickable, and a click will disable/enable the
        // relevant field for search. disable/enable is tracked via boolean value
        searchHillRatingTitle = (TextView) view.findViewById(R.id.searchHillRatingTitle);
        searchHillRatingTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ratingEnabled == true){
                    searchHillRating.setHapticFeedbackEnabled(false);
                    searchHillRating.setVisibility(View.INVISIBLE);
                    searchHillRating.setRating(-1);
                    ratingEnabled = false;
                }else if (ratingEnabled == false){
                    searchHillRating.setHapticFeedbackEnabled(true);
                    searchHillRating.setVisibility(View.VISIBLE);
                    ratingEnabled = true;
                }

            }
        });
        searchSlopeTitle = (TextView) view.findViewById(R.id.searchSlopeTitle);
        searchSlopeTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(slopeEnabled == true){
                    searchSlopeValue.setEnabled(false);
                    searchSlopeValue.setVisibility(View.INVISIBLE);
                    slopeEnabled = false;
                }else if(slopeEnabled == false){
                    searchSlopeValue.setEnabled(true);
                    searchSlopeValue.setVisibility(View.VISIBLE);
                    slopeEnabled = true;
                }

            }
        });
        searchLengthTitle = (TextView) view.findViewById(R.id.searchLengthTitle);
        searchLengthTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lengthEnabled == true){
                    searchlengthValue.setEnabled(false);
                    searchlengthValue.setVisibility(View.INVISIBLE);
                    searchlengthValue.setProgress(-1);
                    searchLengthDisplay.setVisibility(View.INVISIBLE);
                    lengthEnabled = false;
                }else if(lengthEnabled == false){
                    searchlengthValue.setEnabled(true);
                    searchlengthValue.setVisibility(View.VISIBLE);
                    searchLengthDisplay.setVisibility(View.VISIBLE);
                    lengthEnabled = true;
                }
            }
        });
        searchTerrainTitle = (TextView) view.findViewById(R.id.searchTerrainTitle);
        searchTerrainTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(terrainEnabled == true){
                    searchTerrainValue.setEnabled(false);
                    searchTerrainValue.setVisibility(View.INVISIBLE);
                    terrainEnabled = false;
                }else if(terrainEnabled == false){
                    searchTerrainValue.setEnabled(true);
                    searchTerrainValue.setVisibility(View.VISIBLE);
                    terrainEnabled = true;
                }
            }
        });
        searchTrafficTitle = (TextView) view.findViewById(R.id.searchTrafficTitle);
        searchTrafficTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(trafficEnabled == true){
                    searchTrafficRating.setHapticFeedbackEnabled(false);
                    searchTrafficRating.setVisibility(View.INVISIBLE);
                    searchTrafficRating.setRating(-1);
                    trafficEnabled = false;
                }else if(trafficEnabled == false){
                    searchTrafficRating.setHapticFeedbackEnabled(true);
                    searchTrafficRating.setVisibility(View.VISIBLE);
                    trafficEnabled = true;
                }
            }
        });
    // set listeners for seekbars and ratingbars to track values
        searchDistanceValue.setOnSeekBarChangeListener(this);
        searchlengthValue.setOnSeekBarChangeListener(this);
        searchHillRating.setOnRatingBarChangeListener(this);
        searchTrafficRating.setOnRatingBarChangeListener(this);
    // adapters set up to monitor the spinners and set the values of dropdown etc
        ArrayAdapter<CharSequence> slopeAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.slope_array,android.R.layout.simple_spinner_item);
        slopeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchSlopeValue.setAdapter(slopeAdapter);
        searchSlopeValue.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> terrainAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.terrain_array,android.R.layout.simple_spinner_item);
        terrainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchTerrainValue.setAdapter(terrainAdapter);
        searchTerrainValue.setOnItemSelectedListener(this);
    // retrieve hill arraylist and location from arguments
        hills = getArguments().getParcelableArrayList("Hills");
        location = getArguments().getParcelable("Location");

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processQuery(hills,location,meanRating,traffic,slope,terrain,length,distance);
                if(noResults){
                    Toast.makeText(getContext(), "No results!",
                            Toast.LENGTH_SHORT).show();
                    dismiss();
                }
                if(!noResults){
                    mCallback.SearchQueryDataBaseListener(hills);
                    dismiss();
                }

            }
        });
        builder.setView(view);
        return builder.create();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
        switch (parent.getId()){
            case R.id.searchSlopeValue:
                slope = parent.getItemAtPosition(pos).toString();
                break;
            case R.id.searchTerrainValue:
                terrain = parent.getItemAtPosition(pos).toString();
                break;
        }
    }
    public void onNothingSelected(AdapterView<?> parent){
    }

    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromTouch) {

        switch (ratingBar.getId()) {
            case R.id.searchHillRating:
                meanRating = searchHillRating.getRating();
            case R.id.searchTrafficRating:
                traffic = searchTrafficRating.getRating();

        }
    }

    public void onStartTrackingTouch(SeekBar seekBar){
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
        switch (seekBar.getId()) {
            case R.id.searchLengthValue:
                length = (searchlengthValue.getProgress()+1) * 100;
                searchLengthDisplay.setText(length + "m");
                break;
            case R.id.searchDistanceValue:
                distance = (searchDistanceValue.getProgress()+1) * 10;
                searchDistanceDisplay.setText(distance + "km");
                break;
        }
    }

    public void onStopTrackingTouch(SeekBar seekBar){
    }

    public void processQuery(ArrayList<Hill> hills, Location location, float meanRating, float traffic, String slope,
                             String terrain, int length, int distance) {

        ArrayList<Hill> resultsHill = new ArrayList<Hill>();

        for (Hill hill : hills){
            String query = "";
            String hillValues = "";
            Location hillStart = new Location("");
            hillStart.setLatitude(hill.getStartCoordinates().getLatitude());
            hillStart.setLongitude(hill.getStartCoordinates().getLongitude());

            if (location.distanceTo(hillStart) <= distance*1000) {
                if (ratingEnabled) {
                    query += String.valueOf(meanRating);
                    if(hill.getmMeanRating() != 0f && hill.getmMeanRating() != 0.5f
                            && hill.getmMeanRating() != 1f && hill.getmMeanRating() != 1.5f
                            && hill.getmMeanRating() != 2f && hill.getmMeanRating() != 2.5f
                            && hill.getmMeanRating() != 3f && hill.getmMeanRating() != 3.5f
                            && hill.getmMeanRating() != 4f && hill.getmMeanRating() !=4.5f
                            && hill.getmMeanRating() != 5f) {
                        float queryMeanRating = Math.round(hill.getmMeanRating() - 0.5f) + 0.5f;
                        hillValues += String.valueOf(queryMeanRating);
                    }else{
                        float queryMeanRating = hill.getmMeanRating();
                        hillValues += String.valueOf(queryMeanRating);
                    }

                }
                if (trafficEnabled) {
                    query += String.valueOf(traffic);
                    if(hill.getmTraffic() < 5) {
                        float queryTraffic = Math.round(hill.getmTraffic() - 0.5f) + 0.5f;
                        hillValues += String.valueOf(queryTraffic);
                    }else{
                        float queryTraffic = hill.getmTraffic();
                        hillValues += String.valueOf(queryTraffic);
                    }

                }
                if (slopeEnabled) {
                    query += slope;
                    hillValues += hill.getmSlope();
                }
                if (terrainEnabled) {
                    query += terrain;
                    hillValues += hill.getmTerrain();
                }
                if (lengthEnabled) {
                    String appendLength = hill.getmDistance().substring(0, hill.getmDistance().length() - 1);
                    double hillLength = Double.valueOf(appendLength);
                    double convertHillLength =   100 * Math.ceil(hillLength / 100);
                    hillValues += convertHillLength;
                    double queryLength = (double) length;
                    query += queryLength;
                }
                if(query.equals(hillValues)){
                    resultsHill.add(hill);
                }
            }

        }
        if(resultsHill.isEmpty()){
            noResults = true;
        }else {
            hills.clear();
            hills.addAll(resultsHill);
            noResults = false;
        }
    }

    public interface SearchQueryDataBaseListener {
        public void SearchQueryDataBaseListener(ArrayList<Hill> hill);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (SearchQueryDataBaseListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement AddHillToDataBaseListener");
        }
    }

}
