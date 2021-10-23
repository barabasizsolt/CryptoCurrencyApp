package com.example.cryptoapp.api.exchanges

import com.example.cryptoapp.model.exchanges.Exchange
import retrofit2.Response

class ExchangeApiRepository {
    suspend fun getAllExchanges(perPage : Int, page : String) : Response<List<Exchange>> {
        return ExchangeRetrofitInstance.api.getExchanges(perPage = perPage, page = page)
    }
}