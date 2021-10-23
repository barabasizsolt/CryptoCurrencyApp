package com.example.cryptoapp.fragment.exchanges

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptoapp.R
import com.example.cryptoapp.adapter.exchanges.ExchangeAdapter
import com.example.cryptoapp.api.exchanges.ExchangeApiRepository
import com.example.cryptoapp.api.exchanges.ExchangeApiViewModel
import com.example.cryptoapp.cache.Cache
import com.example.cryptoapp.interfaces.OnItemClickListener
import com.example.cryptoapp.interfaces.OnItemLongClickListener
import com.example.cryptoapp.model.exchanges.Exchange
import retrofit2.Response

class ExchangeFragment : Fragment(), OnItemClickListener, OnItemLongClickListener {
    private lateinit var viewModel: ExchangeApiViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var exchangeAdapter: ExchangeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exchange, container, false)

        bindUI(view)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.allExchangeResponse.removeObserver(exchangesObserver)
    }

    private fun bindUI(view: View){
        recyclerView = view.findViewById(R.id.recyclerview)
        viewModel = ExchangeApiViewModel(ExchangeApiRepository())
        viewModel.getAllExchanges()
        viewModel.allExchangeResponse.observe(requireActivity(), exchangesObserver)
    }

    private fun initUI(exchanges : MutableList<Exchange>){
        linearLayoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = linearLayoutManager
        exchangeAdapter = ExchangeAdapter(exchanges, this, this)
        recyclerView.adapter = exchangeAdapter
    }

    private val exchangesObserver = androidx.lifecycle.Observer<Response<List<Exchange>>> { response ->
        if(response.isSuccessful){
            Log.d("Exchanges", response.body().toString())
            Cache.setExchanges(response.body() as MutableList<Exchange>)
            initUI(response.body() as MutableList<Exchange>)
        }
    }

    override fun onItemClick(position: Int) {
        //TODO:implement it
    }

    override fun onItemLongClick(position: Int) {
        //TODO:implement it
    }
}