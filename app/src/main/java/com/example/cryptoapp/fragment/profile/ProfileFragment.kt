package com.example.cryptoapp.fragment.profile

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.cryptoapp.MainActivity
import com.example.cryptoapp.R
import java.util.*

class ProfileFragment : Fragment() {
    private lateinit var userLogo: ImageView
    private lateinit var userEmail: TextView
    private lateinit var registrationDate: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        bindUI(view)
        initUI()

        return view
    }

    private fun bindUI(view: View){
        userLogo = view.findViewById(R.id.user_logo)
        userEmail = view.findViewById(R.id.email)
        registrationDate = view.findViewById(R.id.registration_date)
    }

    private fun initUI(){
        val user = (activity as MainActivity).mAuth.currentUser
        Glide.with(this).load(user?.photoUrl).placeholder(R.drawable.ic_avataaars).circleCrop().into(userLogo)
        userEmail.text = user?.email
        registrationDate.text = user?.metadata?.creationTimestamp?.let { getDate(it) }
    }

    private fun getDate(timeStamp: Long): String? {
        val formatter = SimpleDateFormat("MMM dd, yyy", Locale.getDefault())
        return formatter.format(timeStamp)
    }
}