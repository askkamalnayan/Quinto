package com.example.kamalnayan.qunto

import android.animation.ValueAnimator
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash_time_out:Long=2500
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val title=findViewById<ImageView>(R.id.iVtitle)
       // val subTitle=findViewById<ImageView>(R.id.iVSubTitle)
        val fromBotom=AnimationUtils.loadAnimation(this,R.anim.frombottom)
          title.startAnimation(fromBotom)
       // val rotate=AnimationUtils.loadAnimation(this,R.anim.rotate_anim)
        //subTitle.startAnimation(rotate)

       val background=object : Thread(){
           override fun run(){
               try{
                   Thread.sleep(splash_time_out)
                   val intent=Intent(baseContext,Login::class.java)
                   startActivity(intent)
                   finish()
               }
               catch (e:Exception)
               {
                   e.printStackTrace()
               }
           }
       }
        background.start()
    }

}
