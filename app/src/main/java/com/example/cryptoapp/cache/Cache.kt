package com.example.cryptoapp.cache

import com.example.cryptoapp.model.allcryptocurrencies.CryptoCurrency
import com.example.cryptoapp.model.cryptocurrencydetail.CoinDetails

object Cache {
    private lateinit var cryptoCurrencies : MutableList<CryptoCurrency>
    private lateinit var cryptoCurrency : CoinDetails

    fun setCryptoCurrencies(data : MutableList<CryptoCurrency>) {
        cryptoCurrencies = data
    }

    fun getCryptoCurrencies() = cryptoCurrencies

    fun setCryptoCurrency(data : CoinDetails) {
        cryptoCurrency = data
    }

    fun getCryptoCurrency() = cryptoCurrency
}