package com.example.cryptoapp.constant

import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.cryptoapp.R
import com.google.android.material.chip.ChipGroup
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import android.icu.util.CurrencyAmount
import android.icu.text.CompactDecimalFormat.CompactStyle
import android.icu.text.CompactDecimalFormat

object Constant {
    private const val COUNTRY = "US"
    private const val LANGUAGE = "en"
    private val format : CompactDecimalFormat = CompactDecimalFormat.getInstance(Locale.US, CompactStyle.SHORT)
    private val numberFormat : NumberFormat = NumberFormat.getCurrencyInstance(Locale(LANGUAGE, COUNTRY))
    private val currency = Currency.getInstance(Locale.US)

    init {
        format.maximumFractionDigits = 2
    }

    const val COIN_ID : String = "coin_id"

    const val HOUR24 = "24h"
    const val DAY7 = "7d"
    const val YEAR1 = "1y"
    const val YEAR6 = "5y"

    const val PRICE_FIELD = "price"
    const val VOLUME_FIELD = "24hVolume"
    const val MARKET_CAP_FIELD = "marketCap"
    const val ASC = "asc"
    const val DESC = "desc"
    const val OFFSET = 0
    const val LIMIT = 50

    const val checkedItem = 1

    val filterTags = arrayOf(
        "defi",
        "stablecoin",
        "nft",
        "dex",
        "exchange",
        "staking",
        "dao",
        "meme",
        "privacy"
    )

    val sortingTags = arrayOf(
        "Highest Price",
        "Lowest Price",
        "Highest 24H Volume",
        "Lowest 24H Volume",
        "Highest Market Cap",
        "Lowest Market Cap"
    )

    val sortingParams = arrayOf(
        Pair(PRICE_FIELD, DESC),
        Pair(PRICE_FIELD, ASC),
        Pair(VOLUME_FIELD, DESC),
        Pair(VOLUME_FIELD, ASC),
        Pair(MARKET_CAP_FIELD, DESC),
        Pair(MARKET_CAP_FIELD, ASC),
    )

    fun ImageView.loadSvg(url: String) {
        val imageLoader = ImageLoader.Builder(this.context)
            .componentRegistry { add(SvgDecoder(this@loadSvg.context)) }
            .build()
        val request = ImageRequest.Builder(this.context)
            .data(url)
            .target(this)
            .build()
        imageLoader.enqueue(request)
    }

    fun setPercentage(percentage : Double, textView: TextView){
        when{
            percentage < 0 -> {
                val percentageText = String.format("%.2f", percentage) + "%"
                textView.text = percentageText
                textView.setTextColor(ContextCompat.getColor(textView.context, R.color.red))
            }
            percentage > 0 -> {
                val percentageText = "+" + String.format("%.2f", percentage) + "%"
                textView.text = percentageText
                textView.setTextColor(ContextCompat.getColor(textView.context, R.color.green))
            }
        }
    }

    fun getTime(timeStamp : Long): LocalDateTime = Instant.ofEpochSecond(timeStamp).atZone(ZoneId.systemDefault()).toLocalDateTime()

    fun ChipGroup.setChildrenEnabled(enable: Boolean) {
        children.forEach { it.isEnabled = enable }
    }

    fun setValue(inputNumber: Double) : String {
        return format.format(inputNumber)
    }

    fun setPrice(inputNumber: Double) : String {
        return numberFormat.format(inputNumber)
    }

    fun setCompactPrice(inputNumber: Double) : String {
        val amount = CurrencyAmount(inputNumber, currency)
        return format.format(amount)
    }
}