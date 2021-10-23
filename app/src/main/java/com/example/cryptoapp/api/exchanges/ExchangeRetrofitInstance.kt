package com.example.cryptoapp.api.exchanges
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ExchangeRetrofitInstance {
    private const val BASE_URL = "https://api.coingecko.com/api/v3/"

    private val retrofit by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
    }

    val api : ExchangeAPI by lazy {
        retrofit.create(ExchangeAPI::class.java)
    }
}