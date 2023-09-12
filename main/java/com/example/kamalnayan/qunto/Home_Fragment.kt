package com.example.kamalnayan.qunto

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_home_.*
import kotlinx.android.synthetic.main.sort_and_filter.*

class Home_Fragment : Fragment() {
    val res_List=ArrayList<Restaurants_data>()
    var locationText: TextView?=null;
    var mShimmerViewContainer:ShimmerFrameLayout?=null
    var adapter:Restaurants_adapter?=null
    var home:Home?=null
    var mDialog:Dialog?=null
    var myLocation:String?=null
    var mcity:String?=null
    var boolArray=BooleanArray(8)
    var regex:Regex=".".toRegex(setOf(RegexOption.IGNORE_CASE))
        var rest_recycler_view:RecyclerView?=null
    private var mDatabaseReference: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val rootView=inflater.inflate(R.layout.fragment_home_, container, false)

       // addValueToAdapter()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rest_recycler_view=view.findViewById<RecyclerView>(R.id.rest_recycler)
        rest_recycler_view!!.setItemViewCacheSize(10)
        rest_recycler_view!!.setHasFixedSize(true)
        rest_recycler_view!!.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL,false)
        val mFirebaseDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mFirebaseDatabase.getReference("Restaurants_List")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val locationseter=view!!.findViewById<View>(R.id.relLayout1)
        locationText=view!!.findViewById(R.id.placeName)
        setLocationText();
        mDialog= Dialog(context)
        locationseter.setOnClickListener{
            val intent=Intent(context,LocationSeter::class.java)
            startActivity(intent);
        }
        mShimmerViewContainer = view!!.findViewById(R.id.shimmer_view_container);

        filter.setOnClickListener{
            openPopUp();
        }
      home=activity as Home
        addValueToAdapter()
            ScrollAwareFABBehavior(
                recyclerView = rest_recycler,
                floatingActionButton = filter
            ).start()

        super.onActivityCreated(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        val view = activity!!.findViewById<View>(R.id.m_home)
        view.requestFocus()
        mShimmerViewContainer!!.startShimmerAnimation()
        setLocationText();
    }

    override fun onPause() {
        mShimmerViewContainer!!.stopShimmerAnimation()
        super.onPause()
    }
    fun setLocationText()
    {
        val sharedPref = context!!.getSharedPreferences("myLocation",Context.MODE_PRIVATE) ?: return
         myLocation=sharedPref.getString("Location","Set Your Location");
        mcity=sharedPref.getString("city","nan")
        locationText!!.setText(myLocation);
    }
    fun addValueToAdapter()
    {

       mDatabaseReference!!.addValueEventListener(object : ValueEventListener{
           override fun onCancelled(p0: DatabaseError) {
               Toast.makeText(activity,p0.toString(),Toast.LENGTH_SHORT).show()
           }

           override fun onDataChange(p0: DataSnapshot) {
               res_List.clear()
               if(p0.exists())
               {
                   mShimmerViewContainer!!.visibility=View.VISIBLE

                    for(h in p0.children)
                    {
                        if(h.exists()){
                        try {
                            val resData = h.getValue(Restaurants_data::class.java);
                            if(   regex.containsMatchIn(resData!!.cousine)    &&(resData!!.city.equals("nan")||resData!!.city.equals(mcity,true)||resData!!.city.equals(myLocation,true))) {
                                res_List.add(resData!!)
                            }

                        }
                        catch (e:Exception)
                        {
                            Toast.makeText(activity,e.toString(),Toast.LENGTH_SHORT).show()
                        }
                    }
                    }
                   try {
                       adapter = Restaurants_adapter((activity as Home), home!!.applicationContext, res_List)
                       rest_recycler_view!!.adapter = adapter
                       mShimmerViewContainer!!.visibility = View.GONE
                   }
                   catch (e:Exception)
                   {
                       Log.e("HomeFragment",e.toString());
                   }

               }


           }


       })
    }

    inner class ScrollAwareFABBehavior (val recyclerView: RecyclerView, val floatingActionButton: FloatingActionButton) {

        fun start() {
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (dy > 0) {
                        if (floatingActionButton.isShown) {
                            floatingActionButton.hide()
                            (activity as Home).SetNavigationVisibiltity(false)

                        }

                    } else if (dy < 0) {
                        if (!floatingActionButton.isShown) {
                            floatingActionButton?.show()
                            (activity as Home).SetNavigationVisibiltity(true)
                        }

                    }
                }
            })
        }
    }
 fun openPopUp()
 {

     mDialog!!.setContentView(R.layout.sort_and_filter);
     val mexit=mDialog!!.tvexit;
     val checkBox1=mDialog!!.ctv1
     val checkBox2=mDialog!!.ctv2
     val checkBox3=mDialog!!.ctv3
     val checkBox4=mDialog!!.ctv4
     val checkBox5=mDialog!!.ctv5
     val checkBox6=mDialog!!.ctv6
     val checkBox7=mDialog!!.ctv7
     val checkBox8=mDialog!!.ctv8
     val submit=mDialog!!.bSubmit;


     if(boolArray[0]==true)
     {
         checkBox1.isChecked=true
     }
     if(boolArray[1]==true)
     {
         checkBox2.isChecked=true
     }
     if(boolArray[2]==true)
     {
         checkBox3.isChecked=true
     }
     if(boolArray[3]==true)
     {
         checkBox4.isChecked=true
     }
     if(boolArray[4]==true)
     {
         checkBox5.isChecked=true
     }
     if(boolArray[5]==true)
     {
         checkBox6.isChecked=true
     }
     if(boolArray[6]==true)
     {
         checkBox7.isChecked=true
     }
     if(boolArray[7]==true)
     {
         checkBox8.isChecked=true
     }
     val cuisineList=ArrayList<String>()
     submit.setOnClickListener {
         boolArray[0]=false
         boolArray[1]=false
         boolArray[2]= false
         boolArray[3]=false
         boolArray[4]=false
         boolArray[5]=false
         boolArray[6]=false
         boolArray[7]=false
       if(checkBox1.isChecked)
       {
           cuisineList.add("(Continental)")
           boolArray[0]=true
       }
         if(checkBox2.isChecked)
         {
             cuisineList.add("(Chinese)")
             boolArray[1]=true
         }
         if(checkBox3.isChecked)
         {
             cuisineList.add("(NortIndian)")
             boolArray[2]=true
         }
         if(checkBox4.isChecked)
         {
             cuisineList.add("(Indian)")
             boolArray[3]=true
         }
         if(checkBox5.isChecked)
         {
             cuisineList.add("(FastFood)")
             boolArray[4]=true
         }
         if(checkBox6.isChecked)
         {
             cuisineList.add("(SouthIndian)")
             boolArray[5]=true
         }
         if(checkBox7.isChecked)
         {
             cuisineList.add("(Italian)")
             boolArray[6]=true
         }
         if(checkBox8.isChecked)
         {
             cuisineList.add("(Snacks)")
             boolArray[7]=true
         }
      if(!cuisineList.isEmpty())
      {
          var cuisineString:String?=""
          cuisineString=cuisineString+cuisineList[0];
          val sz=cuisineList.size-1
          for(i in 1..sz)
          {
              cuisineString=cuisineString+"|"+cuisineList[i]
          }
          regex=cuisineString!!.toRegex(setOf(RegexOption.IGNORE_CASE))
          addValueToAdapter()
          mDialog!!.dismiss()
      }
         else{
          regex=".".toRegex(setOf(RegexOption.IGNORE_CASE))
          addValueToAdapter()
          mDialog!!.dismiss()
      }

     }
     mexit.setOnClickListener{
        mDialog!!.dismiss();
     }
     mDialog!!.show()
 }

}
