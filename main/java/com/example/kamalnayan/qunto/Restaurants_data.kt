package com.example.kamalnayan.qunto

import android.media.Rating

data class Restaurants_data(val name:String,val photoId:String,val cousine:String, val rating:Float,val rate:String,val id:String,val city:String,val number:String,val open:String,val serve:String,val address:String)
{
    constructor():this("","","non",5f,"","","","","8:00am to 10:00pm","30 min","address")
}