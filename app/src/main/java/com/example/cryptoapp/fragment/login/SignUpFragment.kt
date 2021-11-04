package com.example.cryptoapp.fragment.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.cryptoapp.MainActivity
import com.example.cryptoapp.R
import com.example.cryptoapp.fragment.cryptocurrencies.CryptoCurrencyFragment
import com.google.android.gms.tasks.OnCompleteListener

class SignUpFragment : Fragment() {
    private lateinit var progressBar: ProgressBar
    private lateinit var signUpButton : Button
    private lateinit var email : TextView
    private lateinit var password : TextView
    private lateinit var confirmPassword : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)
        bindUI(view)
        initUI()
        return view
    }

    private fun bindUI(view: View){
        progressBar = view.findViewById(R.id.progress_bar)
        signUpButton = view.findViewById(R.id.sign_up_button)
        email = view.findViewById(R.id.email)
        password = view.findViewById(R.id.password)
        confirmPassword = view.findViewById(R.id.confirm_password)
    }

    private fun initUI(){
        signUpButton.setOnClickListener {
            if (validateInput()) {
                progressBar.visibility = View.VISIBLE

                (activity as MainActivity).mAuth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                    .addOnCompleteListener(requireActivity()
                    ) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Successfully Registered",
                                Toast.LENGTH_LONG
                            ).show()
                            val fm = (activity as MainActivity).supportFragmentManager
                            for (i in 0 until fm.backStackEntryCount) {
                                fm.popBackStack()
                            }
                            (activity as MainActivity).replaceFragment(
                                CryptoCurrencyFragment(),
                                R.id.activity_fragment_container,
                                withAnimation = false
                            )
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Registration Failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        progressBar.visibility = View.INVISIBLE
                    }
            }
        }
    }

    private fun validateInput(): Boolean {
        when{
            email.text.toString().isEmpty() -> {
                Toast.makeText(
                    requireContext(),
                    "Missing email address",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            password.text.toString().isEmpty() -> {
                Toast.makeText(
                    requireContext(),
                    "Missing password",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            password.text.toString() != confirmPassword.text.toString() -> {
                Toast.makeText(
                    requireContext(),
                    "Two password doesn't match",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
        }
        return true
    }
}