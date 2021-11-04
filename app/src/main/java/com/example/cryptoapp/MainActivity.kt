package com.example.cryptoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.cryptoapp.api.cryptocurrencies.CryptoApiRepository
import com.example.cryptoapp.api.cryptocurrencies.CryptoApiViewModel
import com.example.cryptoapp.cache.Cache
import com.example.cryptoapp.fragment.cryptocurrencies.CryptoCurrencyFragment
import com.example.cryptoapp.fragment.exchanges.ExchangeFragment
import com.example.cryptoapp.fragment.login.LoginFragment
import com.example.cryptoapp.model.allcryptocurrencies.AllCryptoCurrencies
import com.example.cryptoapp.model.allcryptocurrencies.CryptoCurrency
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: CryptoApiViewModel
    private lateinit var navHeader: View
    private lateinit var userLogo: ImageView
    private lateinit var userEmail: TextView
    lateinit var mAuth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    lateinit var favoriteMenuItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        firestore = Firebase.firestore

        supportActionBar?.hide()
        topAppBar.visibility = View.GONE
        bottomNavigation.visibility = View.GONE
        favoriteMenuItem = topAppBar.menu[0]
        favoriteMenuItem.isVisible = false

        navHeader = navigationView.getHeaderView(0)
        userLogo = navHeader.findViewById(R.id.user_logo)
        userEmail = navHeader.findViewById(R.id.user_email)

        viewModel = CryptoApiViewModel(CryptoApiRepository())
        viewModel.getAllCryptoCurrencies()
        viewModel.allCryptoCurrenciesResponse.observe(this, mainObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.allCryptoCurrenciesResponse.removeObserver(mainObserver)
    }

    private val mainObserver = androidx.lifecycle.Observer<Response<AllCryptoCurrencies>> { response ->
        if (response.isSuccessful) {
            Log.d("Observed", response.body()?.data.toString())
            response.body()?.data?.let { Cache.setCryptoCurrencies(it.coins as MutableList<CryptoCurrency>) }
            initBottomNavigation()

            if(mAuth.currentUser == null){
                replaceFragment(LoginFragment(), R.id.activity_fragment_container)
            }else{
                initModalNavigationDrawer()
                replaceFragment(CryptoCurrencyFragment(), R.id.activity_fragment_container)
            }
        }
    }

    private fun initBottomNavigation(){
        bottomNavigation.setOnItemSelectedListener {item ->
            when(item.itemId) {
                R.id.currencies -> {
                    replaceFragment(CryptoCurrencyFragment(), R.id.activity_fragment_container)
                    true
                }
                R.id.exchanges -> {
                    replaceFragment(ExchangeFragment(), R.id.activity_fragment_container)
                    true
                }
                R.id.favorites -> {
                    //TODO: implement it
                    true
                }
                R.id.events -> {
                    //TODO: implement it
                    true
                }
                else -> false
            }
        }
    }

    fun initModalNavigationDrawer(){
        topAppBar.setNavigationOnClickListener {
            drawerLayout.open()
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            when(menuItem.itemId){
                R.id.profile -> {
                    //TODO: implement it
                }
                R.id.wallet -> {
                    //TODO: implement it
                }
                R.id.calculator -> {
                    //TODO: implement it
                }
                R.id.logout -> {
                    mAuth.signOut()
                    topAppBar.visibility = View.GONE
                    bottomNavigation.visibility = View.GONE
                    replaceFragment(LoginFragment(), R.id.activity_fragment_container)
                }
            }
            drawerLayout.close()
            true
        }

        if(mAuth.currentUser?.photoUrl != null){
            Glide.with(this).load(mAuth.currentUser?.photoUrl).circleCrop().into(userLogo)
        }
        else{
            Glide.with(this).load(R.drawable.avatar).circleCrop().into(userLogo)
        }
        userEmail.text = mAuth.currentUser?.email.toString()
    }

    fun replaceFragment(fragment: Fragment, containerId: Int, addToBackStack:Boolean = false, withAnimation:Boolean = false){
        val transaction = supportFragmentManager.beginTransaction()
        when(withAnimation){
            true -> {
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
            }
        }
        transaction.replace(containerId, fragment)
        when(addToBackStack){
            true -> {
                transaction.addToBackStack(null)
            }
        }
        transaction.commit()
    }

    fun getViewModel() = viewModel
}