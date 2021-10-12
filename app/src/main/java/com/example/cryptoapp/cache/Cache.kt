package com.example.cryptoapp.cache

import com.example.cryptoapp.model.allcryptocurrencies.CryptoCurrency
import com.example.cryptoapp.model.cryptocurrencydetail.CoinDetails

object Cache {
    private lateinit var cryptoCurrencies : List<CryptoCurrency>
    private lateinit var cryptoCurrency : CoinDetails

    fun setCryptoCurrencies(data : List<CryptoCurrency>) {
        cryptoCurrencies = data
    }

    fun getCryptoCurrencies() = cryptoCurrencies

    fun setCryptoCurrency(data : CoinDetails) {
        cryptoCurrency = data
    }

    fun getCryptoCurrency() = cryptoCurrency
}