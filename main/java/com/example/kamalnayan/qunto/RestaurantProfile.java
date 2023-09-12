package com.example.kamalnayan.qunto;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import com.google.firebase.database.*;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RestaurantProfile extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ImageView ivPhoto;
    private CollapsingToolbarLayout ctbar;
    private ProgressBar pbar;
    private List<MenuChild> childList;
    private List<MenuParent> parentList;
    private TextView tvRating;
    private TextView tvRate;
    private TextView tvAddress;
    private TextView tvOpenTime;
    private TextView tvServeTime;
    private String id;
    Boolean active;
    private TextView tvPhNumber;
    private TextView tvCousine;
    private RecyclerView nestedrv;
    private RecyclerView childrv;
    private MenuParentAdapter menuParentAdapter;
 Restaurants_data restaurantDetails;
    private static final String TAG = "MyActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_profile);
        Intent intent = getIntent();
        active=intent.getBooleanExtra("active",true);
        String name=intent.getStringExtra("resName");
        String city=intent.getStringExtra("city");
        String cousine=intent.getStringExtra("cousine");
        String phnumber=intent.getStringExtra("number");
        String rate=intent.getStringExtra("rate");
        String serve=intent.getStringExtra("serve");
        String open=intent.getStringExtra("open");
        String address=intent.getStringExtra("address");
        Float rating=intent.getFloatExtra("rating",5);
        id=intent.getStringExtra("id");
        String photoid = intent.getStringExtra("photoId");
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebaseDatabase.getReference("Menu");
        ivPhoto=findViewById(R.id.respic);
        nestedrv=findViewById(R.id.rv_parent);
        childrv=findViewById(R.id.rv_child);
        ctbar=findViewById(R.id.restname);
        restaurantDetails=new Restaurants_data(name,photoid,cousine,rating,rate,id,city,phnumber,open,serve,address);
        ctbar.setTitle(name);
        supportPostponeEnterTransition();
        Picasso.with(RestaurantProfile.this)
                .load(photoid)
                .resize(1000,1000)
                .onlyScaleDown()
                .into(ivPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        supportStartPostponedEnterTransition();

                    }
                    @Override
                    public void onError() {
                        supportStartPostponedEnterTransition();
                    }
                });
        initRecycler();
  }
  void initRecycler()
  {


      parentList=new ArrayList<>();
      mDatabaseReference.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if(dataSnapshot.exists()) {
                  parentList.clear();
                  for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                      String key = dataSnapshot1.getKey();

                      if(key.equals(id)) {
                          for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                              String MenuHeading = dataSnapshot2.getKey();
                              if (dataSnapshot2.exists()) {
                                  childList=new ArrayList<>();
                                  for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {
                                      MenuChild menuChild = dataSnapshot3.getValue(MenuChild.class);
                                      childList.add(menuChild);

                                  }
                              }
                              parentList.add(new MenuParent(MenuHeading, childList));
                             // Log.e(parentList.get(parentList.size()-1).getTitle(),parentList.get(parentList.size()-1).getChild().get(0).getItemName());

                          }
                          break;
                      }
                  }
              }
              menuParentAdapter=new MenuParentAdapter(active,restaurantDetails,RestaurantProfile.this,parentList);
              nestedrv.setLayoutManager(new LinearLayoutManager(RestaurantProfile.this,LinearLayoutManager.VERTICAL, false));
              nestedrv.setAdapter(menuParentAdapter);
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });


  }
}
