package com.example.cryptoapp.fragment

import android.annotation.SuppressLint
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
import com.example.cryptoapp.cache.Cache
import com.example.cryptoapp.constant.Constant
import com.example.cryptoapp.constant.Constant.MARKET_CAP_FIELD
import com.example.cryptoapp.constant.Constant.PRICE_FIELD
import com.example.cryptoapp.constant.Constant.RANK_FIELD
import com.example.cryptoapp.constant.Constant.sortingParams
import com.example.cryptoapp.constant.Constant.sortingTags
import com.example.cryptoapp.constant.SortingConstant
import com.example.cryptoapp.constant.SortingConstant.sortCryptoCurrencies
import com.example.cryptoapp.interfaces.OnItemClickListener
import com.example.cryptoapp.interfaces.OnItemLongClickListener
import com.example.cryptoapp.model.allcryptocurrencies.CryptoCurrency
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_crypto_currency.*

class CryptoCurrencyFragment : Fragment(), OnItemClickListener, OnItemLongClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var cryptoCurrencyAdapter: CryptoCurrencyAdapter
    private lateinit var chipGroup : ChipGroup
    private lateinit var chipSortBy : Chip
    private var sortingParam : Pair<Boolean, String> = sortingParams[0]

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
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        cryptoCurrencyAdapter = CryptoCurrencyAdapter(Cache.getCryptoCurrencies(), this, this)
        recyclerView.adapter = cryptoCurrencyAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initChipSortBy(){
        chipSortBy.setOnClickListener {
            val checkedItem = 1

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.sorting_title))
                .setNeutralButton(resources.getString(R.string.cancel)) { _, _ -> }
                .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                    lateinit var orderedCurrencies : List<CryptoCurrency>
                    val isDescending = sortingParam.first
                    when(sortingParam.second){
                        PRICE_FIELD -> {
                            orderedCurrencies = sortCryptoCurrencies(CryptoCurrency::price, isDescending)
                        }
                        RANK_FIELD -> {
                            orderedCurrencies = sortCryptoCurrencies(CryptoCurrency::rank, isDescending)
                        }
                        MARKET_CAP_FIELD -> {
                            orderedCurrencies = sortCryptoCurrencies(CryptoCurrency::marketCap, isDescending)
                        }
                    }
                    cryptoCurrencyAdapter.setData(orderedCurrencies)
                    cryptoCurrencyAdapter.notifyDataSetChanged()
                }
                .setSingleChoiceItems(sortingTags, checkedItem) { _, which ->
                    Log.d("which", which.toString())
                    sortingParam = sortingParams[which]
                }
                .show()
        }
    }
}