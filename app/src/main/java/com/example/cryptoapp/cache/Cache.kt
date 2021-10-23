package com.example.cryptoapp.cache

import com.example.cryptoapp.model.allcryptocurrencies.CryptoCurrency
import com.example.cryptoapp.model.cryptocurrencydetail.CoinDetails
import com.example.cryptoapp.model.exchanges.Exchange

object Cache {
    private lateinit var cryptoCurrencies : MutableList<CryptoCurrency>
    private lateinit var cryptoCurrency : CoinDetails
    private lateinit var exchanges : MutableList<Exchange>

    fun setCryptoCurrencies(data : MutableList<CryptoCurrency>) {
        cryptoCurrencies = data
    }

    fun getCryptoCurrencies() = cryptoCurrencies

    fun setCryptoCurrency(data : CoinDetails) {
        cryptoCurrency = data
    }

    fun getCryptoCurrency() = cryptoCurrency

    fun setExchanges(data : MutableList<Exchange>) {
        exchanges = data
    }

    fun getExchanges() = exchanges
}