package com.example.kamalnayan.qunto

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment(),ZXingScannerView.ResultHandler {
    var qrCodeScanner: ZXingScannerView? = null
    public var home: Home? = null
    val  MY_CAMERA_REQUEST_CODE=101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        home=activity as Home
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,

        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_search, container, false)
     qrCodeScanner = rootView.findViewById(R.id.qrCodeScanner)
        return rootView
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context as Home,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(context as Home,
                        android.Manifest.permission.CAMERA)) {
                    val alertBuilder = AlertDialog.Builder(context as Home)
                    alertBuilder.setCancelable(true)
                    alertBuilder.setTitle("Camera permission necessary")
                    alertBuilder.setMessage("This feature needs camera permission to read barcode.")
                    alertBuilder.setPositiveButton(android.R.string.yes,
                       DialogInterface.OnClickListener { dialog, which ->
                            ActivityCompat.requestPermissions(
                                activity as Home,
                                arrayOf(android.Manifest.permission.CAMERA),
                                MY_CAMERA_REQUEST_CODE)
                       })

                    val alert = alertBuilder.create()
                    alert.show()
                }
                else {
                    ActivityCompat.requestPermissions(
                        activity as Home, arrayOf(android.Manifest.permission.CAMERA),
                        MY_CAMERA_REQUEST_CODE
                    )
                }
            }
        }
            qrCodeScanner!!.startCamera()
            qrCodeScanner!!.setResultHandler(this)
    }

    override fun handleResult(p0: Result?) {
        if (p0 != null) {
            openMenu(p0.text)
        }
    }
        override fun onPause() {
            super.onPause()
            qrCodeScanner!!.stopCamera()
        }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_CAMERA_REQUEST_CODE -> {

                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    qrCodeScanner!!.startCamera()
                    qrCodeScanner!!.setResultHandler(this)
                } else {
                    /*val fm = fragmentManager
                    if(fm!!.backStackEntryCount>0)
                        fm.popBackStack();*/
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }
    fun openMenu( id:String)
    {
        val mFirebaseDatabase = FirebaseDatabase.getInstance()
        val mDatabaseReference = mFirebaseDatabase.getReference("Restaurants_List").child(id)

        if(mDatabaseReference==null)
        {
            showDialog()
            return
        }
       mDatabaseReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val RData = dataSnapshot.getValue(Restaurants_data::class.java)
                    val intent = Intent(activity, RestaurantProfile::class.java)
                    intent.putExtra("photoId", RData!!.photoId)
                    intent.putExtra("resName", RData.name)
                    intent.putExtra("city", RData.city)
                    intent.putExtra("cousine", RData.cousine)
                    intent.putExtra("rate", RData.rate)
                    intent.putExtra("serve", RData.serve)
                    intent.putExtra("open", RData.open)
                    intent.putExtra("rating", RData.rating)
                    intent.putExtra("number", RData.number)
                    intent.putExtra("id", RData.id)
                    intent.putExtra("active",true);
                    intent.putExtra("address",RData.address)
                    activity!!.startActivity(intent)
                }
                else
                {
                    showDialog()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("data error", "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })


    }
 fun showDialog()
 {
     val alertBuilder = AlertDialog.Builder(context as Home)
     alertBuilder.setCancelable(true)
     alertBuilder.setTitle("Invalid QR Code")
     alertBuilder.setMessage("This is not a valid QR_Code of any restaurant")
     alertBuilder.setPositiveButton(android.R.string.yes,
         DialogInterface.OnClickListener { dialog, which ->
             onResume()
         })

     val alert = alertBuilder.create()
     alert.show()
 }
    }