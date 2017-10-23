package com.example.rex.myapplication;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//maps api imports
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//Google Maps Android API utility imports
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;


//nav menu imports
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Menu;
import android.view.MenuInflater;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        NavigationView.OnNavigationItemSelectedListener,
        AddHillDialogFragment.AddHillToDataBaseListener,
        AddReviewDialogFragment.AddReviewToDataBaseListener,
        SearchHillDialogFragment.SearchQueryDataBaseListener,
        SearchResultsFragment.ShowSelectedSearchResultListener{

    // Min and Max Update Intervals for our Location Service
    private static final long MAX_UPDATE_INTERVAL = 10000; // 10 Seconds
    private static final long MIN_UPDATE_INTERVAL = 2000; // 2 Seconds


    // Global Variables for Google Maps
    private GoogleMap m_cGoogleMap;
    private Location m_cCurrentLocation;
    private GoogleApiClient m_cAPIClient;
    //boolean value determining whether or not fine location permission granted
    private boolean m_cLocationPermissionGranted;
    //boolean returns true on app launch
    private boolean startApp = true;
    private de.hdodenhof.circleimageview.CircleImageView locButton;
    private FloatingActionButton fab;



    //Firebase database Global variables
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mHillConditionRef = mRootRef.child("Hills");
    DatabaseReference mReviewConditionRef = mRootRef.child("Reviews");


    //request code for permission
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;


    //add hill global variables
    private de.hdodenhof.circleimageview.CircleImageView toggleStateMap;
    private de.hdodenhof.circleimageview.CircleImageView toggleStateDraw;
    private de.hdodenhof.circleimageview.CircleImageView resetAddButton;
    //detect if adding a hill. Changes optionsmenu items, disables onMarkerClick while true
    private boolean addingHill = false;

    //framelayout onto which lines can be drawn
    private RelativeLayout fram_map;
    //checks whether marker is added to set onMapClick method prevents multiple marker adds
    private boolean isMarkerAdded = false;
    //detect if alertDialog for drawing over 1km has been displayed
    private boolean alerted = false;
    private Marker marker;
    //arraylist of polyline locations used to create polyline
    private ArrayList<LatLng> lineLocations = new ArrayList<>();
    //polyline that the user draws onto map
    private Polyline polyline;
    //used to calculate distance of polyline in meters
    private double polylineDistance;

    private boolean displayingSearch = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer);

        //retrieve appCompat Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //set the padding to match status bar height
        //my device did not need this to correct padding between the app bar and the status bar
        //toolbar.setPadding(0,getStatusBarHeight(),0,0);


        //retrieve fab
        fab = (FloatingActionButton) findViewById(R.id.fabAddHill);
        //set fab on click listener here
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateHill();
                fab.setEnabled(false);
                fab.setVisibility(View.INVISIBLE);
            }
        });
        toggleStateMap = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.toggleStateMap);
        toggleStateDraw = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.toggleStateDraw);
        resetAddButton = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.resetAddButton);
        //retrieves the drawer from nav_drawer.xml
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //toggles the open and closed states of the drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //depreciated method, working on older devices to set drawerlistener to toggle
        drawer.setDrawerListener(toggle);
        //syncs the state of drawer to toggle
        toggle.syncState();
        //this retrieves the navigation view of the drawer, including the header and menu options
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Check to see if our APIClient is null.
        if(m_cAPIClient == null) {
            // Create our API Client and tell it to connect to Location Services
            m_cAPIClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        // Get access to our MapFragment
        MapFragment mapFrag = (MapFragment)
               getFragmentManager().findFragmentById(R.id.map_fragment);
        // Set up an asynchronous callback to let us know when the map has loaded
        mapFrag.getMapAsync(this);

        locButton = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.updateButton);
        locButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeMapLocation();
            }
        });
    }

    //method to get status bar height in order to make it transparent
    /*public int getStatusBarHeight(){
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0){
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }*/

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public void onBackPressed() {
        //handles a BACK button press while in nav drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        menu.setGroupVisible(R.id.add_hill_menu,false);
        menu.setGroupEnabled(R.id.add_hill_menu,false);
        if(addingHill == true){
            menu.setGroupVisible(R.id.add_hill_menu,true);
            menu.setGroupEnabled(R.id.add_hill_menu,true);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                final ArrayList<Hill> queryHills = new ArrayList<Hill>();
                mHillConditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dsp : dataSnapshot.getChildren()){
                            Hill queryHill = dsp.getValue(Hill.class);
                            queryHills.add(queryHill);
                        }
                        searchHillDialog(m_cCurrentLocation,queryHills);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                return true;
            case R.id.action_about:
                Intent intent = new Intent(this,AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_save:
                if(marker != null && !lineLocations.isEmpty()) {
                    fram_map.setEnabled(false);
                    LatLng newCoordinates = new LatLng(marker.getPosition().latitude,
                            marker.getPosition().longitude);
                    showAddHillDialog(newCoordinates, lineLocations, (float) Math.round(polylineDistance * 100) / 100 + "m");
                    //write code to open dialog box for entering hill info
                    //code to stop drawing line and save the marker and polyline arraylist to hill object
                }else if (marker == null || lineLocations.isEmpty()){
                    //dialog opens to notify user that not enough data was added for a hill
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setMessage("Please add a marker to the map" +
                            " and draw the hill path");
                    alertDialogBuilder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    //on click of yes activity will be recreated
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                return true;
            case R.id.action_cancel:
                //dialog opens to notify before cancelling add hill
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Add a Hill progress will not be saved" +
                        " on exit. Close?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                //on click of yes activity will be recreated
                                recreate();
                            }
                        });
                alertDialogBuilder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //no will close dialog and user can continue adding hill
                                Toast.makeText(MainActivity.this,"Resume adding hill",
                                        Toast.LENGTH_LONG).show();
                            }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //get the id value of the textview
        if (id == R.id.favourite){
            Toast.makeText(this, "Favourites Screen",
                    Toast.LENGTH_SHORT).show();
            return true;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Android 6.0 & up added security permissions
        // If the user rejects allowing access to location data then this try block
        // will stop the application from crashing (Will not track location)
        try {
            // Set up a constant updater for location services
            LocationRequest locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                    .setInterval(MAX_UPDATE_INTERVAL)
                    .setFastestInterval(MIN_UPDATE_INTERVAL);
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(m_cAPIClient, locationRequest, this);
            //set m_cCurrentLocation to the last, most recent location
            //m_cCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(m_cAPIClient);
            //run changeMapLocation using m_cCurrentLocation value
            ChangeMapLocation();
        }
        catch (SecurityException secEx) {
            Toast.makeText(this, "ERROR: Please enable location services",
                    Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        // Do nothing for now. This function is called should the connection halt
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Do nothing for now. This function is called if the connection fails initially
    }

    @Override
    protected void onStart() {
        m_cAPIClient.connect();
        super.onStart();
    }
    @Override
    protected void onStop() {
        m_cAPIClient.disconnect();
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {
        // This is our function that is called whenever we change locations
        // Update our current location variable
        m_cCurrentLocation = location;
        if(startApp == true){
            ChangeMapLocation();
            startApp  = false;
        }
    }

    private void ChangeMapLocation() {
        // fine location permission of the app is checked against current context
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                //if fine location permission is already granted then set permission granted to true
                m_cLocationPermissionGranted = true;
            //else fine location permission is not granted so request it
        } else {
            //request permission from this activity, a string array containing the permissions to request and a result code
            //result of the permission request is handled by the onRequestPermissionsResult method
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        if (m_cLocationPermissionGranted) {
            m_cGoogleMap.setMyLocationEnabled(true);
            // Check to ensure map and location are not null
            if (m_cCurrentLocation != null && m_cGoogleMap != null) {
                // Create a new LatLng based on our new location
                LatLng newPos = new LatLng(m_cCurrentLocation.getLatitude(),
                        m_cCurrentLocation.getLongitude());
                // Change the map focus to be our new location
                m_cGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPos, 15));
            }
        }else {
            m_cGoogleMap.setMyLocationEnabled(false);
            m_cCurrentLocation = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        //permission defaults to not granted
        m_cLocationPermissionGranted = false;
        //switch case used in case more permissions needed in future
        switch (requestCode) {
            //if requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    m_cLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
       // Function is called once the map has fully loaded.
       // Set our map object to reference the loaded map
       m_cGoogleMap = googleMap;
        // Call our Add Default Markers function
        // NOTE: In a proper application it may be better to load these from a DB
       DisplayHillsFromDatabase();
       m_cGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
           @Override
           public void onMapClick(LatLng latLng) {
               if(displayingSearch){
                   getSupportFragmentManager().beginTransaction()
                           .remove(getSupportFragmentManager().findFragmentById(R.id.searchHillResult))
                           .commit();
                   displayingSearch = false;
               }
           }
       });
       m_cGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (addingHill == false) {
                    final Hill hill = (Hill) marker.getTag();
                    final String hillId = hill.getHillId();
                    LatLng hillStart = new LatLng(hill.getStartCoordinates().getLatitude(),
                            hill.getStartCoordinates().getLongitude());
                    m_cGoogleMap.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(hillStart, 15));
                    //addsingleeventlistener
                    mReviewConditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        ArrayList<Review> mReviews = new ArrayList<Review>();

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(hillId)) {
                                mReviewConditionRef.orderByKey().equalTo(hillId).addChildEventListener(new ChildEventListener() {
                                    //onChildAdded method returns results of a query
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        for (DataSnapshot dsp : dataSnapshot.child("Reviews").getChildren()) {
                                            //set a review object to equal value obtained
                                            Review review = dsp.getValue(Review.class);
                                            //add review object to arraylist of reviews
                                            mReviews.add(review);
                                        }
                                        showViewHillDialog(hill, mReviews);
                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }

                                });
                            } else {
                                showViewHillDialog(hill, mReviews);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    // Move the focus of the map to be on the marker. 15 is for zoom

                }

                return true;
            }
        });
    }

    private void CreateHill(){
        //adding hill true changes options menu
        addingHill = true;
        fram_map = (RelativeLayout) findViewById(R.id.fram_map);
        final Hill hill = new Hill();
        //refreshes options menu to display new options
        invalidateOptionsMenu();

        //set icon for toggleState to map button
        toggleStateMap.setImageResource(R.drawable.ic_map_black_24dp);
        //remove locButton Icon
        locButton.setImageResource(0);
        //set resetAddButton icon
        resetAddButton.setImageResource(R.drawable.ic_undo_black_24dp);

        //onclick of toggleStateMap button
        toggleStateMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //disable fram_map to allow map to be moved
                fram_map.setEnabled(false);
                //reset locButton icon such that it is visible
                locButton.setImageResource(R.drawable.crosshairs);
                //set the icon of the toggleStateDraw Button
                toggleStateDraw.setImageResource(R.drawable.ic_edit_location_black_24dp);
                //remove the icon of the toggleStateMap button so it does not overlay toggleStateDraw button
                toggleStateMap.setImageResource(0);
                //remove the icon of the resetAddButton so it does not overlay the locButton
                resetAddButton.setImageResource(0);
            }

        });
        //onClick of toggleStateDraw button
        toggleStateDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if not adding hill then do nothing
                if(addingHill == true) {
                    //enable fram_map so drawing can be done
                    fram_map.setEnabled(true);
                    resetAddButton.setImageResource(R.drawable.ic_undo_black_24dp);
                    //reset toggleStateMap icon such that it is visible
                    toggleStateMap.setImageResource(R.drawable.ic_map_black_24dp);
                    //remove the icon of the toggleStateDraw button so it does not overlay toggleStateMwp button
                    toggleStateDraw.setImageResource(0);
                    //remove locButton icon
                    locButton.setImageResource(0);
                }
            }
        });
        resetAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (marker != null) {
                    marker.remove();
                    marker = null;
                    isMarkerAdded = false;
                    if (polyline != null && !lineLocations.isEmpty()) {
                        polyline.remove();
                        polyline = null;
                        lineLocations.clear();
                    }
                }
            }
        });

        m_cGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if (isMarkerAdded == false) {
                    if(displayingSearch){
                        getSupportFragmentManager().beginTransaction()
                                .remove(getSupportFragmentManager().findFragmentById(R.id.searchHillResult))
                                .commit();
                        displayingSearch = false;
                    }
                    //initialise marker parameters
                    MarkerOptions markerOptions = new MarkerOptions();
                    //add the lat and long of touch as marker position
                    markerOptions.position(new LatLng(point.latitude, point.longitude));
                    //add the marker
                    marker = m_cGoogleMap.addMarker(markerOptions);
                    //change map moveable boolean to true, such that drawing can begin
                }
                isMarkerAdded = true;
            }
        });
        //set an onTouchListener for framelayout overlay
        fram_map.setOnTouchListener(new View.OnTouchListener() {
                @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isMarkerAdded == true) {
                // get masked (not specific to a pointer) action
                int maskedAction = MotionEventCompat.getActionMasked(event);
                int pointerIndex = event.getActionIndex();

                float x = event.getX(pointerIndex);
                float y = event.getY(pointerIndex);

                int x_co = Math.round(x);
                int y_co = Math.round(y);

                Point x_y_points = new Point(x_co, y_co);
                LatLng latLng = m_cGoogleMap.getProjection().fromScreenLocation(x_y_points);

                switch (maskedAction) {
                    case MotionEvent.ACTION_DOWN:
                        // finger touches the screen
                       if(lineLocations.isEmpty()) {
                           lineLocations.add(marker.getPosition());
                       }
                       if (SphericalUtil.computeDistanceBetween(marker.getPosition(),latLng)< 1000) {
                            lineLocations.add(new LatLng(latLng.latitude, latLng.longitude));
                            Log.i("test", event.toString());
                       }
                    case MotionEvent.ACTION_MOVE:
                        // finger moves on the screen;
                        if(lineLocations.isEmpty()) {
                            lineLocations.add(marker.getPosition());
                        }
                        else{
                            lineLocations.add(new LatLng(latLng.latitude, latLng.longitude));
                        }
                        Log.i("test", event.toString());
                    case MotionEvent.ACTION_UP:
                        // finger leaves the screen
                        if(polyline == null){
                            PolylineOptions polylineOptions = new PolylineOptions();
                            polylineOptions.addAll(lineLocations);
                            polylineOptions.color(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
                            polylineOptions.geodesic(true);
                            polyline = m_cGoogleMap.addPolyline(polylineOptions);
                        }else {
                            polyline.setPoints(lineLocations);
                        }
                        polylineDistance = SphericalUtil.computeLength(lineLocations);
                        if(polylineDistance > 1000) {
                            polyline.remove();
                            polyline = null;
                            if(alerted == false) {
                                alerted = true;
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                alertDialogBuilder.setMessage("Maximum Hill distance extended." +
                                        " Cannot end a Hill more than 1km from its start.");
                                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        lineLocations.clear();
                                        alerted = false;
                                    }
                                });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                        }
                        Log.i("test", event.toString());
                        break;
                    }
                    return true;
                }else {
                    return false;
                }
            }
        });
    }
    //instance of AddHillDialogFragment interface used to receive data
    public void AddHillToDataBaseListener(Hill newHill) {
        final Hill addHill = newHill;
        mHillConditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            //method that handles reading data
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //for each child of mHillConditionRef
                String hillId = mHillConditionRef.push().getKey();
                addHill.setHillId(hillId);
                //push hill object to firebase as a new instance of Hills
                mHillConditionRef.child(hillId).setValue(addHill);
                //refresh activity to display hill on map
                recreate();
            }
            //if data retrieval fails
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error retrieving data",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    //instance of AddReviewDialogFragment interface used to receive data
    public void AddReviewToDataBaseListener(Hill reviewedHill, final Review newReview){
        final Hill updateHill = reviewedHill;
        final Review addReview = newReview;
        final String hillId =  addReview.getHillId();
        //push a new Reviews Key
        final String reviewId = mReviewConditionRef.push().getKey();
        mReviewConditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            //method that handles reading data
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //set value of review object at reviewId to the newly obtained review object
                mReviewConditionRef.child(hillId).child("Reviews").child(reviewId).setValue(addReview);
                long reviewCount = dataSnapshot.child(hillId).child("Reviews").getChildrenCount();
                reviewCount ++;
                if (reviewCount == 1) {
                    float meanReview = (updateHill.getmMeanRating() + newReview.getmRating()) / (reviewCount + 1);
                    updateHill.setmMeanRating(meanReview);
                    float meanTraffic = (updateHill.getmTraffic() + newReview.getmTraffic()) / (reviewCount +1);
                    updateHill.setmTraffic(meanTraffic);
                } else {
                    //rolling (cumulative) average formula is used to calculate a series of averages
                    float meanReview = ((updateHill.getmMeanRating() * reviewCount) + newReview.getmRating()) / (reviewCount + 1);
                    updateHill.setmMeanRating(meanReview);
                    float meanTraffic = ((updateHill.getmTraffic() * reviewCount) + newReview.getmTraffic()) / (reviewCount + 1);
                    updateHill.setmTraffic(meanTraffic);
                }
            }
            //if data retrieval fails
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error retrieving data",
                        Toast.LENGTH_SHORT).show();
            }
        });
        //update Hill object to include newly created reviewId in list of review ids
        //query Hills where "hillId" == hillId
        //note: the hillId value is obtained when a user clicks on a hill object to view its info.
        //the hill object is collected and passed to the addReviewDialogFragment. here it is added to the
        //new review object.
        mHillConditionRef.orderByChild("hillId").equalTo(hillId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> ratingUpdate = new HashMap<String, Object>();
                //the Hill child field "mMeanRating" will be updated with new mean rating value
                ratingUpdate.put("mMeanRating",updateHill.getmMeanRating());

                Map<String, Object> trafficUpdate = new HashMap<String, Object>();
                //the Hill child field "mTraffic" will be updated with new mean traffic value
                trafficUpdate.put("mTraffic", updateHill.getmTraffic());

                //update child at hillId with reviewUpdate hashmap
                mHillConditionRef.child(hillId).updateChildren(ratingUpdate);
                //update child at hillId with trafficUpdate hashmap
                mHillConditionRef.child(hillId).updateChildren(trafficUpdate);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error retrieving data",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void SearchQueryDataBaseListener(ArrayList<Hill> hill) {
        Bundle args = new Bundle();
        args.putParcelableArrayList("Hill", hill);
        SearchResultsFragment frag = new SearchResultsFragment();
        frag.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.searchHillResult,frag)
                .commit();
        displayingSearch = true;
    }

    public void ShowSelectedSearchResultListener(Hill hill){
        LatLng hillStart = new LatLng(hill.getStartCoordinates().getLatitude(),
                hill.getStartCoordinates().getLongitude());
        m_cGoogleMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(hillStart, 15));

    }


    //method called to retrieve and display hills from firebase
    public void DisplayHillsFromDatabase(){
        //reads data once from database
        mHillConditionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            //method that handles reading data
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //for each child of mHillConditionRef
                for (DataSnapshot dsp : dataSnapshot.getChildren()){
                    //retrieve hill object
                    Hill newHill = dsp.getValue(Hill.class);
                    MyLatLng coordinates = newHill.getStartCoordinates();
                    LatLng convertCoordinates = new LatLng(coordinates.getLatitude(),coordinates.getLongitude());
                    //google maps LatLng does not have an empty constructor and cant be retrieved
                    //in order to work around this, I created my own latlng class
                    ArrayList <MyLatLng> hillPath = newHill.getmHillPath();
                    ArrayList <LatLng> polylinePath = new ArrayList<LatLng>();
                    //convert each object in MyLatLng hillPath ArrayList to Google Maps LatLng
                    //ArrayList polylinePath
                    for(MyLatLng latLng : hillPath)
                    {
                        //create a LatLng object and assign its values
                        // to those from hillPath
                       LatLng mapsLatLng = new LatLng(latLng.getLatitude(), latLng.getLongitude());
                        //add new object to polylinePath ArrayList
                        polylinePath.add(mapsLatLng);
                    }
                    //add marker to map
                    m_cGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(convertCoordinates.latitude,
                                    convertCoordinates.longitude))).setTag(newHill);
                    //add polyline to map based on converted ArrayList
                    m_cGoogleMap.addPolyline(new PolylineOptions().geodesic(true)
                            .addAll(polylinePath));
                }
            }
            //if data retrieval fails
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error retrieving data",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void searchHillDialog(Location location, ArrayList<Hill> queryHills){
        DialogFragment dialog = SearchHillDialogFragment.newInstance(location, queryHills);
        dialog.show(getSupportFragmentManager(),"search_hill_frag");
    }

    public void showViewHillDialog(Hill hill, ArrayList<Review> reviews) {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = ViewHillDialogFragment.newInstance(hill, reviews);
        dialog.show(getSupportFragmentManager(),"view_hill_frag");
    }

    public void showAddHillDialog(LatLng coordinates, ArrayList<LatLng> lineLocations, String hillPath){
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = AddHillDialogFragment.newInstance(coordinates,lineLocations,hillPath);
        dialog.show(getSupportFragmentManager(),"add_hill_frag");
    }

}
