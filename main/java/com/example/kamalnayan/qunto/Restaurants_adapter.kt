package com.example.kamalnayan.qunto

import android.app.Activity
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.opengl.Visibility
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.widget.ProgressBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.active_menu.view.*
import kotlinx.android.synthetic.main.fragment_home_.view.*
import kotlinx.android.synthetic.main.list_layout.view.*
import kotlinx.android.synthetic.main.menu_item.view.*


class Restaurants_adapter(val activity:Activity,val contex: Context,val RestaurantsList:ArrayList<Restaurants_data>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    val rating_color = mutableListOf<String>("#CC0000","#CC3300","#CC6600","#CC9900","#CCCC00","#99CC00","#66CC00","#33CC00","#6ABA0D","#00AA00")
    private val FOOTER_VIEW = 1
    private val NORMAL_VIEW=0
    val sharedPreferences: SharedPreferences =contex.getSharedPreferences("Cart",Context.MODE_PRIVATE)
    override fun getItemViewType(position: Int): Int {
        if(position==RestaurantsList.size)
            return FOOTER_VIEW
        else
            return NORMAL_VIEW
    }
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        if(p1==NORMAL_VIEW)
        {
            val v=LayoutInflater.from(p0.context).inflate(R.layout.list_layout, p0,false)
            return ViewHolder1(v)
        }
        else
        {
            val v=LayoutInflater.from(p0.context).inflate(R.layout.footer,p0,false)
            return ViewFooter(v)
        }


    }
    override fun getItemCount(): Int {
        return RestaurantsList.size+1
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        if(p0 is ViewHolder1) {
            val RData: Restaurants_data = RestaurantsList[p1]
            p0.tVName.text = RData.name;
            p0.tvCost.text = RData.rate.toString() + " per person";
            p0.tvRating.text = RData.rating.toString()
            p0.tvType.text = RData.cousine
            p0.tvServe.text=RData.serve+" min"
            p0.tvOpen.text=RData.open
            if (RData.photoId != null && !RData.photoId.isEmpty() && RData.photoId != "non") {
                Picasso.with(contex)
                    .load(RData.photoId)
                    .noFade()
                    .resize(1000,1000)
                    .onlyScaleDown()
                    .into(p0.iVImage)
            } else {
                //Toast.makeText(context,"Unable to download all images",Toast.LENGTH_SHORT).show()
            }
            p0.tvRating.setBackgroundColor(Color.parseColor(rating_color[((RData.rating * 2 - 1)).toInt()]))
            p0.itemView.setOnClickListener {
                val pairImageView = Pair.create<View, String>(p0.iVImage, "restImage")
                val pairImageView1 = Pair.create<View, String>(p0.tvRating, "ratingTransition")
                val optionCompact: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pairImageView,pairImageView1)
                val intent = Intent(contex, RestaurantProfile::class.java)
                intent.putExtra("photoId", RData.photoId)
                intent.putExtra("resName", RData.name)
                intent.putExtra("city", RData.city)
                intent.putExtra("cousine", RData.cousine)
                intent.putExtra("rate", RData.rate)
                intent.putExtra("serve",RData.serve)
                intent.putExtra("active",false);
                intent.putExtra("open",RData.open)
                intent.putExtra("rating", RData.rating)
                intent.putExtra("number", RData.number)
                intent.putExtra("address",RData.address)
                intent.putExtra("id", RData.id)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                activity.startActivity(intent, optionCompact.toBundle())
                makeItemCountZero()
            }

        }
        if(p0 is ViewFooter)
        {

        }
    }

        inner class ViewHolder1 (itemView:View) :RecyclerView.ViewHolder(itemView)
    {
        val tVName=itemView.findViewById(R.id.tVName) as TextView;
        val iVImage=itemView.findViewById(R.id.iVImage) as ImageView
        val tvCost=itemView.findViewById<TextView>(R.id.tvcost)
        val tvRating=itemView.findViewById<TextView>(R.id.tvRating)
        val tvType=itemView.findViewById<TextView>(R.id.tvtype)
        val tvOpen=itemView.findViewById<TextView>(R.id.tvopen)
        val tvServe=itemView.findViewById<TextView>(R.id.time)

    }
    inner class ViewFooter (itemView: View):RecyclerView.ViewHolder(itemView)
    {

    }
    fun makeItemCountZero()
    {
        val editor = sharedPreferences.edit()
        editor.putString("ItemCount","0")
        editor.commit()
        val mDataBase= FirebaseDatabase.getInstance().getReference("User")
        val userid= FirebaseAuth.getInstance().uid
        mDataBase.child(userid.toString()).child("Cart").child("Restaurant Uid").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                mDataBase.child(userid.toString()).child("Cart").removeValue()
            }
        })
        }


}