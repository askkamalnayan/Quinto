package com.example.kamalnayan.qunto

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.support.design.widget.BottomNavigationView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_home.*
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.widget.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.support.design.widget.Snackbar





class Home : AppCompatActivity() {
    var view:View?=null
    var defaultMenu:Menu?=null
    var coordinatorLayout:View?=null
    private var sharedPreferences:SharedPreferences?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        startFragment()
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        view = findViewById<View>(R.id.m_home)
      sharedPreferences =getSharedPreferences("Cart",Context.MODE_PRIVATE)
        coordinatorLayout=findViewById(R.id.viewSnack);


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId)
        {
            R.id.m_cart->{
                val intent=Intent(this,FoodCart::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.m_home -> {
                startFragment()
                return@OnNavigationItemSelectedListener true
            }
            R.id.m_search -> {
                replaceFragment(SearchFragment(),"fragS")
                return@OnNavigationItemSelectedListener true
            }
            R.id.m_profile -> {
               replaceFragment(ProfileFragment(),"fragP")
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }



    private fun replaceFragment(fragment: Fragment,fragName:String){
        val manager = supportFragmentManager
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentHolder, fragment).addToBackStack(fragName)
        if (manager.getBackStackEntryCount() > 0)
            manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        fragmentTransaction.commit()

    }
    private fun startFragment()
    {
        val manager = supportFragmentManager
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentHolder,Home_Fragment())
        if (manager.getBackStackEntryCount() > 0)
            manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentTransaction.commit()
    }

    public  fun SetNavigationVisibiltity(b: Boolean) {
        if (b) {
            navigation.animate().translationY(0f).setDuration(300)
        } else {
            navigation.animate().translationY(navigation.height.toFloat()).setDuration(300)
        }
    }

    override fun onBackPressed() {

        val count = supportFragmentManager.backStackEntryCount

        if (count == 0) {
            super.onBackPressed()
            //additional code
        } else {
            navigation.menu.getItem(0).setChecked(true)
            supportFragmentManager.popBackStack()
        }

    }

    override fun onStart() {
        super.onStart()
        var count:String="0";
        if(sharedPreferences!=null)
        {
            count=sharedPreferences!!.getString("ItemCount","0")
        }
        if(defaultMenu!=null)
        {
            setCount(this, count,defaultMenu)

        };

    }
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        var count:String="0";
        defaultMenu=menu
        if(sharedPreferences!=null)
        {
           count=sharedPreferences!!.getString("ItemCount","0")
        }
        setCount(this, count,menu);
        return super.onPrepareOptionsMenu(menu)
    }
    fun setCount(context: Context, count: String,menu: Menu?) {
        val menuItem = menu!!.findItem(R.id.m_cart)
        val icon = menuItem.getIcon() as LayerDrawable

        val badge: CountDrawable

        // Reuse drawable if possible
        val reuse = icon.findDrawableByLayerId(R.id.ic_group_count)
        if (reuse != null && reuse is CountDrawable) {
            badge = reuse
        } else {
            badge = CountDrawable(context)
        }

        badge.setCount(count)
        icon.mutate()
        icon.setDrawableByLayerId(R.id.ic_group_count, badge)
        if(count.toInt()>0)
        {
            val snackbar = Snackbar.make(coordinatorLayout!!, "Food is waiting for you in your cart", Snackbar.LENGTH_SHORT)
            snackbar.show()
        }
    }
}
