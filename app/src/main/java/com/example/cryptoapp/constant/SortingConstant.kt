package com.example.cryptoapp.constant

import com.example.cryptoapp.cache.Cache
import com.example.cryptoapp.model.allcryptocurrencies.CryptoCurrency

object SortingConstant {
    fun <R: Comparable<R>> sortCryptoCurrencies(field: (CryptoCurrency) -> R, isDescending: Boolean): List<CryptoCurrency> {
        val currencies = Cache.getCryptoCurrencies()
        return when(isDescending){
            true -> {
                currencies.sortedByDescending(field)
            }
            else -> {
                currencies.sortedBy(field)
            }
        }
    }
}