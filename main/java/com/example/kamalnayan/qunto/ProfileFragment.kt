package com.example.kamalnayan.qunto

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.service.carrier.CarrierMessagingService
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import android.support.annotation.NonNull
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status


class ProfileFragment : Fragment() {

   var textView:TextView?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textView=view.findViewById(R.id.logout)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        textView!!.setOnClickListener(View.OnClickListener {
            logout();
        })

    }
    fun logout()
    {
        FirebaseAuth.getInstance().signOut();
        val intent=Intent(activity as Home,Login::class.java);
        startActivity(intent)
        (activity as Home).finish()
    }

    override fun onStart() {
        super.onStart()
    }
}
