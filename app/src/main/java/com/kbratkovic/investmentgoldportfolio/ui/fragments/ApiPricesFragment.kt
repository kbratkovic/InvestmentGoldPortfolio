package com.kbratkovic.investmentgoldportfolio.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kbratkovic.investmentgoldportfolio.R
import com.kbratkovic.investmentgoldportfolio.domain.models.MetalPriceApiCom
import com.kbratkovic.investmentgoldportfolio.ui.MainViewModel
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.CURRENCY_USD_CODE
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.GOLD_CODE
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.CONVERT_TROY_OUNCE_CODE
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.CURRENCY_EUR_CODE
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.WEIGHT_GRAM_CODE
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.WEIGHT_TROY_OUNCE_CODE
import com.kbratkovic.investmentgoldportfolio.util.Resource
import timber.log.Timber
import java.text.DateFormat
import java.text.NumberFormat
import java.util.*


class ApiPricesFragment : Fragment() {

    private lateinit var textViewTimeAndDate: TextView
    private lateinit var textViewMetalCurrentPrice: TextView

    private lateinit var autoCompleteTextViewWeight : AutoCompleteTextView
    private lateinit var autoCompleteTextViewCurrency : AutoCompleteTextView

    private lateinit var pricesContainer: ConstraintLayout

    private lateinit var selectedMetal: String
    private lateinit var selectedWeight: String
    private lateinit var selectedCurrency: String

    private var mLocaleUS =  NumberFormat.getCurrencyInstance(Locale.US)
    private var mLocaleEUR =  NumberFormat.getCurrencyInstance(Locale.GERMANY)

    private var mPriceOfOneOztOfGoldInUSD = 0.0
    private var mPriceOfOneOztOfGoldInEUR = 0.0
    private var mPriceOfOneGramOfGoldInUSD = 0.0
    private var mPriceOfOneGramOfGoldInEUR = 0.0
    private var mExchangeRateUSD = 0.0
    private var mExchangeRateEUR = 0.0

    private val mMainViewModel: MainViewModel by activityViewModels()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_api_prices, container, false)
    } // end onCreateView


    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeLayoutViews(view)
//        startOnDataChangeListener()
        setDefaultValueInDropDownMenu()
        observeCurrentGoldPriceChangeFromMetalPriceApiCom()
    } // onViewCreated


    override fun onResume() {
        super.onResume()
        handleDropDownMenus()
        setDefaultValueInDropDownMenu()
    }


    private fun initializeLayoutViews(view: View) {
        textViewTimeAndDate = view.findViewById(R.id.time_and_date)

        autoCompleteTextViewWeight = view.findViewById(R.id.auto_complete_text_view_weight)
        autoCompleteTextViewCurrency = view.findViewById(R.id.auto_complete_text_view_currency)

        pricesContainer = view.findViewById(R.id.prices_container)
        textViewMetalCurrentPrice = view.findViewById(R.id.metal_current_price)

    }


//    private fun startOnDataChangeListener() {
//        mMainViewModel.setOnDataChangeListener(object: MainViewModel.OnDataChangeListener {
//            override fun onDataChanged(message: String?) {
//                if (message.equals(getString(R.string.network_error))) {
//                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
//                }
//            }
//        })
//    }


    private fun observeCurrentGoldPriceChangeFromMetalPriceApiCom() {
        mMainViewModel.currentGoldPriceFromMetalPriceApiCom.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { metalPrice ->
                        showPricesContainer()
                        displayDateAndTime(metalPrice)
                        calculateExchangeRatesAndGoldPrices(metalPrice)

                        when (selectedCurrency) {
                            CURRENCY_USD_CODE -> {
                                textViewMetalCurrentPrice.text = mLocaleUS.format(getString(R.string.price_holder).toInt())
                                if (selectedWeight == WEIGHT_TROY_OUNCE_CODE)
                                    displayPricesInUSDAndTroyOunce()
                                if (selectedWeight == WEIGHT_GRAM_CODE)
                                    displayPricesInUSDAndGrams()
                            }
                            CURRENCY_EUR_CODE -> {
                                textViewMetalCurrentPrice.text = mLocaleEUR.format(getString(R.string.price_holder).toInt())
                                if (selectedWeight == WEIGHT_TROY_OUNCE_CODE)
                                    displayPricesInEURAndTroyOunce()
                                if (selectedWeight == WEIGHT_GRAM_CODE)
                                    displayPricesInEURAndGrams()
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
                        toast.show()
                        Timber.e(message)
                    }
                }
                is Resource.Loading -> {
                }
            }
        }
    } // observeCurrentGoldPriceChangeFromMetalPriceApiCom


    private fun calculateExchangeRatesAndGoldPrices(metalPrice: MetalPriceApiCom) {
        mExchangeRateUSD = (1).div(metalPrice.rates.EUR)
        mExchangeRateEUR = metalPrice.rates.EUR

        mPriceOfOneOztOfGoldInUSD = (1).div(metalPrice.rates.XAU)
        mPriceOfOneOztOfGoldInEUR = (1).div(metalPrice.rates.XAU).times(mExchangeRateEUR)
        mPriceOfOneGramOfGoldInUSD = mPriceOfOneOztOfGoldInUSD.div(CONVERT_TROY_OUNCE_CODE)
        mPriceOfOneGramOfGoldInEUR = mPriceOfOneOztOfGoldInEUR.div(CONVERT_TROY_OUNCE_CODE)
    }

    private fun displayDateAndTime(metalPrice: MetalPriceApiCom) {
        textViewTimeAndDate.text =
            getString(R.string.time_and_date, formatDateAndTime(metalPrice))
    }


    private fun formatDateAndTime(goldPriceResponse: MetalPriceApiCom) : String {
        val date = Date(goldPriceResponse.timestamp.toLong() * 1000)
        val dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.US)
        val timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.US)
        val formattedDate = dateFormat.format(date)
        val formattedTime = timeFormat.format(date)
        return "$formattedDate, $formattedTime"
    }


    private fun handleDropDownMenus() {
        val currencyDropdownList = resources.getStringArray(R.array.currency_items)
        val weightDropdownList = resources.getStringArray(R.array.weight_items)

        val arrayAdapterWeight = ArrayAdapter(requireContext(), R.layout.item_menu_dropdown, weightDropdownList)
        autoCompleteTextViewWeight.setAdapter(arrayAdapterWeight)

        autoCompleteTextViewWeight.onItemClickListener = object: AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 >= 0) {
                    selectedWeight = p0?.getItemAtPosition(p2) as String

                    when (selectedWeight) {
                        WEIGHT_GRAM_CODE -> {
                            if (selectedCurrency == CURRENCY_EUR_CODE)
                                displayPricesInEURAndGrams()
                            if (selectedCurrency == CURRENCY_USD_CODE)
                                displayPricesInUSDAndGrams()
                        }
                        WEIGHT_TROY_OUNCE_CODE -> {
                            if (selectedCurrency == CURRENCY_EUR_CODE)
                                displayPricesInEURAndTroyOunce()
                            if (selectedCurrency == CURRENCY_USD_CODE)
                                displayPricesInEURAndGrams()
                        }
                    }
                }
            }
        }


        val arrayAdapterCurrency = ArrayAdapter(requireContext(), R.layout.item_menu_dropdown, currencyDropdownList)
        autoCompleteTextViewCurrency.setAdapter(arrayAdapterCurrency)

        autoCompleteTextViewCurrency.onItemClickListener = object: AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 >= 0) {
                    selectedCurrency = p0?.getItemAtPosition(p2) as String

                    when (selectedCurrency) {
                        CURRENCY_EUR_CODE -> {
                            if (selectedWeight == WEIGHT_GRAM_CODE)
                                displayPricesInEURAndGrams()
                            if (selectedWeight == WEIGHT_TROY_OUNCE_CODE)
                                displayPricesInEURAndTroyOunce()
                        }
                        CURRENCY_USD_CODE -> {
                            if (selectedWeight == WEIGHT_GRAM_CODE)
                                displayPricesInUSDAndGrams()
                            if (selectedWeight == WEIGHT_TROY_OUNCE_CODE)
                                displayPricesInUSDAndTroyOunce()
                        }
                    }
                }
            }
        }
    } // handleDropDownMenus


    private fun displayPricesInUSDAndTroyOunce() {
        textViewMetalCurrentPrice.text = mLocaleUS.format(mPriceOfOneOztOfGoldInUSD)
    }


    private fun displayPricesInEURAndTroyOunce() {
        textViewMetalCurrentPrice.text = mLocaleEUR.format(mPriceOfOneOztOfGoldInEUR)
    }


    private fun displayPricesInUSDAndGrams() {
        textViewMetalCurrentPrice.text = mLocaleUS.format(mPriceOfOneGramOfGoldInUSD)
    }


    private fun displayPricesInEURAndGrams() {
        textViewMetalCurrentPrice.text = mLocaleEUR.format(mPriceOfOneGramOfGoldInEUR)
    }


    private fun showPricesContainer() {
        pricesContainer.visibility = View.VISIBLE
    }


    private fun setDefaultValueInDropDownMenu() {
        selectedMetal = GOLD_CODE
        selectedWeight = WEIGHT_TROY_OUNCE_CODE
        selectedCurrency = CURRENCY_USD_CODE
        autoCompleteTextViewCurrency.setText(CURRENCY_USD_CODE, false)
        autoCompleteTextViewWeight.setText(WEIGHT_TROY_OUNCE_CODE, false)
    }


}