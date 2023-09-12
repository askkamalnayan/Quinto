package com.example.kamalnayan.qunto;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodCart extends AppCompatActivity {

    private String uid;
    private String Ruid;
    private ImageView RestaurantImage;
    private Restaurants_data RData;
    private TextView RestaurantName;
    private TextView RestaurantAddress;
    private RecyclerView rv;
    private CartAdapter cartAdapter;
    private Button submitButton;
    private TextView subtotal;
    private TextView total;
    private  DatabaseReference toPath;
    private TextView tax;
    private DatabaseReference mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    String currentToken="100000";
    String LastToken="100000";
    String cid;
    private FirebaseDatabase mFirebaseInstance;
    private ShimmerFrameLayout mshimmerLayout;
    private SharedPreferences sharedPreferences;
    float tot=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.foodcart);
        uid=FirebaseAuth.getInstance().getUid();
        rv=findViewById(R.id.rv2);
        rv.setNestedScrollingEnabled(false);
        mFirebaseInstance=FirebaseDatabase.getInstance();
        mFirebaseDatabase=mFirebaseInstance.getReference("User");
        mDatabaseReference = mFirebaseInstance.getReference("Restaurants_List");
        RestaurantImage=findViewById(R.id.iVRestaurant2);
        RestaurantName=findViewById(R.id.tVRestuarntName2);
        RestaurantAddress=findViewById(R.id.tVRestaurantAddress2);
        submitButton=findViewById(R.id.bPlaceOrder);
        subtotal=findViewById(R.id.tvSubtotalvalue);
        tax=findViewById(R.id.tvtaxValue);
        total=findViewById(R.id.tvTotalValue);
        mshimmerLayout=findViewById(R.id.shimmer_view_container1);
        sharedPreferences=getSharedPreferences("Cart",MODE_PRIVATE);
        setValuestoUi();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tot!=0) {
                    sendOrder();
                }
            }
        });
    }
    void  setValuestoUi()
    {

        mFirebaseDatabase.child(uid.toString()).child("Cart").child("Restaurant Uid").addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                if(dataSnapshot.exists()){
            Ruid=dataSnapshot.getValue().toString();
            if(Ruid!=null) {
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            mshimmerLayout.setVisibility(View.VISIBLE);
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                String key = dataSnapshot1.getKey();

                                if (key.equals(Ruid)) {
                                    RData = dataSnapshot1.getValue(Restaurants_data.class);
                                  updateUi();
                                }

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }}}
        });


    }

    @Override
    protected void onStart() {
        String yourtoken=sharedPreferences.getString("YourToken","nan");
        Log.e("Food Cart",yourtoken);
        if(!yourtoken.equals("nan"))
        {
            Intent intent=new Intent(FoodCart.this,ServeTime.class);
            startActivity(intent);
            finish();
        }
        super.onStart();
    }

    private void updateUi (){
    final List<CartData> cartList=new ArrayList<>();

            if (RData != null) {
                if (RData.getPhotoId() != null) {
                    Picasso.with(getApplicationContext()).load(RData.getPhotoId())
                            .noFade()
                            .fit()
                            .into(RestaurantImage);
                } else {
                    //Toast.makeText(context,"Unable to download all images",Toast.LENGTH_SHORT).show()
                }
                String s = RData.getName();
                RestaurantName.setText(s);
                s = RData.getAddress();
                RestaurantAddress.setText(s);
            }
    mFirebaseDatabase.child(uid.toString()).child("Cart").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists())
            {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {

                        String s=dataSnapshot1.getKey();
                        Log.e("Food Cart",s);
                       if(dataSnapshot1.exists() && !s.equals("Restaurant Uid")) {
                           CartData cartData = dataSnapshot1.getValue(CartData.class);
                           if (cartData.getCount() > 0) {
                               cartList.add(cartData);
                               tot=cartData.getCount()*Integer.valueOf(cartData.getItemRate())+tot;
                           }

                       }
                }
            }
            subtotal.setText(String.valueOf(tot));
            float tot1=tot/10;
            tax.setText(String.valueOf(tot1));
            tot1=tot1+tot;
            total.setText(String.valueOf(tot1));
            cartAdapter=new CartAdapter(cartList);
            rv.setLayoutManager(new LinearLayoutManager(FoodCart.this,LinearLayoutManager.VERTICAL, false));
            rv.setAdapter(cartAdapter);
            mshimmerLayout.setVisibility(View.GONE);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    }

    @Override
    protected void onResume() {
        mshimmerLayout.startShimmerAnimation();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mshimmerLayout.stopShimmerAnimation();
        super.onPause();
    }
    void  sendOrder()
    {

        new AlertDialog.Builder(this)
                .setTitle("Confirm your order")
                .setMessage("Shall we start preparing food for you?")
                .setPositiveButton(
                        "yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                               moveData();
                                dialog.cancel();
                                sharedPreferences.edit().putString("RestaurantUid",Ruid).commit();
                                sharedPreferences.edit().putString("ItemCount","0").commit();
                                Intent intent=new Intent(FoodCart.this,ServeTime.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                .setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }
                )
                .show();
    }
    private  void moveData()
    {


        final DatabaseReference fromPath=mFirebaseDatabase.child(uid).child("Cart");
        toPath=FirebaseDatabase.getInstance().getReference("Orders").child(Ruid);
        toPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                    {
                        String s=dataSnapshot1.getKey();
                        if(s.equals("CurrentToken"))
                        {
                            currentToken=dataSnapshot1.getValue().toString();
                        }
                        else if(s.equals("LastToken"))
                        {
                            LastToken=dataSnapshot1.getValue().toString();
                        }
                    }
                }

                fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        cid=toPath.push().getKey();
                        toPath.child(cid).setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                                if (firebaseError != null) {
                                    System.out.println("Copy failed");
                                } else {
                                    sharedPreferences.edit().putString("OrderId",cid).apply();
                                    toPath.child("CurrentToken").setValue(currentToken);
                                    toPath.child(cid).child("Restaurant Uid").setValue(LastToken);
                                    toPath.child("LastToken").setValue(String.valueOf(Integer.valueOf(LastToken)+1));
                                    fromPath.removeValue();

                                }
                            }
                        })  ;

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }
}
