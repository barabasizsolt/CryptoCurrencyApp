package com.example.cryptoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.cryptoapp.api.cryptocurrencies.CryptoApiRepository
import com.example.cryptoapp.api.cryptocurrencies.CryptoApiViewModel
import com.example.cryptoapp.cache.Cache
import com.example.cryptoapp.fragment.CryptoCurrencyFragment
import com.example.cryptoapp.model.allcryptocurrencies.AllCryptoCurrencies
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: CryptoApiViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            Log.d("Currencies", response.body()?.data.toString())
            response.body()?.data?.let { Cache.setCryptoCurrencies(it.coins) }
            initBottomNavigation()
            replaceFragment(CryptoCurrencyFragment(), R.id.activity_fragment_container, withAnimation = false)
        }
    }

    private fun initBottomNavigation(){
        bottomNavigation.setOnItemSelectedListener {item ->
            when(item.itemId) {
                R.id.currencies -> {
                    replaceFragment(CryptoCurrencyFragment(), R.id.activity_fragment_container, withAnimation = false)
                    true
                }
                R.id.favorites -> {
                    //TODO: implement it
                    true
                }
                R.id.news -> {
                    //TODO: implement it
                    true
                }
                else -> false
            }
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