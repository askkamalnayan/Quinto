package com.example.kamalnayan.qunto
data class MenuChild(
    val itemName:String,val itemType:Int,val itemRate:String
)
{
    constructor():this("Name",0,"100.00")
}