package com.example.cryptoapp.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.anychart.anychart.*
import com.example.cryptoapp.R
import com.example.cryptoapp.api.cryptocurrencies.CryptoApiViewModel
import com.example.cryptoapp.constant.Constant
import com.example.cryptoapp.constant.Constant.DAY7
import com.example.cryptoapp.constant.Constant.HOUR24
import com.example.cryptoapp.constant.Constant.YEAR1
import com.example.cryptoapp.constant.Constant.getTime
import com.example.cryptoapp.constant.Constant.loadSvg
import com.example.cryptoapp.constant.Constant.setPercentage
import com.example.cryptoapp.model.cryptocurrencydetail.CryptoCurrencyDetails
import com.example.cryptoapp.model.cryptocurrencydetail.CryptoHistory
import com.google.android.material.chip.ChipGroup
import kotlin.collections.ArrayList
import com.anychart.anychart.AnyChart.area
import com.example.cryptoapp.MainActivity
import com.example.cryptoapp.cache.Cache
import com.example.cryptoapp.constant.Constant.YEAR6
import com.example.cryptoapp.constant.Constant.setCompactPrice
import com.example.cryptoapp.constant.Constant.setPrice
import com.example.cryptoapp.model.cryptocurrencydetail.CryptoCurrencyHistory
import com.google.android.material.tabs.TabLayout
import retrofit2.Response

class CryptoCurrencyDetailsFragment : Fragment() {
    private lateinit var areaChart : Cartesian
    private lateinit var cryptoLogo: ImageView
    private lateinit var cryptoName: TextView
    private lateinit var cryptoSymbol: TextView
    private lateinit var cryptoPrice: TextView
    private lateinit var cryptoValueSymbol: TextView
    private lateinit var percentageChange24H: TextView
    private lateinit var volume: TextView
    private lateinit var marketCap: TextView
    private lateinit var chipGroup: ChipGroup
    private lateinit var tabLayout: TabLayout
    private lateinit var viewModel : CryptoApiViewModel

    private var currentTimeFrame = HOUR24

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crypto_currency_details, container, false)

        bindUI(view)
        val cryptoCurrencyId = requireArguments().getString(Constant.COIN_ID)!!

        Log.d("ID", cryptoCurrencyId)

        viewModel.getCryptoCurrencyDetails(cryptoCurrencyId)
        viewModel.cryptoCurrencyDetails.observe(requireActivity(), cryptoDetailsObserver)

        viewModel.getCryptoCurrencyHistory(uuid = cryptoCurrencyId, timePeriod = HOUR24)
        viewModel.cryptoCurrencyHistory.observe(requireActivity(), cryptoHistoryObserver)

        initializeChipGroup(cryptoCurrencyId)
        initializeTabLayout()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.cryptoCurrencyHistory.removeObserver(cryptoHistoryObserver)
        viewModel.cryptoCurrencyDetails.removeObserver(cryptoDetailsObserver)
    }

    private val cryptoDetailsObserver  = androidx.lifecycle.Observer<Response<CryptoCurrencyDetails>> { response ->
        if(response.isSuccessful) {

            response.body()?.let { cryptoDetails ->
                Cache.setCryptoCurrency(cryptoDetails.data.coin)
                initUI(cryptoDetails)
                tabLayout.getTabAt(1)!!.select()
                tabLayout.getTabAt(0)!!.select()
            }
        }
    }

    private val cryptoHistoryObserver  = androidx.lifecycle.Observer<Response<CryptoCurrencyHistory>> { response ->
        if (response.isSuccessful) {
            when (currentTimeFrame) {
                HOUR24 -> {
                    val currencyHistory = createDataForAreaChart(response.body()?.data?.history!! as MutableList, HOUR24)
                    refreshChart(currencyHistory)
                }
                DAY7 -> {
                    val currencyHistory = createDataForAreaChart(response.body()?.data?.history!! as MutableList, DAY7)
                    refreshChart(currencyHistory)
                }
                YEAR1 -> {
                    val currencyHistory = createDataForAreaChart(response.body()?.data?.history!! as MutableList, YEAR1)
                    refreshChart(currencyHistory)
                }
                YEAR6 -> {
                    val currencyHistory = createDataForAreaChart(response.body()?.data?.history!! as MutableList, YEAR6)
                    refreshChart(currencyHistory)
                }
            }
        }
    }

    private fun bindUI(view: View){
        viewModel = (activity as MainActivity).getViewModel()
        initializeChart(view)
        tabLayout = view.findViewById(R.id.tab_layout)
        cryptoLogo = view.findViewById(R.id.crypto_logo)
        cryptoName = view.findViewById(R.id.crypto_name)
        cryptoSymbol = view.findViewById(R.id.crypto_symbol)
        cryptoPrice = view.findViewById(R.id.crypto_price)
        cryptoValueSymbol = view.findViewById(R.id.crypto_value_symbol)
        percentageChange24H = view.findViewById(R.id.percent_change_24h)
        volume = view.findViewById(R.id.volume)
        marketCap = view.findViewById(R.id.market_cap)
        chipGroup = view.findViewById(R.id.chip_group)
    }

    private fun initUI(cryptoCurrencyDetails: CryptoCurrencyDetails){
        val coin = cryptoCurrencyDetails.data.coin
        val price = setPrice(coin.price.toDouble())
        val currentTime = getTime(System.currentTimeMillis())
        var currentHour = currentTime.hour.toString()
        var currentMinute = currentTime.minute.toString()
        if(currentHour.toInt() < 10){
            currentHour = "0$currentHour"
        }
        if(currentMinute.toInt() < 10){
            currentMinute = "0$currentMinute"
        }
        val coinValueSymbol = coin.symbol + "/" + "USD" + " - AVG - " + currentHour + ":" + currentMinute
        val marketCapText = setCompactPrice(coin.marketCap.toDouble())
        val volumeText = setCompactPrice(coin.volume.toDouble())
        cryptoLogo.loadSvg(coin.iconUrl)
        cryptoName.text = coin.name
        cryptoSymbol.text = coin.symbol
        cryptoPrice.text = price
        cryptoValueSymbol.text = coinValueSymbol
        setPercentage(coin.change.toDouble(), percentageChange24H)
        volume.text = volumeText
        marketCap.text = marketCapText
    }

    private fun initializeChipGroup(cryptoCurrencyId : String){
        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                R.id.chip_24h -> {
                    Log.d("CH24", "Chipped")
                    viewModel.getCryptoCurrencyHistory(uuid = cryptoCurrencyId, timePeriod = HOUR24)
                    currentTimeFrame = HOUR24
                }
                R.id.chip_7d -> {
                    Log.d("CH7", "Chipped")
                    viewModel.getCryptoCurrencyHistory(uuid = cryptoCurrencyId, timePeriod = DAY7)
                    currentTimeFrame = DAY7
                }
                R.id.chip_1y -> {
                    Log.d("CH1", "Chipped")
                    viewModel.getCryptoCurrencyHistory(uuid = cryptoCurrencyId, timePeriod = YEAR1)
                    currentTimeFrame = YEAR1
                }
                R.id.chip_6y -> {
                    Log.d("CH3", "Chipped")
                    viewModel.getCryptoCurrencyHistory(uuid = cryptoCurrencyId, timePeriod = YEAR6)
                    currentTimeFrame = YEAR6
                }
            }
        }
    }

    private fun initializeTabLayout(){
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab!!.position){
                    0 -> (activity as MainActivity).replaceFragment(CryptoDetailsInfoFragment(), R.id.crypto_details_fragment_container, withAnimation = false)
                    1 -> {
                        //TODO:Implement it
                    }
                    2 -> {
                        //TODO:Implement it
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun createDataForAreaChart(history: MutableList<CryptoHistory>, timeFrame : String) : MutableList<DataEntry>{
        val currencyHistory : MutableList<DataEntry> = ArrayList()

        //TODO : make a fun for day7 and year1 (or simplify the whole thing)

        when(timeFrame){
            HOUR24 -> {
                val groupedHistory = sortedMapOf<Int, MutableList<Double>>()

                history.forEach { curr ->
                    val time = getTime(curr.timestamp).hour
                    if(!groupedHistory.containsKey(time)){
                        groupedHistory[time] = mutableListOf()
                    }
                    if(!curr.price.isNullOrBlank()){groupedHistory[time]?.add(curr.price.toDouble())}
                }

                groupedHistory.forEach { elem ->
                    currencyHistory.add(ValueDataEntry(elem.key.toString() + ":00", elem.value.maxOfOrNull { it }))
                }
            }
            DAY7 -> {
                val groupedHistory = mutableMapOf<String, MutableList<Double>>()

                history.sortWith(compareBy { getTime(it.timestamp).dayOfWeek.ordinal })
                history.forEach { curr ->
                    val dayOfWeek = getTime(curr.timestamp).dayOfWeek.name.substring(0, 3)
                    if(!groupedHistory.containsKey(dayOfWeek)){
                        groupedHistory[dayOfWeek] = mutableListOf()
                    }
                    if(!curr.price.isNullOrBlank()){groupedHistory[dayOfWeek]?.add(curr.price.toDouble())}
                }

                groupedHistory.forEach { elem ->
                    currencyHistory.add(ValueDataEntry(elem.key, elem.value.maxOfOrNull { it }))
                }
            }
            YEAR1 -> {
                val groupedHistory = mutableMapOf<String, MutableList<Double>>()

                history.sortWith(compareBy { getTime(it.timestamp).month.ordinal })
                history.forEach { curr ->
                    val month = getTime(curr.timestamp).month.name.substring(0, 3)
                    if(!groupedHistory.containsKey(month)){
                        groupedHistory[month] = mutableListOf()
                    }
                    if(!curr.price.isNullOrBlank()){groupedHistory[month]?.add(curr.price.toDouble())}
                }

                groupedHistory.forEach { elem -> currencyHistory.add(ValueDataEntry(elem.key, elem.value.maxOfOrNull { it })) }
            }
            YEAR6 -> {
                val groupedHistory = mutableMapOf<String, MutableList<Double>>()

                history.sortWith(compareBy { getTime(it.timestamp).year })
                history.forEach { curr ->
                    val year = getTime(curr.timestamp).year.toString()
                    if(!groupedHistory.containsKey(year)){
                        groupedHistory[year] = mutableListOf()
                    }
                    if(!curr.price.isNullOrBlank()){groupedHistory[year]?.add(curr.price.toDouble())}
                }

                groupedHistory.forEach { elem -> currencyHistory.add(ValueDataEntry(elem.key, elem.value.maxOfOrNull { it })) }
            }
        }

        return currencyHistory
    }

    private fun initializeChart(view: View){
        val anyChartView: AnyChartView = view.findViewById(R.id.any_chart_view)
        anyChartView.setBackgroundColor("#444444")

        areaChart = area()

        val crossHair: Crosshair = areaChart.crosshair
        crossHair.setEnabled(true)

        crossHair.setYStroke(null as Stroke?, null as Number?, null as String?, null as StrokeLineJoin?, null as StrokeLineCap?)
            .setXStroke("#fff", 1.0, null, null as StrokeLineJoin?, null as StrokeLineCap?)
            .setZIndex(39.0)
        crossHair.getYLabel(0).setEnabled(true)

        areaChart.yScale.setStackMode(ScaleStackMode.VALUE)
        areaChart.yGrid.setEnabled(true)
        areaChart.background.fill("#444444", 0)

        areaChart.legend.setEnabled(true)
        areaChart.legend.setFontSize(13.0)
        areaChart.legend.setPadding(0.0, 0.0, 20.0, 0.0)

        areaChart.getXAxis(0).setTitle(false)
        areaChart.getXAxis(0).labels.setFontColor("#fff")

        areaChart.getYAxis(0).labels.setFontColor("#fff")
        areaChart.getYAxis(0).labels.setFormat("\${%value}")
        areaChart.getYAxis(0).title.setFontColor("#fff")
        areaChart.getYAxis(0).setTitle("Value (in US Dollars)")

        areaChart.interactivity.setHoverMode(HoverMode.BY_X)
        areaChart.tooltip
            .setValuePrefix("$")
            .setDisplayMode(TooltipDisplayMode.SINGLE)

        anyChartView.setChart(areaChart)
    }

    private fun refreshChart(data: MutableList<DataEntry>){
        val series = areaChart.area(data)
        series.setName("Cryptocurrency History")
        series.setStroke("3 #fff")
        series.hovered.setStroke("3 #fff")
        series.hovered.markers.setEnabled(true)
        series.hovered.markers
            .setType(MarkerType.CIRCLE)
            .setSize(4.0)
            .setStroke("1.5 #fff")
        series.markers.setZIndex(100.0)
        series.fill("#C8C8C8", 5)
        areaChart.setData(data)
    }
}