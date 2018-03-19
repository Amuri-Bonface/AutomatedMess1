package com.project.ams.automatedmess;

import android.*;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.Manifest;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks {


    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION=101;
    private static final int MY_PERMISSION_REQUEST_COARSE_LOCATION=102;
    private boolean permissionIsGranted=false;

    private DatabaseReference mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        checkLocationPermission();
        //dref = FirebaseDatabase.getInstance().getReference().child("Users").child("MessProviders").child("ESX23mmeeNh5n4sZe0YXQlZOrTE2").child("ProfileInformation");
        //mClass = new MessProfileEditor();
        ChildEventListener mChildEventListener;
    }





            @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        buildGoogleApiClient();



    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.addMarker(new MarkerOptions().position(latLng).title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

        mDb = FirebaseDatabase.getInstance().getReference("Users").child("MessProviders").child("ESX23mmeeNh5n4sZe0YXQlZOrTE2").child("ProfileInformation").child("mProviderLocation");
        mDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot s: dataSnapshot.getChildren()){
                    MessProfileEditor mess = s.getValue(MessProfileEditor.class);
                    LatLng loc =new LatLng(mess.messLatitude, mess.messLongitude);
                    //LatLng loc = new LatLng(
                    //       dataSnapshot.child("mProviderLatitude").getValue(Long.class),
                    //      dataSnapshot.child("mProviderLongitude").getValue(Long.class));

                    mMap.addMarker(new MarkerOptions().position(loc).title("My Mess"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

       /* mDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                LatLng loc = new LatLng(
                        dataSnapshot.child("mProviderLatitude").getValue(Long.class),
                        dataSnapshot.child("mProviderLongitude").getValue(Long.class));

                mMap.addMarker(new MarkerOptions().position(loc).title("My").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));


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
    }

    /*public void messLocation() {
        DatabaseReference messProviders = FirebaseDatabase.getInstance().getReference("Users").child("MessProviders").child("ESX23mmeeNh5n4sZe0YXQlZOrTE2").child("ProfileInformation").child("mProviderLocation");

        messProviders.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Create an array of markers
                int size = (int) dataSnapshot.getChildrenCount(); //
                Marker[] allMarkers = new Marker[size];
                mMap.clear();   //Assuming you're using mMap
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    //Specify your model class here
                    MessProfileEditor modelObject=new MessProfileEditor();
                    //lets create a loop
                    for(int i=0;i<=size;i++) {
                        try {
                            //assuming you've set your getters and setters in the Model class
                            /*modelObject.setMproviderBrandName(ds.getValue(MessProfileEditor.class).getMproviderBrandName());
                            modelObject.setMproviderMobileNo(ds.getValue(MessProfileEditor.class).getMproviderMobileNo());

                            modelObject.setMproviderLatitude(ds.getValue(MessProfileEditor.class).getMproviderLatitude());
                            modelObject.setMproviderLongitude(ds.getValue(MessProfileEditor.class).getMproviderLongitude());

                            //lets retrieve the coordinates and other information
                           /* String brandName=modelObject.getMproviderBrandName();
                            String mobileNo=modelObject.getMproviderMobileNo();

                            Double latitude = Double.parseDouble(String.valueOf(modelObject.getMproviderLatitude()));
                            Double longitude = Double.parseDouble(String.valueOf(modelObject.getMproviderLongitude()));

                            //convert the coordinates to LatLng
                            LatLng latLng = new LatLng(latitude,longitude);
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                            //Now lets add updated markers
                            //create an Icon and store it in the drawable resource
                            allMarkers[i] = mMap.addMarker(new MarkerOptions()
                                    .position(latLng).title("hello"));
                        }catch (Exception ex){}
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

*/




        /*

        /*mMap.addMarker(new MarkerOptions().position(new LatLng(18.551146, 73.937660)).title("Reliance Mess"));//.snippet(String.valueOf()));
        mMap.addMarker(new MarkerOptions().position(new LatLng(18.547118, 73.940439)).title("Zensar Mess"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(18.552041, 73.950996)).title("Eon Mess"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(18.552190, 73.961640)).title("Panchashil Mess"));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            Intent intent1;
            Intent intent2;
            Intent intent3;
            Intent intent4;

            @Override
            public boolean onMarkerClick(Marker arg0) {
                if(arg0.getTitle().equals("Reliance Mess")) {
                    intent1 = new Intent(MapsActivity.this, Popup.class);
                    startActivity(intent1);
                    return true;
                }
                else{
                    if(arg0.getTitle().equals("Zensar Mess")) {
                        intent2 = new Intent(MapsActivity.this, Zensar.class);
                        startActivity(intent2);
                        return true;
                    }
                    else {
                        if(arg0.getTitle().equals("Eon Mess")) {
                        intent3 = new Intent(MapsActivity.this, Eon.class);
                        startActivity(intent3);
                        return true;
                    }
                    else {
                            if(arg0.getTitle().equals("Panchashil Mess")) {
                                intent4 = new Intent(MapsActivity.this, Panchashil.class);
                                startActivity(intent4);
                                return true;
                            }
                            else {
                                return false;
                            }

                    }

                    }

                }






            }

        });

        /*mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int position = (int)(marker.getTag());
                startActivity(new Intent(MapsActivity.this, Popup.class));
                return false;
            }
        });*/


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        //mLocationRequest.setInterval(1000);
        //mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
            }
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSION_REQUEST_FINE_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_REQUEST_FINE_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {


                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

}


