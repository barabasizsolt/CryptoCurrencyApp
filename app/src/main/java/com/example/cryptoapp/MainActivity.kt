package com.example.cryptoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
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

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: CryptoApiViewModel
    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        supportActionBar?.hide()
        topAppBar.visibility = View.GONE
        bottomNavigation.visibility = View.GONE

        viewModel = CryptoApiViewModel(CryptoApiRepository())
        viewModel.getAllCryptoCurrencies()
        viewModel.allCryptoCurrenciesResponse.observe(this, mainObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        //mAuth.signOut()
        viewModel.allCryptoCurrenciesResponse.removeObserver(mainObserver)
    }

    private val mainObserver = androidx.lifecycle.Observer<Response<AllCryptoCurrencies>> { response ->
        if (response.isSuccessful) {
            Log.d("Observed", response.body()?.data.toString())
            response.body()?.data?.let { Cache.setCryptoCurrencies(it.coins as MutableList<CryptoCurrency>) }
            initBottomNavigation()
            initModalNavigationDrawer()

            if(mAuth.currentUser == null){
                replaceFragment(LoginFragment(), R.id.activity_fragment_container, withAnimation = false)
            }else{
                replaceFragment(CryptoCurrencyFragment(), R.id.activity_fragment_container, withAnimation = false)
            }
        }
    }

    private fun initBottomNavigation(){
        bottomNavigation.setOnItemSelectedListener {item ->
            when(item.itemId) {
                R.id.currencies -> {
                    replaceFragment(CryptoCurrencyFragment(), R.id.activity_fragment_container, withAnimation = false)
                    true
                }
                R.id.exchanges -> {
                    replaceFragment(ExchangeFragment(), R.id.activity_fragment_container, withAnimation = false)
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

    private fun initModalNavigationDrawer(){
        topAppBar.setNavigationOnClickListener {
            drawerLayout.open()
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            when(menuItem.itemId){
                R.id.item1 -> {
                    //TODO: implement it
                }
                R.id.item2 -> {
                    //TODO: implement it
                }
                R.id.logout -> {
                    mAuth.signOut()
                    topAppBar.visibility = View.GONE
                    bottomNavigation.visibility = View.GONE
                    replaceFragment(LoginFragment(), R.id.activity_fragment_container, withAnimation = false)
                }
            }
            drawerLayout.close()
            true
        }

    }

    fun replaceFragment(fragment: Fragment, containerId: Int, addToBackStack:Boolean = false, withAnimation:Boolean = true){
        val transaction = supportFragmentManager.beginTransaction()
//        when(withAnimation){
//            true -> {
//                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
//            }
//        }
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