package com.example.cryptoapp.api.cryptocurrencies

import androidx.lifecycle.*
import androidx.lifecycle.MutableLiveData
import com.example.cryptoapp.constant.Constant.ASC
import com.example.cryptoapp.constant.Constant.DESC
import com.example.cryptoapp.constant.Constant.MARKET_CAP_FIELD
import com.example.cryptoapp.model.allcryptocurrencies.AllCryptoCurrencies
import com.example.cryptoapp.model.cryptocurrencydetail.CryptoCurrencyDetails
import com.example.cryptoapp.model.cryptocurrencydetail.CryptoCurrencyHistory
import kotlinx.coroutines.launch
import retrofit2.Response

class CryptoApiViewModel(private val repository: CryptoApiRepository):ViewModel() {
    val allCryptoCurrenciesResponse: MutableLiveData<Response<AllCryptoCurrencies>> = MutableLiveData()
    val cryptoCurrencyHistory : MutableLiveData<Response<CryptoCurrencyHistory>> = MutableLiveData()
    val cryptoCurrencyDetails : MutableLiveData<Response<CryptoCurrencyDetails>> = MutableLiveData()

    fun getAllCryptoCurrencies(orderBy : String = MARKET_CAP_FIELD, orderDirection : String = DESC){
        viewModelScope.launch {
            val response = repository.getAllCryptoCurrencies(orderBy = orderBy, orderDirection = orderDirection)
            allCryptoCurrenciesResponse.value = response
        }
    }

    fun getCryptoCurrencyDetails(uuid : String){
        viewModelScope.launch {
            val response = repository.getCryptoCurrencyDetails(uuid = uuid)
            cryptoCurrencyDetails.value = response
        }
    }

    fun getCryptoCurrencyHistory(uuid : String, timePeriod : String){
        viewModelScope.launch {
            val response = repository.getCryptoCurrencyHistory(uuid = uuid, timePeriod = timePeriod)
            cryptoCurrencyHistory.value = response
        }
    }
}