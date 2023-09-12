package com.example.kamalnayan.qunto

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_restaurant_profile.view.*
import kotlinx.android.synthetic.main.menu_ticket.view.*
import kotlinx.android.synthetic.main.res_header.view.*

class MenuParentAdapter(val active:Boolean,val resturantDetails:Restaurants_data,val context:Context,private val parents:List<MenuParent>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    val rating_color = mutableListOf<String>("#CC0000","#CC3300","#CC6600","#CC9900","#CCCC00","#99CC00","#66CC00","#33CC00","#6ABA0D","#00AA00")
    private val HEADER=0;
    private val ITEM=1;
    private val FOOTER=2;
    private var mDatabaseReference: DatabaseReference? = null
    private val viewPool = RecyclerView.RecycledViewPool()
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        if(p1==1) {
            val v = LayoutInflater.from(p0.context)
                .inflate(R.layout.menu_ticket, p0, false)
            return ViewHolder(v)
        }
        else if(p1==0) {
            val v = LayoutInflater.from(p0.context)
                .inflate(R.layout.res_header, p0, false)
            return Header(v)
        }
       else {
            val v = LayoutInflater.from(p0.context)
                .inflate(R.layout.footer, p0, false)
            return Footer(v)
        }

    }

    override fun getItemViewType(position: Int): Int {
        if(position==0)
            return HEADER
        else if(position<=parents.size)
            return ITEM
        else
            return FOOTER

    }
    override fun getItemCount(): Int {
        return parents.size+2
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ViewHolder) {
            val parent = parents[position-1];
            holder.menuTitle.text = parent.title
            val childLayoutManager = LinearLayoutManager(holder.recyclerView.context, LinearLayout.VERTICAL, false)
            holder.recyclerView.apply {
                layoutManager = childLayoutManager
                adapter = MenuChildAdapter(active,context,parent.child,resturantDetails.id)
                setRecycledViewPool(viewPool)
            }
        }
        else if(holder is Header)
        {
            holder.tvAddress.text=resturantDetails.city
            holder.tvCousine.text=resturantDetails.cousine
            holder.tvOpenTime.text=resturantDetails.open
            holder.tvPhNumber.text=resturantDetails.number
            holder.tvRating.text=resturantDetails.rating.toString()
            holder.tvRating.setBackgroundColor(Color.parseColor(rating_color[((resturantDetails.rating * 2 - 1)).toInt()]))
            holder.tvServeTime.text=resturantDetails.serve+" min"
            holder.tvRate.text=resturantDetails.rate+" per person"
        }
        else if(holder is Footer)
        {

        }
    }
inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
    val recyclerView:RecyclerView=itemView.rv_child
    val menuTitle:TextView=itemView.tVMenuName
}
    inner class Header(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        val tvRating=itemView.tvRating2;
       val tvCousine=itemView.tvCousine3;
        val tvAddress=itemView.tvAddress3
       val tvPhNumber=itemView.tvPhNumber3
       val tvOpenTime=itemView.tvOpenTime3
       val tvServeTime=itemView.tvServeTime3
       val tvRate=itemView.tvRate3
    }
    inner class Footer(itemView: View):RecyclerView.ViewHolder(itemView) {
    }
}