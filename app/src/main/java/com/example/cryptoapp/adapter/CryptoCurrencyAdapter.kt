package com.example.cryptoapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptoapp.R
import com.example.cryptoapp.constant.Constant
import com.example.cryptoapp.constant.Constant.loadSvg
import com.example.cryptoapp.constant.Constant.setPercentage
import com.example.cryptoapp.interfaces.OnItemClickListener
import com.example.cryptoapp.interfaces.OnItemLongClickListener
import com.example.cryptoapp.model.allcryptocurrencies.CryptoCurrency

class CryptoCurrencyAdapter (private val mList: List<CryptoCurrency>, onItemClickListener: OnItemClickListener, onItemLongClickListener: OnItemLongClickListener)
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

        holder.currencyLogo.loadSvg(itemsViewModel.iconUrl)
        val price = "$" + String.format("%.2f", itemsViewModel.price.toDouble())
        val marketCap = "$" + String.format("%.2f", itemsViewModel.marketCap.toDouble().div(Constant.BILLION)) + "B"
        val volume = "$" + String.format("%.2f", itemsViewModel.volume.toDouble().div(Constant.BILLION)) + "B"

        holder.currencyName.text = itemsViewModel.name
        holder.currencySymbol.text = itemsViewModel.symbol
        holder.currencyValue.text = price
        setPercentage(itemsViewModel.change.toDouble(), holder.percentChange24H)
        holder.volume.text = volume
        holder.marketCap.text = marketCap
    }

    override fun getItemCount(): Int = mList.size

    class ViewHolder(itemView: View, onItemClickListener: OnItemClickListener, onItemLongClickListener: OnItemLongClickListener)
        : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener{

        private val mOnItemClickListener: OnItemClickListener = onItemClickListener
        private val mOnItemLongClickListener: OnItemLongClickListener = onItemLongClickListener

        var currencyLogo: ImageView = itemView.findViewById(R.id.crypto_logo)
        var currencyName: TextView = itemView.findViewById(R.id.crypto_name)
        var currencySymbol: TextView = itemView.findViewById(R.id.crypto_symbol)
        var currencyValue: TextView = itemView.findViewById(R.id.crypto_value)
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