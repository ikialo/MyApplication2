package com.metrolinq.isaac.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.common.collect.Maps;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.metrolinq.ScheduleActivity;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int AUTOCOMPLETE_REQUEST_CODE = 450 ;
    private GoogleMap mMap;
    DatabaseReference databaseReference, mDatabase;
    Boolean Mapclear = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ImageView line1 = findViewById(R.id.line_center_bus);

        Animation animation_line = AnimationUtils.loadAnimation(this, R.anim.anim_left2right);


        line1.setAnimation(animation_line);

        ImageView line2 = findViewById(R.id.line_center_bus2);

        Animation animation_line2 = AnimationUtils.loadAnimation(this, R.anim.anim_right2left);


        line2.setAnimation(animation_line2);

        databaseReference = FirebaseDatabase.getInstance().getReference("Car Location/LATLONG/Bus 123");
        mDatabase = FirebaseDatabase.getInstance().getReference("ClearMap");



        ImageView schedule_list = findViewById(R.id.schedule_map);

        schedule_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity( new Intent(MapsActivity.this, ScheduleActivity.class));
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


        // Add a marker in Sydney and move the camera
        LatLng port_Moresby = new LatLng(-9.4438, 147.1803);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //mMap.addMarker(new MarkerOptions().position(port_Moresby).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(port_Moresby));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(port_Moresby, 13));


        updateMapMarker();



    }


    private void updateMapMarker(){



        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               Mapclear = (Boolean) dataSnapshot.child("Map Clear").getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (!Mapclear) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mMap.clear();

                    //   for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){


                    LatLng car = new LatLng((Double) dataSnapshot.child("latitude").getValue(), (Double) dataSnapshot.child("longitude").getValue());

                    mMap.addMarker(new MarkerOptions()
                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.bus_metro))
                            .position(car)
                            .title(dataSnapshot.getKey()));
                    //  }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.backtear);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(5, 5, vectorDrawable.getIntrinsicWidth() + 25, vectorDrawable.getIntrinsicHeight() + 25);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }








}
