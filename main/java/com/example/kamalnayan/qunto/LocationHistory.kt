package com.example.kamalnayan.qunto

data class LocationHistory(
    val address:String,
    val lat:Double,
    val lng:Double
)
{
    constructor():this("Unknown",23.4123,85.4399)
}