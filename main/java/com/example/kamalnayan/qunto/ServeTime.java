package com.example.kamalnayan.qunto;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.database.*;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ServeTime extends AppCompatActivity {
private TextView mTextField;
private DatabaseReference mDatabaseReference;
private SharedPreferences mSharedPrefernce;
private String Ruid;
private TextView yourToken;
static private boolean changeToken;
private TextView currentToken;
private Button button1;
private Dialog mDialog;
private long startTime;
final Context context=this;
private long currentTime;
private CountDownTimer cTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serve_time);
        changeToken=true;
        //mDialog= new Dialog(ServeTime.this);
        mTextField =findViewById(R.id.tVTime);
        yourToken=findViewById(R.id.tvYourToken);
        currentToken=findViewById(R.id.tVServingToken);
        button1=findViewById(R.id.bCancelOrder);
        mSharedPrefernce=getSharedPreferences("Cart",MODE_PRIVATE);
        currentTime= System.currentTimeMillis();
        startTime=mSharedPrefernce.getLong("StartTime",currentTime);
        if(startTime==123456789)
            startTime=currentTime;
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ServeTime.this)
                        .setTitle("Order Cancellation ")
                        .setMessage("Shall we cancel your order?")
                        .setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener()
                                {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        changeToken=false;
                                        mSharedPrefernce.edit().putString("YourToken","nan").apply();
                                        mSharedPrefernce.edit().putLong("StartTime",123456789).apply(); ;
                                        cancelOrder();
                                        Intent intent=new Intent(getApplicationContext(),Home.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }


                        )
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
        });
        Ruid=mSharedPrefernce.getString("RestaurantUid","nan");
        System.out.println(Ruid);
        if(!Ruid.equals("nan"))
        updateUi();

        cTimer=new CountDownTimer(99999999, 1000) {
            public void onTick(long millisUntilFinished) {
                long millis=System.currentTimeMillis()-startTime;
                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                mTextField.setText(hms);
            }

            public void onFinish() {
                mTextField.setText("done!");
                cancelTimer();
            }

        }.start();
    }
   void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
         cancelTimer();
    }
    private void updateUi()
    {
        mDatabaseReference= FirebaseDatabase.getInstance().getReference("Orders").child(Ruid);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              //  System.out.println("data "+dataSnapshot.getValue().toString());
                if(dataSnapshot.exists())
                {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                        String key=dataSnapshot1.getKey();
                        if(key.equals("CurrentToken"))
                        {
                            currentToken.setText(dataSnapshot1.getValue().toString());

                        }
                        else if(key.equals("LastToken"))
                        {
                                yourTokenUpdate(dataSnapshot1.getValue().toString());
                        }
                        if(currentToken.getText().equals(yourToken.getText()))
                        {
                            foodServed();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void yourTokenUpdate(String tkn)
    {
        if(changeToken) {

            String yourtoken;
           // Log.e("ServeTime12", mSharedPrefernce.getString("YourToken", "nan3"));
            yourtoken = mSharedPrefernce.getString("YourToken", "nan");
            if (yourtoken.equals("nan")) {
                long ctime = System.currentTimeMillis();
               // Log.e("ServeTime", yourtoken);
                yourToken.setText(tkn);
                mSharedPrefernce.edit().putString("YourToken", tkn).apply();
                mSharedPrefernce.edit().putLong("StartTime", ctime).apply();
            } else {
                yourToken.setText(yourtoken);
            }
        }
    }
    private void cancelOrder()
    {
        FirebaseDatabase.getInstance().getReference("Orders").child(Ruid).child(mSharedPrefernce.getString("OrderId","nan")).removeValue();
    }
   void foodServed()
   {
       if(!((Activity) context).isFinishing()) {
           mDialog = new Dialog(context);
           mDialog.setContentView(R.layout.rating_dialog);
           Button b1 = mDialog.findViewById(R.id.bsubmit1);
           b1.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   mDialog.dismiss();
                   changeToken = false;
                   mSharedPrefernce.edit().putString("YourToken", "nan").apply();
                   mSharedPrefernce.edit().putLong("StartTime", 123456789).apply();
                   ;
                   cancelOrder();
                   Intent intent = new Intent(getApplicationContext(), Home.class);
                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                   startActivity(intent);
                   finish();
               }
           });
           mDialog.setCancelable(false);
           mDialog.show();
       }
   }
}
