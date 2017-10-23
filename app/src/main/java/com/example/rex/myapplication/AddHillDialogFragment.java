package com.example.rex.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

/**
 * Created by Rex on 15/05/2017.
 */

public class AddHillDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener,
        RatingBar.OnRatingBarChangeListener{
    AddHillToDataBaseListener mCallback;
    private RatingBar addHillRating;
    private Spinner addSlopeValue;
    private TextView lengthValue;
    private Spinner addTerrainValue;
    private RatingBar addTrafficRating;
    private Button saveButton;


    private Hill hill;
    private String hillId;
    private LatLng coordinates;
    private MyLatLng startCoordinates;
    private ArrayList <LatLng> lineLocations;
    private ArrayList <MyLatLng> polyLineLatLng = new ArrayList<MyLatLng>();
    private float meanRating;
    private String slope;
    private String hillLength;
    private String terrain;
    private float traffic;
    private ArrayList<String> reviews;

    public AddHillDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static AddHillDialogFragment newInstance(LatLng coordinates, ArrayList<LatLng> lineLocations, String hillLength) {
        AddHillDialogFragment frag = new AddHillDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("Hill LatLng",coordinates);
        args.putParcelableArrayList("Hill Path", lineLocations);
        args.putString("Hill Length", hillLength);
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
        final View view = inflater.inflate(R.layout.add_hill_dialog, null);

        // Get field from view
        addHillRating = (RatingBar) view.findViewById(R.id.addHillRating);
        addSlopeValue = (Spinner) view.findViewById(R.id.addSlopeValue);
        lengthValue = (TextView) view.findViewById(R.id.addLengthValue);
        addTerrainValue = (Spinner) view.findViewById(R.id.addTerrainValue);
        addTrafficRating = (RatingBar) view.findViewById(R.id.addTrafficRating);
        saveButton = (Button) view.findViewById(R.id.addSaveButton);

        addHillRating.setOnRatingBarChangeListener(this);
        addTrafficRating.setOnRatingBarChangeListener(this);

        ArrayAdapter<CharSequence> slopeAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.slope_array,android.R.layout.simple_spinner_item);
        slopeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addSlopeValue.setAdapter(slopeAdapter);
        addSlopeValue.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> terrainAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.terrain_array,android.R.layout.simple_spinner_item);
        terrainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addTerrainValue.setAdapter(terrainAdapter);
        addTerrainValue.setOnItemSelectedListener(this);

        //retrieve values for hill object instantiation
        coordinates = getArguments().getParcelable("Hill LatLng");
        startCoordinates = new MyLatLng(coordinates.latitude,coordinates.longitude);
        lineLocations = getArguments().getParcelableArrayList("Hill Path");
        //convert Google Maps LatLng class into my own LatLng class for database reading
        //for each object of lineLocations
        for(LatLng latLng : lineLocations)
        {
            //instantiate an instance of MyLatLng and set its values to those of
            //lineLocations lat and long values
            MyLatLng convertLatLng = new MyLatLng(latLng.latitude,latLng.longitude);
            //add this object to MyLatLng ArrayList
            polyLineLatLng.add(convertLatLng);
        }
        hillId = "";
        hillLength = getArguments().getString("Hill Length");
        lengthValue.setText(hillLength);
        reviews = new ArrayList<String>();
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                hill = new Hill(hillId,startCoordinates,polyLineLatLng,meanRating,
                        slope,hillLength,terrain,traffic,reviews);
                mCallback.AddHillToDataBaseListener(hill);
                dismiss();
            }
        });
        builder.setView(view);
        return builder.create();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
        switch (parent.getId()){
            case R.id.addSlopeValue:
                slope = parent.getItemAtPosition(pos).toString();
                break;
            case R.id.addTerrainValue:
                terrain = parent.getItemAtPosition(pos).toString();
             break;
        }
    }
    public void onNothingSelected(AdapterView<?> parent){

    }

    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromTouch) {

        switch (ratingBar.getId()) {
            case R.id.addHillRating:
                final int hillNumStars = addHillRating.getNumStars();
                meanRating = addHillRating.getRating();
                final float hillRatingBarStepSize = addHillRating.getStepSize();
            case R.id.addTrafficRating:
                final int trafficNumStars = addTrafficRating.getNumStars();
                traffic = addTrafficRating.getRating();
                final float trafficRatingBarStepSize = addTrafficRating.getStepSize();
        }
    }


    public interface AddHillToDataBaseListener {
        public void AddHillToDataBaseListener(Hill hill);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (AddHillToDataBaseListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement AddHillToDataBaseListener");
        }
    }

}
