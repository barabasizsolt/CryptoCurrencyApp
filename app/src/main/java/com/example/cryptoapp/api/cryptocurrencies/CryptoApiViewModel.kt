package com.example.cryptoapp.api.cryptocurrencies

import androidx.lifecycle.*
import androidx.lifecycle.MutableLiveData
import com.example.cryptoapp.model.allcryptocurrencies.AllCryptoCurrencies
import com.example.cryptoapp.model.cryptocurrencydetail.CryptoCurrencyDetails
import com.example.cryptoapp.model.cryptocurrencydetail.CryptoCurrencyHistory
import kotlinx.coroutines.launch
import retrofit2.Response

class CryptoApiViewModel(private val repository: CryptoApiRepository):ViewModel() {
    val allCryptoCurrenciesResponse: MutableLiveData<Response<AllCryptoCurrencies>> = MutableLiveData()
    val cryptoCurrencyHistory : MutableLiveData<Response<CryptoCurrencyHistory>> = MutableLiveData()
    val cryptoCurrencyDetails : MutableLiveData<Response<CryptoCurrencyDetails>> = MutableLiveData()

    fun getAllCryptoCurrencies(){
        viewModelScope.launch {
            val response = repository.getAllCryptoCurrencies()
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