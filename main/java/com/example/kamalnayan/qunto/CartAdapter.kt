package com.example.kamalnayan.qunto

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.menu_item.view.*

class   CartAdapter(private val children : List<CartData>)
    : RecyclerView.Adapter<CartAdapter.ViewHolder>(){
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CartAdapter.ViewHolder {
        val v =  LayoutInflater.from(p0.context)
            .inflate(R.layout.menu_item,p0,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return children.size
    }

    override fun onBindViewHolder(p0: CartAdapter.ViewHolder, p1: Int) {
        val child=children[p1]
        p0.menuItemCost.text=(child.itemRate.toInt()*child.count.toInt()).toString()
        p0.menuItemName.text=child.itemName
        p0.tvAdd.text=child.count.toString()
        if(child.itemType==0)
        {
            p0.ivMenuImage.setImageResource(R.drawable.nonveg);
        }
        else
            p0.ivMenuImage.setImageResource(R.drawable.veg)
    }
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val ivMenuImage: ImageView =itemView.findViewById(R.id.ivVeg1)
        val menuItemName: TextView =itemView.tVMenuItem1
        val menuItemCost: TextView =itemView.tVMenuItemPrice1
        val tvAdd=itemView.findViewById<TextView>(R.id.tvAdd2)

    }
}