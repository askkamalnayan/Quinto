package com.example.kamalnayan.qunto

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color
import android.renderscript.Sampler
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.active_menu.view.*



class MenuChildAdapter(private val active:Boolean,private val context: Context,private val children : List<MenuChild>,private  val ResUid:String)
    : RecyclerView.Adapter<MenuChildAdapter.ViewHolder>(){
    val mDataBase=FirebaseDatabase.getInstance().getReference("User")
    val userid=FirebaseAuth.getInstance().uid
    val sharedPreferences:SharedPreferences=context.getSharedPreferences("Cart",Context.MODE_PRIVATE)
    var coordinatorLayout:CoordinatorLayout?=null
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MenuChildAdapter.ViewHolder {
        val v =  LayoutInflater.from(p0.context)
            .inflate(R.layout.active_menu,p0,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
       return children.size
    }

    override fun onBindViewHolder(p0: MenuChildAdapter.ViewHolder, p1: Int) {
       val child=children[p1]
        p0.menuItemCost.text=child.itemRate.toString()+".00"
        p0.menuItemName.text=child.itemName
       if(child.itemType==0)
        {
            //veg sign image has name nonveg by mistake so its veg image
            p0.ivMenuImage.setImageResource(R.drawable.nonveg);
        }
        else
            p0.ivMenuImage.setImageResource(R.drawable.veg)
        var s=p0.tvAdd.text.toString()
        var i:Int=0
       if(active) {
           p0.tvPlus.setOnClickListener {
               if (s.equals("Add")) {
                   p0.tvAdd.text = "1"
                   s = "1"
                   i = 1
               } else {
                   try {
                       i = s.toInt() + 1
                       p0.tvAdd.text = (i).toString()
                       s = i.toString()
                   } catch (nfe: NumberFormatException) {
                       Log.e("MenuChildAdapter", s)
                   }

               }
               setItemCount(1)
               val cartData = CartData(child.itemName, child.itemType, child.itemRate, i)
               addToCart(cartData)
           }
           p0.tvMinus.setOnClickListener {
               if (s.equals("Add") || s.equals("1")) {
                   if (s.equals("1")) {
                       setItemCount(-1)
                   }
                   p0.tvAdd.text = "Add"
                   s = "Add"
                   i = 0
               } else {
                   try {
                       i = s.toInt() - 1
                       p0.tvAdd.text = (i).toString()
                       s = i.toString()
                   } catch (nfe: NumberFormatException) {
                       Log.e("MenuChildAdapter", s)
                   }
                   setItemCount(-1)
               }

               val cartData = CartData(child.itemName, child.itemType, child.itemRate, i)
               addToCart(cartData)
           }
       }
        else
       {
           p0.tvAdd.setTextColor(Color.parseColor("#9fa89f"));
           p0.tvMinus.setTextColor(Color.parseColor("#9fa89f"));
           p0.tvPlus.setTextColor(Color.parseColor("#9fa89f"));
       }
    }
    fun addToCart(cartData: CartData)
    {
        var Ruid:String="nan"
       mDataBase.child(userid.toString()).child("Cart").child("Restaurant Uid").addListenerForSingleValueEvent(object :ValueEventListener{
           override fun onCancelled(p0: DatabaseError) {

           }
           override fun onDataChange(p0: DataSnapshot) {
              Ruid=p0.getValue().toString()
               if((!Ruid.equals("nan") && !Ruid.equals(ResUid))||Ruid.equals("nan"))
               {
                   mDataBase.child(userid.toString()).child("Cart").removeValue()
                   mDataBase.child(userid.toString()).child("Cart").child("Restaurant Uid").setValue(ResUid)
                   mDataBase.child(userid.toString()).child("Cart").child(cartData.itemName).setValue(cartData)

               }
               else{
                   mDataBase.child(userid.toString()).child("Cart").child(cartData.itemName).setValue(cartData)

               }

           }
       })
    }
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val ivMenuImage:ImageView=itemView.findViewById(R.id.ivVeg)
        val menuItemName:TextView=itemView.tVMenuItem
        val menuItemCost:TextView=itemView.tVMenuItemPrice
        val tvPlus=itemView.findViewById<TextView>(R.id.tvPlus)
        val tvMinus=itemView.findViewById<TextView>(R.id.tvMinus)
        val tvAdd=itemView.findViewById<TextView>(R.id.tvAdd)

    }
    fun setItemCount(tot:Int)
    {
        val i=sharedPreferences.getString("ItemCount","0")
        val k=tot+i.toInt()
        val editor = sharedPreferences.edit()
        editor.putString("ItemCount",k.toString())
        editor.commit()
    }


}