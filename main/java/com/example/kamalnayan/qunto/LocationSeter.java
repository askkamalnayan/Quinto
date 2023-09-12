package com.example.kamalnayan.qunto;

import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.*;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class LocationSeter extends AppCompatActivity  implements
        GoogleApiClient.ConnectionCallbacks ,
        GoogleApiClient.OnConnectionFailedListener {
    final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    TextView txtView;
    View crntLocation;
    double mLat=-1;
    double mLng=-1;
    private GoogleMap mMap;
    Location mLastKnownLocation;
    boolean mLocationPermissionGranted;
    final int  REQUEST_CHECK_SETTINGS=102;
    FusedLocationProviderClient fusedLocationClient;
    private RecyclerView locationRecycler;
    private DatabaseReference databaseReference;
    private String UserId;
    private List<LocationHistory>locationSeterList;
    public static final String TAG = LocationSeter.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_seter);
        txtView = findViewById(R.id.placeName);
        crntLocation = findViewById(R.id.currentLocation);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        crntLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCurrentLocation();

            }
        });
        placeAutoComplete();
        UserId= FirebaseAuth.getInstance().getUid();
        locationRecycler=findViewById(R.id.rVLocation);
        inflateLocationRecycler();
    }


    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        Log.i(TAG, "API Client Connection Successful!");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "API Client Connection Suspended!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.e(TAG, "API Client Connection Failed!");
    }


    // Initialize Places.
    private void placeAutoComplete() {
        Places.initialize(getApplicationContext(), "AIzaSyDdE8peJfKH2nnOBp3bGkWi0sPZxz2rRg8");
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Toast.makeText(LocationSeter.this, "Place: " + place.getName() + ", " + place.getId(), Toast.LENGTH_LONG).show();
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());

                    LatLng latLng = place.getLatLng();
                    if(latLng!=null) {
                        mLat = latLng.latitude;
                        mLng = latLng.longitude;

                        Intent intent=new Intent(LocationSeter.this,LocationAndMap.class);
                        intent.putExtra("latitude",mLat);
                        intent.putExtra("longitude",mLng);
                        startActivity(intent);
                    }


            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

    }

    private void fetchCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        } else {
            Task<Location> task = fusedLocationClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //Log.e("onsuccess3","kuchh v");
                    if (location != null) {
                        Log.e("onsuccess3","kuchh 12 v");
                        mLastKnownLocation = location;
                        mLat=mLastKnownLocation.getLatitude();
                        mLng=mLastKnownLocation.getLongitude();
                        Intent intent=new Intent(LocationSeter.this,LocationAndMap.class);
                        intent.putExtra("latitude",mLat);
                        intent.putExtra("longitude",mLng);
                        startActivity(intent);
                    }
                    else {
                        createLocationRequest();
                    }
                }
            });

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchCurrentLocation();
                }
            }
        }
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }



 private void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder LSBuilder = new LocationSettingsRequest.Builder();
        LSBuilder.addLocationRequest(mLocationRequest);
     SettingsClient client = LocationServices.getSettingsClient(this);
     Task<LocationSettingsResponse> task = client.checkLocationSettings(LSBuilder.build());

     task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
         @Override
         public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
             fetchCurrentLocation();
         }
     });

     task.addOnFailureListener(this, new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
             if (e instanceof ResolvableApiException) {
                 // Location settings are not satisfied, but this can be fixed
                 // by showing the user a dialog.
                 try {
                     // Show the dialog by calling startResolutionForResult(),
                     // and check the result in onActivityResult().

                     ResolvableApiException resolvable = (ResolvableApiException) e;
                     resolvable.startResolutionForResult(LocationSeter.this,
                             REQUEST_CHECK_SETTINGS);

                 } catch (IntentSender.SendIntentException sendEx) {
                     // Ignore the error.
                 }
             }
         }
     });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            //Log.e("onsuccess1",String.valueOf(requestCode));
            if (resultCode == RESULT_OK) {
                //Log.e("onsuccess","kuchh v");
                createLocationRequest();
            }
        }
    }

    void inflateLocationRecycler()
    {
        locationSeterList=new ArrayList<>();
        databaseReference=FirebaseDatabase.getInstance().getReference("User").child(UserId).child("LocationHistory");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                        LocationHistory locationHistory=dataSnapshot1.getValue(LocationHistory.class);
                        locationSeterList.add(locationHistory);
                    }
                }
                LocationHistoryAdapter locationHistoryAdapter=new LocationHistoryAdapter(LocationSeter.this,locationSeterList);
                locationRecycler.setLayoutManager(new LinearLayoutManager(LocationSeter.this,LinearLayoutManager.VERTICAL, false));
                locationRecycler.setAdapter(locationHistoryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
