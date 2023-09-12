package com.example.kamalnayan.qunto

import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView

class LocationHistoryAdapter (val context:Context,val locationHistoryList: List<LocationHistory>)
    :RecyclerView.Adapter<LocationHistoryAdapter.ViewHolder>()

{
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): LocationHistoryAdapter.ViewHolder {
        val v=LayoutInflater.from(p0.context).
            inflate(R.layout.location_ticket,p0,false)
        return ViewHolder(v);

    }

    override fun getItemCount(): Int {
        if(locationHistoryList.size>5)
            return 5;
        return locationHistoryList.size
    }

    override fun onBindViewHolder(p0: LocationHistoryAdapter.ViewHolder, p1: Int) {
        val locationTicket=locationHistoryList[locationHistoryList.size-1-p1];
        p0.textView.setText(locationTicket.address);
        p0.cardView.setOnClickListener{
            val intent = Intent(context, LocationAndMap::class.java)
            intent.putExtra("latitude", locationTicket.lat)
            intent.putExtra("longitude", locationTicket.lng)
            context.startActivity(intent)
        }

    }
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
       val cardView:CardView=itemView.findViewById(R.id.cVlocation12);
        val textView:TextView=itemView.findViewById(R.id.tVlocation12);
    }
}