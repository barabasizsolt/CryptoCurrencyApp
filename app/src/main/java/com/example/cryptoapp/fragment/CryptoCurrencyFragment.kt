package com.example.cryptoapp.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptoapp.MainActivity
import com.example.cryptoapp.R
import com.example.cryptoapp.adapter.CryptoCurrencyAdapter
import com.example.cryptoapp.api.cryptocurrencies.CryptoApiRepository
import com.example.cryptoapp.api.cryptocurrencies.CryptoApiViewModel
import com.example.cryptoapp.cache.Cache
import com.example.cryptoapp.constant.Constant
import com.example.cryptoapp.constant.Constant.checkedItem
import com.example.cryptoapp.constant.Constant.sortingParams
import com.example.cryptoapp.constant.Constant.sortingTags
import com.example.cryptoapp.interfaces.OnItemClickListener
import com.example.cryptoapp.interfaces.OnItemLongClickListener
import com.example.cryptoapp.model.allcryptocurrencies.AllCryptoCurrencies
import com.example.cryptoapp.model.allcryptocurrencies.CryptoCurrency
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Response

class CryptoCurrencyFragment : Fragment(), OnItemClickListener, OnItemLongClickListener {
    private lateinit var viewModel: CryptoApiViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var cryptoCurrencyAdapter: CryptoCurrencyAdapter
    private lateinit var chipGroup : ChipGroup
    private lateinit var chipSortBy : Chip
    private var sortingParam : Pair<String, String> = sortingParams[checkedItem]

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crypto_currency, container, false)

        bindUI(view)
        initUI()
        initChipSortBy()

        return view
    }

    override fun onItemClick(position: Int) {
        val currentCryptoCurrency = Cache.getCryptoCurrencies()[position]
        val fragment = CryptoCurrencyDetailsFragment()
        val bundle = Bundle()
        bundle.putString(Constant.COIN_ID, currentCryptoCurrency.uuid)
        fragment.arguments = bundle
        (activity as MainActivity).replaceFragment(fragment, R.id.activity_fragment_container, addToBackStack = true, withAnimation = false)
    }

    override fun onItemLongClick(position: Int) {
        Log.d("OnLongClick", Cache.getCryptoCurrencies()[position].toString())
    }

    private fun bindUI(view: View){
        recyclerView = view.findViewById(R.id.recyclerview)
        chipGroup = view.findViewById(R.id.chip_group)
        chipSortBy = view.findViewById(R.id.chip_sort_by)
    }

    private fun initUI(){
        viewModel = CryptoApiViewModel(CryptoApiRepository())
        viewModel.allCryptoCurrenciesResponse.observe(requireActivity(), currenciesObserver)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        cryptoCurrencyAdapter = CryptoCurrencyAdapter(Cache.getCryptoCurrencies(), this, this)
        recyclerView.adapter = cryptoCurrencyAdapter
    }

    private fun initChipSortBy(){
        chipSortBy.setOnClickListener {
            sortingParam = sortingParams[checkedItem]

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.sorting_title))
                .setNeutralButton(resources.getString(R.string.cancel)) { _, _ -> }
                .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                    val sortingProperty = sortingParam.first
                    val isDescending = sortingParam.second
                    viewModel.getAllCryptoCurrencies(sortingProperty, isDescending)
                }
                .setSingleChoiceItems(sortingTags, checkedItem) { _, which ->
                    sortingParam = sortingParams[which]
                }
                .show()
        }
    }

    private val currenciesObserver = androidx.lifecycle.Observer<Response<AllCryptoCurrencies>> { response ->
        if (response.isSuccessful) {
            Log.d("Observed", response.body()?.data?.coins.toString())
            val currencies = response.body()?.data?.coins as MutableList<CryptoCurrency>
            Cache.setCryptoCurrencies(currencies)
            cryptoCurrencyAdapter.resetData(currencies)
        }
    }
}