package com.example.cryptoapp.constant

import android.util.Log
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

object Constant {
    const val BILLION : Long = 1000000000
    const val MILLION : Long = 1000000
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
                val percentageText = "-" + String.format("%.2f", percentage) + "%"
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
}