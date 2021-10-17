package com.example.cryptoapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptoapp.R
import com.example.cryptoapp.constant.Constant.setCompactPrice
import com.example.cryptoapp.constant.Constant.loadSvg
import com.example.cryptoapp.constant.Constant.setPercentage
import com.example.cryptoapp.constant.Constant.setPrice
import com.example.cryptoapp.interfaces.OnItemClickListener
import com.example.cryptoapp.interfaces.OnItemLongClickListener
import com.example.cryptoapp.model.allcryptocurrencies.CryptoCurrency


class CryptoCurrencyAdapter (private val mList: MutableList<CryptoCurrency>, onItemClickListener: OnItemClickListener, onItemLongClickListener: OnItemLongClickListener)
    : RecyclerView.Adapter<CryptoCurrencyAdapter.ViewHolder>() {

    private val mOnItemClickListener: OnItemClickListener = onItemClickListener
    private val mOnItemLongClickListener: OnItemLongClickListener = onItemLongClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.crypto_element, parent, false)

        return ViewHolder(view, mOnItemClickListener, mOnItemLongClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemsViewModel = mList[position]

        itemsViewModel.iconUrl?.let { holder.currencyLogo.loadSvg(it) }
        val price = itemsViewModel.price?.let { setPrice(it.toDouble()) }
        val marketCap = itemsViewModel.marketCap?.let { setCompactPrice(it.toDouble()) }
        val volume = itemsViewModel.volume?.let { setCompactPrice(it.toDouble()) }

        holder.currencyName.text = itemsViewModel.name
        holder.currencySymbol.text = itemsViewModel.symbol
        holder.currencyValue.text = price
        itemsViewModel.change?.let { setPercentage(it.toDouble(), holder.percentChange24H) }
        holder.volume.text = volume
        holder.marketCap.text = marketCap
    }

    override fun getItemCount(): Int = mList.size

    @SuppressLint("NotifyDataSetChanged")
    fun resetData(data: MutableList<CryptoCurrency>) {
        mList.clear()
        mList.addAll(data)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View, onItemClickListener: OnItemClickListener, onItemLongClickListener: OnItemLongClickListener)
        : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener{

        private val mOnItemClickListener: OnItemClickListener = onItemClickListener
        private val mOnItemLongClickListener: OnItemLongClickListener = onItemLongClickListener

        val currencyLogo: ImageView = itemView.findViewById(R.id.crypto_logo)
        val currencyName: TextView = itemView.findViewById(R.id.crypto_name)
        val currencySymbol: TextView = itemView.findViewById(R.id.crypto_symbol)
        val currencyValue: TextView = itemView.findViewById(R.id.crypto_value)
        val percentChange24H: TextView = itemView.findViewById(R.id.percent_change_24h)
        val volume: TextView = itemView.findViewById(R.id.volume)
        val marketCap: TextView = itemView.findViewById(R.id.market_cap)

        override fun onClick(view: View) {
            mOnItemClickListener.onItemClick(bindingAdapterPosition)
        }

        override fun onLongClick(view: View): Boolean {
            mOnItemLongClickListener.onItemLongClick(bindingAdapterPosition)
            return true
        }

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }
    }
}