package com.example.kamalnayan.qunto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationAndMap extends AppCompatActivity implements OnMapReadyCallback {
    double mLat=-1;
    double mLng=-1;
    double mLat1=-1;
    double mLng1=-1;
   private String UserId;
    String location=null;
    String city=null;
    private String add;
    SharedPreferences sharedpreferences;
    private GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_and_map);
        UserId= FirebaseAuth.getInstance().getUid();
        sharedpreferences = getSharedPreferences("myLocation", Context.MODE_PRIVATE);
        Intent intent = getIntent();
        mLat = intent.getDoubleExtra("latitude",-1);
        mLng=intent.getDoubleExtra("longitude",-1);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_icon);
        if (mLat!=-1 && mLng!=-1) {

            LatLng latLng = new LatLng(mLat, mLng);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                    .title("Hold and move pin to adjust");
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
            googleMap.addMarker(markerOptions).showInfoWindow();
            googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    mLat=marker.getPosition().latitude;
                    mLng=marker.getPosition().longitude;
                    getAddress(mLat,mLng);
                }
            });
                    getAddress(mLat, mLng);
        }
    }
    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(LocationAndMap.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            add = obj.getAddressLine(0);
            TextView tvLocation=findViewById(R.id.tVLocation);
            tvLocation.setText(add);
            /*add = add + ", " + obj.getCountryName();
            add = add + ", " + obj.getCountryCode();
            add = add + ", " + obj.getAdminArea();
            add = add + ", " + obj.getPostalCode();
            add = add + ", " + obj.getSubAdminArea();
            add = add + ", " + obj.getLocality();
            add = add + ", " + obj.getSubThoroughfare();
            add = add + ", " + obj.getSubLocality();*/
            mLat1=obj.getLatitude();
            mLng1=obj.getLongitude();
            location=obj.getLocality();
            city=obj.getSubAdminArea();
            Log.v("IGA", "Address" + add);
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void setSharedPreference(String type,String value)
    {
        SharedPreferences sharedPref = getSharedPreferences("myLocation", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(type,value);
        editor.commit();
    }
    public  void bLocationSetter(View view)
    {
        LocationHistory locationHistory=new LocationHistory(add,mLat1,mLng1);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("User").child(UserId).child("LocationHistory");
        databaseReference.push().setValue(locationHistory);
        if(location!=null) {
            setSharedPreference("Location", location);
            setSharedPreference("city",city);

        }
        Intent intent=new Intent(LocationAndMap.this,Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    public void bAddressSet(View view)
    {

    }

}
