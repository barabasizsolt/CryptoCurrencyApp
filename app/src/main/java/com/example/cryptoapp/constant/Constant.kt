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
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.math.round

object Constant {
    const val THOUSAND : Long = 100000L
    const val MILLION : Long = 1000000L
    const val BILLION : Long = 1000000000L
    const val TRILLION : Long = 1000000000000L

    const val COIN_ID : String = "coin_id"

    const val HOUR24 = "24h"
    const val DAY7 = "7d"
    const val YEAR1 = "1y"
    const val YEAR6 = "5y"

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

    fun convertNumberForTextView(number: Double): String {
        return when(number.toLong()){
            in 0 until THOUSAND -> {
                "%.2f".format(number)
            }
            in THOUSAND until MILLION -> {
                val fraction = calculateFraction(number, THOUSAND)
                fraction + "K"
            }
            in MILLION until BILLION -> {
                val fraction = calculateFraction(number, MILLION)
                fraction + "M"
            }
            else -> {
                val fraction = calculateFraction(number, BILLION)
                fraction + "B"
            }
        }
    }

    fun convertNumberToDollarValue(inputNumber: Double) : String {
        return "$" + convertNumberForTextView(inputNumber)
    }

    private fun calculateFraction(number: Double, divisor: Long): String {
        val truncate = (number * 10L + divisor / 2L) / divisor
        return "%.2f".format(truncate.toFloat() * 0.10f)
    }
}