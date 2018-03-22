package com.project.ams.automatedmess;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessProfileEditor extends AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback{

    private static final String TAG = "MessProfileEditor";

    // Declare a few fields
    private TextView emailValueView;
    public TextView editProfileMessName;
    private TextView editProfileMobileNo;
    private TextView editProfileTelephoneNo;
    private Button editProfileSaveBtn;

    // Google Map Stuff
    private GoogleMap mMap;

    // Declare a few firebase stuff
    private FirebaseAuth mAuth;

    // Used for storing the latitude and longitude that we get from the user's current location
    public double messLatitude = 0.0;
    public double messLongitude = 0.0;

    // Store a reference to the userID
    private String userID;

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    //introduce an array of markers
    private ArrayList<LatLng> MarkerPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_profile_editor);

        // Initializing array of markers
        MarkerPoints = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Start Coding from here...


        // Create an onclick Listener for the profile image in the navigation drawer
        ImageView profileImg = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.messOwnerProfileImg);
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onProfileImg: Profile Image Clicked");
            }
        });

        // Init Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Set the connections
        emailValueView = navigationView.getHeaderView(0).findViewById(R.id.messOwnerEmail);
        editProfileMessName = findViewById(R.id.editProfile_messName);
        editProfileMobileNo = findViewById(R.id.editProfile_mobileNo);
        editProfileTelephoneNo = findViewById(R.id.editProfile_telephoneNo);
        editProfileSaveBtn = findViewById(R.id.editProfile_saveBtn);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String emailValue = mAuth.getCurrentUser().getEmail().toString();

        // Get the email of the currently signed in user and set it to the email textview
        emailValueView.setText(emailValue);

        // Get the userID for the currently signed in mess provider
        userID = mAuth.getCurrentUser().getUid();
        final DatabaseReference currentDBRef = FirebaseDatabase.getInstance().getReference().child("Users").child("MessProviders").child(userID).child("ProfileInformation");
        currentDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String messName = snapshot.child("mProviderBrandName").getValue().toString();
                    messLatitude = Double.parseDouble(snapshot.child("mProviderLocation").child("mProviderLatitude").getValue().toString());
                    messLongitude = Double.parseDouble(snapshot.child("mProviderLocation").child("mProviderLongitude").getValue().toString());
                    String messMobileNo = snapshot.child("mProviderMobileNo").getValue().toString();
                    String messTelephoneNo = snapshot.child("mProviderTelephoneNo").getValue().toString();

                    // Put these values in the activity fields
                    editProfileMessName.setText(messName);

                    editProfileMobileNo.setText(messMobileNo);
                    editProfileTelephoneNo.setText(messTelephoneNo);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        // TODO: Check whether this works on a real phone
//        gpsBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // check if GPS enabled
//                GPSTracker gpsTracker = new GPSTracker(MessProfileEditor.this);
//
//                if (gpsTracker.getIsGPSTrackingEnabled())
//                {
//                    gpsTracker.updateGPSCoordinates();
//                    Log.d(TAG, "onCreate: LATITUDE: " + gpsTracker.getLatitude());
//                    Log.d(TAG, "onCreate: LONGITUDE: " + gpsTracker.getLongitude());
//                }
//                else
//                {
//                    // can't get location
//                    // GPS or Network is not enabled
//                    // Ask user to enable GPS/network in settings
//                    gpsTracker.showSettingsAlert();
//                }
//            }
//        });

        // Set an click event listener on the Save button
        editProfileSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messName = editProfileMessName.getText().toString();
                String messMobileNo = editProfileMobileNo.getText().toString();
                String messTelephoneNo = editProfileTelephoneNo.getText().toString();
                if (messName.isEmpty()) {
                    editProfileMessName.requestFocus();
                    Toast.makeText(MessProfileEditor.this, "Please enter a name/brand name for your mess", Toast.LENGTH_LONG).show();
                    return;
                }

                if (messLatitude == 0.0 && messLongitude == 0.0) {
                    Toast.makeText(MessProfileEditor.this, "Please select your current location\nusing the My Location Button\nfrom the map above", Toast.LENGTH_LONG).show();
                    return;
                }

                if (messMobileNo.isEmpty()) {
                    editProfileMobileNo.requestFocus();
                    Toast.makeText(MessProfileEditor.this, "Please enter a mobile no. of your mess", Toast.LENGTH_LONG).show();
                    return;
                }

                if (messTelephoneNo.isEmpty()) {
                    editProfileTelephoneNo.requestFocus();
                    Toast.makeText(MessProfileEditor.this, "Please enter a telephone no. of your mess", Toast.LENGTH_LONG).show();
                    return;
                }

                // Store these values in the database
                DatabaseReference currentDBRef = FirebaseDatabase.getInstance().getReference().child("Users").child("MessProviders").child(userID).child("ProfileInformation");
                currentDBRef.child("mProviderBrandName").setValue(messName);
                currentDBRef.child("mProviderLocation").child("mProviderLatitude").setValue(messLatitude);
                currentDBRef.child("mProviderLocation").child("mProviderLongitude").setValue(messLongitude);
                currentDBRef.child("mProviderMobileNo").setValue(messMobileNo);
                currentDBRef.child("mProviderTelephoneNo").setValue(messTelephoneNo);

               //Store longitude and latitude values  in the database in a separate Node
                postCordinates(messName,messMobileNo);
                Toast.makeText(MessProfileEditor.this, "Values saved in the database", Toast.LENGTH_LONG).show();
            }
        });



    }

    @Override
    public void onBackPressed() {
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
        getMenuInflater().inflate(R.menu.mess_profile_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Log.d(TAG, "onNavigationItemSelected: navHeader(LinearLayout): " + R.id.nav_header);
        Log.d(TAG, "onNavigationItemSelected: messProfileImage: " + R.id.messOwnerProfileImg);
        Log.d(TAG, "onNavigationItemSelected: id: " + id);

        if (id == R.id.messOwnerProfileImg) {
            Log.d(TAG, "onNavigationItemSelected: messProfileImage: " + R.id.messOwnerProfileImg);
            Log.d(TAG, "onNavigationItemSelected: id: " + id);
        }


        if (id == R.id.nav_edit_profile) {
            Log.d(TAG, "onNavigationItemSelected: Edit Profile Clicked");
        } else if (id == R.id.nav_edit_menu) {
            Log.d(TAG, "onNavigationItemSelected: Edit Menu Clicked");
        } else if (id == R.id.nav_customers_orders) {
            Log.d(TAG, "onNavigationItemSelected: Customer Orders Clicked");
        } else if (id == R.id.nav_order_history) {
            Log.d(TAG, "onNavigationItemSelected: Order History Clicked");
        } else if (id == R.id.nav_user_reviews) {
            Log.d(TAG, "onNavigationItemSelected: User Reviews Clicked");
        } else if (id == R.id.nav_settings) {
            Log.d(TAG, "onNavigationItemSelected: Settings Clicked");
        } else if (id == R.id.nav_sign_out) {
            mAuth.signOut();
            Toast.makeText(MessProfileEditor.this, "You've Signed Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MessProfileEditor.this, MessReg_SignInActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);


        enableMyLocation();

        // Setting onclick event listener for the map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                markers(point);
            }
        });



    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.

            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        //messLatitude = location.getLatitude();
        //messLongitude = location.getLongitude();
     //   Toast.makeText(this, "Latitude (" + messLatitude + ") & Longitude (" + messLongitude + ") Values copied", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    public void markers(LatLng point)
    {
        // clearing map and generating new marker points
        mMap.clear();
        MarkerPoints.clear();
       // Adding new item to the ArrayList
        MarkerPoints.add(point);
        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();
        // Setting the position of the marker
        options.position(point);

        /**
         * For the start location, the color of marker is Red

         */
       if (MarkerPoints.size() == 1) {
            messLongitude= point.longitude;
            messLatitude=point.latitude;
            Geocoder geocoder = new Geocoder(MessProfileEditor.this);
          try {
                List<Address> addresses = geocoder.getFromLocation(messLatitude, messLongitude, 1);
                if (addresses != null && addresses.size() > 0) {
                    String address=addresses.get(0).getAddressLine(0);

                    Toast.makeText(getApplicationContext(), addresses.get(0).getAddressLine(0), Toast.LENGTH_LONG).show();
                }
           } catch (IOException e) {
               e.printStackTrace();
            }
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

      }
        // Add new marker to the Google Map Android API V2
        mMap.addMarker(options);
        CameraPosition position = CameraPosition.builder()
                .target(point)
                .zoom(16)
                .bearing(0)
                .tilt(0)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));

            }


    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }
    public void setMproviderLatitude(double messLatitude){
        this.messLatitude=messLatitude;
    }
    public void setMproviderLongitude(double messLongitude){
        this.messLongitude=messLongitude;
    }
    public double getMproviderLatitude(){
        return messLatitude;
    }
    public double getMproviderLongitude(){
        return messLongitude;
    }

    public void postCordinates(String messName, String messMobileNo)
    {
        DatabaseReference currentDBcordinates = FirebaseDatabase.getInstance().getReference().child("Users").child("MessProviders").child("locations").child(userID);
        currentDBcordinates.child("mProviderLatitude").setValue(messLatitude);
        currentDBcordinates.child("mProviderLongitude").setValue(messLongitude);
        currentDBcordinates.child("mProviderBrandName").setValue(messName);
    }
}
