package com.example.cryptoapp.fragment.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.cryptoapp.MainActivity
import com.example.cryptoapp.R
import com.example.cryptoapp.fragment.cryptocurrencies.CryptoCurrencyFragment

class LoginFragment : Fragment() {
    private lateinit var loginButton : Button
    private lateinit var signUp : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        bindUI(view)
        initUI()

        return view
    }

    private fun bindUI(view: View){
        loginButton = view.findViewById(R.id.login_button)
        signUp = view.findViewById(R.id.sign_up)
    }

    private fun initUI(){
        loginButton.setOnClickListener {
            (activity as MainActivity).replaceFragment(CryptoCurrencyFragment(), R.id.activity_fragment_container, withAnimation = false)
        }
        signUp.setOnClickListener {
            (activity as MainActivity).replaceFragment(SignUpFragment(), R.id.activity_fragment_container, withAnimation = false, addToBackStack = true)
        }
    }
}