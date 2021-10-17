package com.example.cryptoapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.cryptoapp.R
import com.example.cryptoapp.cache.Cache
import com.example.cryptoapp.constant.Constant.setCompactPrice
import com.example.cryptoapp.constant.Constant.getTime
import com.example.cryptoapp.constant.Constant.setPrice
import com.example.cryptoapp.constant.Constant.setValue
import com.example.cryptoapp.model.cryptocurrencydetail.CoinDetails

class CryptoDetailsInfoFragment : Fragment() {
    private lateinit var rank : TextView
    private lateinit var supply : TextView
    private lateinit var circulating : TextView
    private lateinit var btcPrice : TextView
    private lateinit var allTimeHigh : TextView
    private lateinit var allTimeHighDate : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crypto_details_info, container, false)

        bindUI(view)
        initUI()

        return view
    }

    private fun bindUI(view: View){
        rank = view.findViewById(R.id.crypto_rank_value)
        supply = view.findViewById(R.id.crypto_supply_value)
        circulating = view.findViewById(R.id.crypto_circulating_value)
        btcPrice = view.findViewById(R.id.crypto_btc_rice_value)
        allTimeHigh = view.findViewById(R.id.crypto_all_time_high_value)
        allTimeHighDate = view.findViewById(R.id.crypto_all_time_high_date_value)
    }

    private fun initUI(){
        val coin = Cache.getCryptoCurrency()
        val allTimeHighText = setPrice(coin.allTimeHigh.price.toDouble())
        val allTimeHighDateText = getAllTimeHighDate(coin.allTimeHigh.timestamp)

        rank.text = coin.rank.toString()
        if(!coin.supply.total.isNullOrEmpty()){
            supply.text = setValue(coin.supply.total.toDouble())
        }
        if(!coin.supply.circulating.isNullOrBlank()){
            circulating.text = setValue(coin.supply.circulating.toDouble())
        }
        if(!coin.btcPrice.isNullOrBlank()){
            btcPrice.text = String.format("%.7f", coin.btcPrice.toDouble()) + " Btc"
        }
        allTimeHigh.text = allTimeHighText
        allTimeHighDate.text = allTimeHighDateText
    }

    private fun getAllTimeHighDate(timeStamp : Long) : String{
        val time = getTime(timeStamp)
        val month = time.month.name.lowercase().replaceFirstChar { it.uppercase() }.substring(0,3)
        return month + " " + time.dayOfMonth.toString() + ", " + time.year.toString()
    }
}