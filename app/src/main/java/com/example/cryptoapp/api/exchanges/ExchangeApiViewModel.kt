package com.example.cryptoapp.api.exchanges

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoapp.constant.exchanges.ExchangeConstant.PAGE
import com.example.cryptoapp.constant.exchanges.ExchangeConstant.PER_PAGE
import com.example.cryptoapp.model.exchanges.Exchange
import kotlinx.coroutines.launch
import retrofit2.Response

class ExchangeApiViewModel(private val repository: ExchangeApiRepository) : ViewModel() {
    val allExchangeResponse: MutableLiveData<Response<List<Exchange>>> = MutableLiveData()

    fun getAllExchanges(perPage : Int = PER_PAGE, page : String = PAGE){
        viewModelScope.launch {
            val response = repository.getAllExchanges(perPage = perPage, page = page)
            allExchangeResponse.value = response
        }
    }
}