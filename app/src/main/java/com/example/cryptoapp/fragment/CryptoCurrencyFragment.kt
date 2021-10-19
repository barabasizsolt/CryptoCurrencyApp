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
import com.example.cryptoapp.constant.Constant.CHECKED_ITEM_INDEX
import com.example.cryptoapp.constant.Constant.LIMIT
import com.example.cryptoapp.constant.Constant.OFFSET
import com.example.cryptoapp.constant.Constant.filterTags
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
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var cryptoCurrencyAdapter: CryptoCurrencyAdapter
    private lateinit var chipGroup : ChipGroup
    private lateinit var chipTags : Chip
    private lateinit var chipSortBy : Chip

    private var isLoading : Boolean = true
    private var pastVisibleItems = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var checkedItemIndex = CHECKED_ITEM_INDEX
    private var sortingParam : Pair<String, String> = sortingParams[checkedItemIndex]
    private var currentOffset = OFFSET
    private val tags : MutableSet<String> = mutableSetOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crypto_currency, container, false)

        bindUI(view)
        initUI()
        initChipTags()
        initChipSortBy()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.allCryptoCurrenciesResponse.removeObserver(currenciesObserver)
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
        chipTags = view.findViewById(R.id.chip_tags)
        chipSortBy = view.findViewById(R.id.chip_sort_by)
    }

    private fun initUI(){
        viewModel = CryptoApiViewModel(CryptoApiRepository())
        viewModel.allCryptoCurrenciesResponse.observe(requireActivity(), currenciesObserver)
        linearLayoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = linearLayoutManager
        cryptoCurrencyAdapter = CryptoCurrencyAdapter(Cache.getCryptoCurrencies(), this, this)
        recyclerView.adapter = cryptoCurrencyAdapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = linearLayoutManager.childCount
                    totalItemCount = linearLayoutManager.itemCount
                    pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition()
                    if (isLoading) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            isLoading = false
                            currentOffset += LIMIT
                            viewModel.getAllCryptoCurrencies(sortingParam.first, sortingParam.second, currentOffset)
                            Log.v("End", currentOffset.toString())
                        }
                    }
                }
            }
        })
    }

    private fun initChipTags(){
        chipTags.setOnClickListener {
            val checkedItems = BooleanArray(filterTags.size){false}
            tags.forEach { tag ->
                val index = filterTags.indexOf(tag)
                checkedItems[index] = true
            }

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.tags_title))
                .setNeutralButton(resources.getString(R.string.cancel)) { _, _ -> }
                .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                    tags.clear()
                    for(i in checkedItems.indices){
                        if(checkedItems[i]){
                            tags.add(filterTags[i])
                        }
                    }
                    Log.d("checkedItems", tags.toString())
                    currentOffset = OFFSET
                    viewModel.getAllCryptoCurrencies(sortingParam.first, sortingParam.second, OFFSET, tags)
                }
                .setMultiChoiceItems(filterTags, checkedItems) { _, which, checked ->

                    checkedItems[which] = checked
                }
                .show()
        }
    }

    private fun initChipSortBy(){
        chipSortBy.setOnClickListener {
            sortingParam = sortingParams[checkedItemIndex]

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.sorting_title))
                .setNeutralButton(resources.getString(R.string.cancel)) { _, _ -> }
                .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                    currentOffset = OFFSET
                    viewModel.getAllCryptoCurrencies(sortingParam.first, sortingParam.second, OFFSET, tags)
                }
                .setSingleChoiceItems(sortingTags, checkedItemIndex) { _, which ->
                    sortingParam = sortingParams[which]
                    checkedItemIndex = which
                }
                .show()
        }
    }

    private val currenciesObserver = androidx.lifecycle.Observer<Response<AllCryptoCurrencies>> { response ->
        if (response.isSuccessful) {
            Log.d("Observed", response.body()?.data?.coins.toString())
            val currencies = response.body()?.data?.coins as MutableList<CryptoCurrency>
            if(currentOffset == OFFSET) {
                Cache.setCryptoCurrencies(currencies)
                cryptoCurrencyAdapter.resetData(currencies)
            }
            else{
                Cache.setCryptoCurrencies(Cache.getCryptoCurrencies().plus(currencies) as MutableList<CryptoCurrency>)
                cryptoCurrencyAdapter.addData(currencies)
                isLoading = true
            }
        }
    }
}