package com.metrolinq.isaac.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.metrolinq.isaac.myapplication.MapCalcClasses.DirectionFinder;
import com.metrolinq.isaac.myapplication.MapCalcClasses.DirectionFinderListener;
import com.metrolinq.isaac.myapplication.MapCalcClasses.Route;
import com.metrolinq.isaac.myapplication.MapCalcClasses.ScheduleInfo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {


    private final static int LOCATION_REQUEST = 500;

    private GoogleMap mMap;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private ProgressDialog progressDialog;
    private List<Polyline> polylinePaths = new ArrayList<>();
    private List<LatLng> latLng_search;
    private LatLng[] latLng_search_array;
    private String[] placeName = new String[2];
    private DatabaseReference mDatabase, fareDB;
    int scase = 0;

    private LatLng Base;
    private List<Double> distancesToTotal;
    private int roundedfare;
    private AutocompleteSupportFragment autocompleteFragment;
    private Button confirm, startTripInfo, cancelTrip;
    private String payment;
    LatLng port_Moresby;

    Double m, y;


    private static String TIMECONFIRM = "Confirm PickUp Time";
    private static String JOURNEYCONFIRM = "Confirm Journey";
    private static String DATECONFIRM = "Confirm Date";
    private static String PAYMENT_TYPE = "Choose Payment";
    private static String CLIENT_NAME = "Register Trip";

    private FirebaseAuth auth;
    private TextView pick, drop, time, date, paytype;

    private LinearLayout llplace, llmap;

    int day, month, year, hour, min;
    int finalday, finalmonth, finalyear, finalHour, finalMin;

    int bothMarkers = 0;
    private Date currentDate;

    private LatLng latLngToDBorigin, latLngToDBdes;

    private FusedLocationProviderClient fusedLocationClient;


    int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        currentDate = Calendar.getInstance().getTime();


        auth = FirebaseAuth.getInstance();
        auth.getCurrentUser().getPhoneNumber();
        llplace = findViewById(R.id.placesFragment);
        llmap = findViewById(R.id.llmap);

        pick = findViewById(R.id.pick);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        fareDB = FirebaseDatabase.getInstance().getReference("Fare Change");

        drop = findViewById(R.id.drop);
        time = findViewById(R.id.time);
        date = findViewById(R.id.date);
        paytype = findViewById(R.id.paytype);
        cancelTrip = findViewById(R.id.cancel_trip);


//
        String apiKey = getString(R.string.apiPlacesKey);
        mDatabase = FirebaseDatabase.getInstance().getReference("TestRequest");


        fareDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String m_string = dataSnapshot.child("m").getValue().toString();
                String y_string = dataSnapshot.child("y").getValue().toString();

                m = Double.parseDouble(m_string);
                y = Double.parseDouble(y_string);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Initialize Places.
        Places.initialize(getApplicationContext(), apiKey);

        latLng_search = new ArrayList<>();
        latLng_search_array = new LatLng[2];
        distancesToTotal = new ArrayList<>();
        Base = new LatLng(-9.447991923108408, 147.1935924142599);

        confirm = findViewById(R.id.confirm);
        startTripInfo = findViewById(R.id.confirm_start);


// Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_pick);

        autocompleteFragment.setHint("Choose Pick Up Location");


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (latLng_search_array[1] != null && latLng_search_array[0] != null) {

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng_search_array[1], 13));

                    String str_origin_two = latLng_search_array[1].latitude + "," + latLng_search_array[1].longitude;
                    String str_Base_two = latLng_search_array[0].latitude + "," + latLng_search_array[0].longitude;
                    sendRequest(str_Base_two, str_origin_two);

                    confirm.setVisibility(View.GONE);
                    startTripInfo.setVisibility(View.VISIBLE);

                    latLngToDBorigin = latLng_search_array[0];
                    latLngToDBdes = latLng_search_array[1];

                    llmap.setVisibility(View.GONE);
                    llplace.setVisibility(View.GONE);
                    cancelTrip.setVisibility(View.VISIBLE);


                }


            }
        });


        startTripInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressConfirm();
                cancelTrip.setVisibility(View.GONE);
            }
        });


        cancelTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//
//
//
//            LatLng port_Moresby = new LatLng(-9.4438, 147.1803);
//            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//
//            //mMap.addMarker(new MarkerOptions().position(port_Moresby).title("Marker in Sydney"));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(port_Moresby));
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(port_Moresby, 10));
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST);
//            return;
//        }

//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        // Got last known location. In some rare situations this can be null.
//                        if (location != null) {
//                            // Logic to handle location object
//                            port_Moresby = new LatLng(location.getLatitude(), location.getLongitude());
//
//                            mMap.setMyLocationEnabled(true);
//
//
//
//                            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//
//                            //mMap.addMarker(new MarkerOptions().position(port_Moresby).title("Marker in Sydney"));
//                            mMap.moveCamera(CameraUpdateFactory.newLatLng(port_Moresby));
//                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(port_Moresby, 14));
//
//                            //getAddressFromLocation(location.getLatitude(),location.getLongitude());
//
//                            Toast.makeText(MapsActivity2.this,  (location.getLatitude()+"," + location.getLongitude()), Toast.LENGTH_SHORT).show();
//
//
//                        }
//                    }
//                });
//

        port_Moresby = new LatLng(-9.447991923108408, 147.1935924142599);

        // mMap.setMyLocationEnabled(true);


        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //mMap.addMarker(new MarkerOptions().position(port_Moresby).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(port_Moresby));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(port_Moresby, 12));


        pickDestination();
        dropDestination();

        // location_select();

//        if (pickDestination() != null && dropDestination() != null){
//        sendRequest(pickDestination().latitude +","+pickDestination().longitude,
//                dropDestination().latitude +","+dropDestination().longitude);
//            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_REQUEST) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    port_Moresby = new LatLng(location.getLatitude(), location.getLongitude());
                                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        return;
                                    }
                                    mMap.setMyLocationEnabled(true);


                                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

                                    //mMap.addMarker(new MarkerOptions().position(port_Moresby).title("Marker in Sydney"));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(port_Moresby));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(port_Moresby, 13));


                                    Toast.makeText(MapsActivity2.this, (location.getLatitude() + "," + location.getLongitude()), Toast.LENGTH_SHORT).show();


                                }
                            }
                        });

             //   mMap.setMyLocationEnabled(true);
            } else {
                // Permission was denied. Display an error message.
                Toast.makeText(this, "Permission to Use Location was Denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        pickDestination();
        dropDestination();
    }
    @SuppressLint("ResourceAsColor")
    public void pickDestination(){
        // Initialize the AutocompleteSupportFragment.




        autocompleteFragment.setCountry("PG");







// Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

// Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("PLACE", "Place: " + place.getName() + ", " + place.getId());

                latLng_search.add(place.getLatLng());
                latLng_search_array [0] = place.getLatLng();
                placeName [0] = place.getName();


                String str_origin = latLng_search_array[0].latitude+ ","+ latLng_search_array[0].longitude;
                String str_Base = Base.latitude + "," + Base.longitude;


                LatLng loc = new LatLng(latLng_search_array[0].latitude, latLng_search_array[0].longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 14));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                mMap.addMarker(new MarkerOptions().position(loc).title(place.getName()));

                //   isOriginCalculated = true;

                sendRequest(str_Base,str_origin);
                autocompleteFragment.setUserVisibleHint(false);









            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("PLACE", "An error occurred: " + status);
            }
        });




    }
    public void dropDestination(){
        // Initialize the AutocompleteSupportFragment.


        final LatLng[] latLngDropOff = new LatLng[1];
        final AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_drop);

        autocompleteFragment.setCountry("PG");
        autocompleteFragment.setHint("Choose Drop Off Location");



// Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

// Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("PLACE", "Place: " + place.getName() + ", " + place.getId());


                latLng_search_array [1] = place.getLatLng();
                if (latLng_search.size() == 1){
                    latLng_search.add(place.getLatLng());
                }

                String str_origin = latLng_search_array[1].latitude+ ","+ latLng_search_array[1].longitude;
                String str_Base = Base.latitude + "," + Base.longitude;

                placeName [1] = place.getName();

                LatLng loc = new LatLng(latLng_search_array[1].latitude, latLng_search_array[1].longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 14));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                mMap.addMarker(new MarkerOptions().position(loc).title(place.getName()));



                sendRequest(str_Base,str_origin);

                autocompleteFragment.setUserVisibleHint(false);

//                if (latLng_search_array[0] != null){
//
//                    String str_origin_two = latLng_search_array[1].latitude+ ","+ latLng_search_array[1].longitude;
//                    String str_Base_two = latLng_search_array[0].latitude+ ","+ latLng_search_array[0].longitude;
//                    sendRequest(str_Base_two,str_origin_two);
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 12));
//
//
//                }

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("PLACE", "An error occurred: " + status);
            }
        });





    }

    private void sendRequest(String ori, String des) {
        String origin = ori;
        String destination = des;

        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();
        double priceRide = 0.0;
        double roundTripDistance = 0;




        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 13));



//            priceRide = round(route.distance.value *m/1000.0 + y,2);
//

            distancesToTotal.add((double) route.distance.value);

            //


            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.RED).
                    width(7);

            for (int i = 0; i < route.points.size(); i++) {
                polylineOptions.add(route.points.get(i));
            }


          // polylinePaths.add(mMap.addPolyline(polylineOptions));
        }


      //  Toast.makeText(this,  distancesToTotal.get(0).toString(), Toast.LENGTH_SHORT).show();

//        if (isTripReady){
//
//            String str_dest = testLatlngDes.latitude + "," + testLatlngDes.longitude;
//            String str_Base = Base.latitude + "," + Base.longitude;
//            sendRequest(str_dest, str_Base);
//            listpoints.clear();
//            isTripReady = false;
//
//        }
        if (distancesToTotal.size() == 3){
            roundTripDistance = distancesToTotal.get(0)+ distancesToTotal.get(1)+ distancesToTotal.get(2);
            priceRide = 2.0*roundTripDistance *m*y/100000.0;

            Log.d("ROUNDTRIP", "onDirectionFinderSuccess: "+roundTripDistance);

            roundedfare = roundup(priceRide);
            ((TextView) findViewById(R.id.price)).setText("K "+Double.toString(roundedfare));

            distancesToTotal.clear();
           // mMap.clear();
            latLng_search_array [0] = null;
            latLng_search_array [1] = null;

        }

    }

    public static int roundup(double value){

        int rounded = 0;
        int divided;
        if (value % 5 != 0){
            divided = (int) (value/5);
            rounded = divided * 5 + 5;
        }else {
            rounded = (int) value;
        }

        return rounded;
    }


    public void pressConfirm(){



        switch (scase){

            case 0:
                //clearMap.setVisibility(View.GONE);
                findViewById(R.id.map).setVisibility(View.INVISIBLE);
                Calendar c = Calendar.getInstance();

                hour = c.get(Calendar.HOUR);
                min = c.get(Calendar.MINUTE);


                TimePickerDialog timePickerDialog = new TimePickerDialog(MapsActivity2.this, MapsActivity2.this,hour, min, false);
                timePickerDialog.show();

                timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            // Do Stuff
                            finish();

                        }
                    }
                });

                startTripInfo.setText(DATECONFIRM);


                LatLng sydney = new LatLng(-9.4438, 147.1803);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

                scase =1;
                break;

            case 1:
                Calendar calendar = Calendar.getInstance();

                year =calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);




                DatePickerDialog datePickerDialog = new DatePickerDialog(MapsActivity2.this, MapsActivity2.this,year,month, day);

                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            // Do Stuff
                            finish();

                        }
                    }
                });
                datePickerDialog.show();



                startTripInfo.setText(CLIENT_NAME);
                pick.setText("From " +placeName[0] +" to " +placeName[1]);
                drop.setText(" ");
                time.setText( "Time: "+finalHour +":"+ finalMin);
                pick.setVisibility(View.VISIBLE);
                drop.setVisibility(View.VISIBLE);
                time.setVisibility(View.VISIBLE);


               // listpoints.clear();
                mMap.clear();

                scase = 2;
                break;

            case 2:
                DatabaseReference clientDB;

                clientDB = FirebaseDatabase.getInstance().getReference("RegisteredClients");


                final String m_Text =    auth.getCurrentUser().getPhoneNumber();
                clientDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {



                            if(m_Text.equals(postSnapshot.child("phNum").getValue().toString())) {


                                payment = "CASH";

                                ScheduleInfo scheduleInfo = new ScheduleInfo(finalHour,finalMin
                                        , latLngToDBorigin.latitude,latLngToDBorigin.longitude,
                                        latLngToDBdes.latitude,latLngToDBdes.longitude, roundedfare, postSnapshot.child("fullName").getValue().toString(),
                                        finalyear,
                                        finalmonth ,
                                        finalday, currentDate, payment, "no" , placeName[0], placeName[1], m_Text);

                                String uploadId = mDatabase.push().getKey();
                                findViewById(R.id.map).setVisibility(View.VISIBLE);
                                confirm.setVisibility(View.GONE);

                                mDatabase.child(uploadId).setValue(scheduleInfo);


                                break;
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                // ...



                // clearMap.setVisibility(View.VISIBLE);

//                Intent intent = new Intent(MapsActivity2.this, ClinetNameActivity.class);
//                startActivity(intent);
                finish();


                break;






        }

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        finalHour = hourOfDay;
        finalMin = minute;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        finalyear = year;
        finalmonth = month +1;
        finalday = dayOfMonth;

        date.setText("Date: "+finalday+ "/"+ finalmonth+"/" +finalyear);

        date.setVisibility(View.VISIBLE);

    }

    private void getAddressFromLocation(double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);


        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0) {
                Address fetchedAddress = addresses.get(0);
                StringBuilder strAddress = new StringBuilder();
                for (int i = 0; i < fetchedAddress.getMaxAddressLineIndex(); i++) {
                    strAddress.append(fetchedAddress.getAddressLine(i)).append(" ");
                }

                //txtLocationAddress.setText(strAddress.toString());
                ((TextView) findViewById(R.id.price)).setText( strAddress);




            } else {
               // txtLocationAddress.setText("Searching Current Address");
                ((TextView) findViewById(R.id.price)).setText( "nothin");
            }

        } catch (IOException e) {
            e.printStackTrace();

            ((TextView) findViewById(R.id.price)).setText( "not working" +
                    "");
            Toast.makeText(this, "not working", Toast.LENGTH_SHORT).show();


        }
    }

}
