package com.example.cryptoapp.fragment.login

import android.annotation.SuppressLint
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LoginFragment : Fragment() {
    private lateinit var progressBar: ProgressBar
    private lateinit var loginButton : Button
    private lateinit var signUp : TextView
    private lateinit var forgotPassword : TextView
    private lateinit var email : TextView
    private lateinit var password : TextView
    private lateinit var customDialogView: View
    private lateinit var resetEmail: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        customDialogView = inflater.inflate(R.layout.reset_password_dialog_layout, null, false)

        bindUI(view)
        initUI()

        return view
    }

    private fun bindUI(view: View){
        progressBar = view.findViewById(R.id.progress_bar)
        loginButton = view.findViewById(R.id.login_button)
        signUp = view.findViewById(R.id.sign_up)
        forgotPassword = view.findViewById(R.id.forgot_password)
        email = view.findViewById(R.id.email)
        password = view.findViewById(R.id.password)
        resetEmail = customDialogView.findViewById(R.id.reset_email)
    }

    private fun initUI(){
        loginButton.setOnClickListener {
            if(validateInput()){
                progressBar.visibility = View.VISIBLE

                (activity as MainActivity).mAuth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                    .addOnCompleteListener(
                        requireActivity()
                    ) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                requireActivity(),
                                "Successfully logged in",
                                Toast.LENGTH_LONG
                            ).show()
                            (activity as MainActivity).replaceFragment(
                                CryptoCurrencyFragment(),
                                R.id.activity_fragment_container,
                                withAnimation = false
                            )
                        } else {
                            Toast.makeText(
                                requireActivity(),
                                "Login Failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        progressBar.visibility = View.INVISIBLE
                    }
            }
        }
        forgotPassword.setOnClickListener {
            if(customDialogView.parent != null){
                (customDialogView.parent as ViewGroup).removeView(customDialogView)
            }
            MaterialAlertDialogBuilder(requireContext())
                .setView(customDialogView)
                .setTitle(R.string.reset_password)
                .setNeutralButton(resources.getString(R.string.cancel)) { _, _ -> }
                .setPositiveButton(resources.getString(R.string.reset)) { _, _ ->
                    if(validateResetEmail()) {
                        progressBar.visibility = View.VISIBLE

                        (activity as MainActivity).mAuth.sendPasswordResetEmail(resetEmail.text.toString())
                            .addOnCompleteListener(requireActivity()) { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Reset link sent to your email",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Unable to send reset mail",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }

                                progressBar.visibility = View.INVISIBLE
                            }
                    }
                }
                .show()
        }
        signUp.setOnClickListener {
            (activity as MainActivity).replaceFragment(SignUpFragment(), R.id.activity_fragment_container, withAnimation = false, addToBackStack = true)
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
        }
        return true
    }

    private fun validateResetEmail(): Boolean{
        when{
            resetEmail.text.toString().isEmpty() -> {
                Toast.makeText(
                    requireContext(),
                    "Missing email address",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
        }
        return true
    }
}