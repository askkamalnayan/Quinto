package com.example.kamalnayan.qunto

data class CartData(
    val itemName:String,val itemType:Int,val itemRate:String ,val count:Int
)
{
    constructor():this("Name",0,"100.00",0)
}