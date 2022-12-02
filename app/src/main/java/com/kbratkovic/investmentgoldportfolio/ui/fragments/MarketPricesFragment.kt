package com.kbratkovic.investmentgoldportfolio.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kbratkovic.investmentgoldportfolio.R
import com.kbratkovic.investmentgoldportfolio.domain.models.MetalPrice
import com.kbratkovic.investmentgoldportfolio.ui.MainViewModel
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.CURRENCY_USD_CODE
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.CONVERT_TROY_OUNCE_CODE
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.CURRENCY_EUR_CODE
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.WEIGHT_GRAM_CODE
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.WEIGHT_TROY_OUNCE_CODE
import com.kbratkovic.investmentgoldportfolio.util.NetworkConnection
import com.kbratkovic.investmentgoldportfolio.util.Resource
import com.kbratkovic.investmentgoldportfolio.util.Utils
import timber.log.Timber
import java.text.DateFormat
import java.text.NumberFormat
import java.util.*


class MarketPricesFragment : Fragment() {

    private lateinit var mTextViewTimeAndDate: TextView
    private lateinit var mTextViewGoldCurrentPrice: TextView
    private lateinit var mTextViewSilverCurrentPrice: TextView
    private lateinit var mTextViewPlatinumCurrentPrice: TextView
    private lateinit var mTextViewPalladiumCurrentPrice: TextView

    private lateinit var mAutoCompleteTextViewWeight : AutoCompleteTextView
    private lateinit var mAutoCompleteTextViewCurrency : AutoCompleteTextView

    private lateinit var mPricesContainer: ConstraintLayout

    private var mSelectedWeight = ""
    private var mSelectedCurrency = ""

    private var mLocaleUS =  NumberFormat.getCurrencyInstance(Locale.US)
    private var mLocaleEUR =  NumberFormat.getCurrencyInstance(Locale.GERMANY)

    private var mPriceOfOneOztOfGoldInUSD = 0.0
    private var mPriceOfOneOztOfGoldInEUR = 0.0
    private var mPriceOfOneGramOfGoldInUSD = 0.0
    private var mPriceOfOneGramOfGoldInEUR = 0.0

    private var mPriceOfOneOztOfSilverInUSD = 0.0
    private var mPriceOfOneOztOfSilverInEUR = 0.0
    private var mPriceOfOneGramOfSilverInUSD = 0.0
    private var mPriceOfOneGramOfSilverInEUR = 0.0

    private var mPriceOfOneOztOfPlatinumInUSD = 0.0
    private var mPriceOfOneOztOfPlatinumInEUR = 0.0
    private var mPriceOfOneGramOfPlatinumInUSD = 0.0
    private var mPriceOfOneGramOfPlatinumInEUR = 0.0

    private var mPriceOfOneOztOfPalladiumInUSD = 0.0
    private var mPriceOfOneOztOfPalladiumInEUR = 0.0
    private var mPriceOfOneGramOfPalladiumInUSD = 0.0
    private var mPriceOfOneGramOfPalladiumInEUR = 0.0

    private var mExchangeRateUSD = 0.0
    private var mExchangeRateEUR = 0.0

    private lateinit var sharedPreference: SharedPreferences

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

        getSharedPreference()
        initializeLayoutViews(view)
        getValuesFromSharedPreferences()
        displayDefaultZeroValues()
        observeCurrentGoldPriceChangeFromMetalPriceApiCom()

    } // onViewCreated


    override fun onResume() {
        super.onResume()

        handleDropDownMenus()
        setValueToDropDownMenu()
        displayCurrentMarketPrices()
        checkifHasInternetConnection()
    } // onResume


    override fun onPause() {
        super.onPause()
        putValuesToSharedPreferences()
    }


    private fun getSharedPreference() {
        sharedPreference =  requireContext().getSharedPreferences("PREFERENCE_NAME",
            Context.MODE_PRIVATE
        )
    }


    private fun getValuesFromSharedPreferences() {
        mSelectedCurrency = sharedPreference.getString("currency", mSelectedCurrency).toString()
        mSelectedWeight = sharedPreference.getString("weight", mSelectedWeight).toString()
    }


    private fun putValuesToSharedPreferences() {
        val editor = sharedPreference.edit()
        editor.putString("currency", mSelectedCurrency)
        editor.putString("weight", mSelectedWeight)
        editor.apply()
    }


    private fun setValueToDropDownMenu() {
        mAutoCompleteTextViewCurrency.setText(mSelectedCurrency, false)
        mAutoCompleteTextViewWeight.setText(mSelectedWeight, false)
    }


    private fun checkifHasInternetConnection() {
        if (!NetworkConnection.hasInternetConnection(requireContext())) {
            val bottomNavigationView: BottomNavigationView? = activity?.findViewById(R.id.bottom_navigation)
            if (bottomNavigationView != null) {
                Utils.showSnackBar(requireActivity().findViewById(android.R.id.content),
                    getString(R.string.error_network_connection), bottomNavigationView)
            }
        }
    }


    private fun initializeLayoutViews(view: View) {
        mTextViewTimeAndDate = view.findViewById(R.id.time_and_date)

        mAutoCompleteTextViewWeight = view.findViewById(R.id.auto_complete_text_view_weight)
        mAutoCompleteTextViewCurrency = view.findViewById(R.id.auto_complete_text_view_currency)

        mPricesContainer = view.findViewById(R.id.prices_container)

        mTextViewGoldCurrentPrice = view.findViewById(R.id.gold_current_price)
        mTextViewSilverCurrentPrice = view.findViewById(R.id.silver_current_price)
        mTextViewPlatinumCurrentPrice = view.findViewById(R.id.platinum_current_price)
        mTextViewPalladiumCurrentPrice = view.findViewById(R.id.palladium_current_price)

    } // initializeLayoutViews


    private fun observeCurrentGoldPriceChangeFromMetalPriceApiCom() {
        mMainViewModel.metalPrice.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { metalPrice ->
                        showPricesContainer()
                        displayDateAndTime(metalPrice)
                        calculateExchangeRatesAndGoldPrices(metalPrice)
                        displayCurrentMarketPrices()
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


    private fun calculateExchangeRatesAndGoldPrices(metalPrice: MetalPrice) {
        mExchangeRateUSD = (1).div(metalPrice.rates.EUR)
        mExchangeRateEUR = metalPrice.rates.EUR

        mPriceOfOneOztOfGoldInUSD = (1).div(metalPrice.rates.XAU)
        mPriceOfOneOztOfGoldInEUR = (1).div(metalPrice.rates.XAU).times(mExchangeRateEUR)
        mPriceOfOneGramOfGoldInUSD = mPriceOfOneOztOfGoldInUSD.div(CONVERT_TROY_OUNCE_CODE)
        mPriceOfOneGramOfGoldInEUR = mPriceOfOneOztOfGoldInEUR.div(CONVERT_TROY_OUNCE_CODE)

        mPriceOfOneOztOfSilverInUSD = (1).div(metalPrice.rates.XAG)
        mPriceOfOneOztOfSilverInEUR = (1).div(metalPrice.rates.XAG).times(mExchangeRateEUR)
        mPriceOfOneGramOfSilverInUSD = mPriceOfOneOztOfSilverInUSD.div(CONVERT_TROY_OUNCE_CODE)
        mPriceOfOneGramOfSilverInEUR = mPriceOfOneOztOfSilverInEUR.div(CONVERT_TROY_OUNCE_CODE)

        mPriceOfOneOztOfPlatinumInUSD = (1).div(metalPrice.rates.XPT)
        mPriceOfOneOztOfPlatinumInEUR = (1).div(metalPrice.rates.XPT).times(mExchangeRateEUR)
        mPriceOfOneGramOfPlatinumInUSD = mPriceOfOneOztOfPlatinumInUSD.div(CONVERT_TROY_OUNCE_CODE)
        mPriceOfOneGramOfPlatinumInEUR = mPriceOfOneOztOfPlatinumInEUR.div(CONVERT_TROY_OUNCE_CODE)

        mPriceOfOneOztOfPalladiumInUSD = (1).div(metalPrice.rates.XPD)
        mPriceOfOneOztOfPalladiumInEUR = (1).div(metalPrice.rates.XPD).times(mExchangeRateEUR)
        mPriceOfOneGramOfPalladiumInUSD = mPriceOfOneOztOfPalladiumInUSD.div(CONVERT_TROY_OUNCE_CODE)
        mPriceOfOneGramOfPalladiumInEUR = mPriceOfOneOztOfPalladiumInEUR.div(CONVERT_TROY_OUNCE_CODE)
    }

    private fun displayDateAndTime(metalPrice: MetalPrice) {
        mTextViewTimeAndDate.text =
            getString(R.string.time_and_date, formatDateAndTime(metalPrice))
    }


    private fun formatDateAndTime(metalPrice: MetalPrice) : String {
        val date = Date(metalPrice.timestamp.toLong() * 1000)
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
        mAutoCompleteTextViewWeight.setAdapter(arrayAdapterWeight)

        mAutoCompleteTextViewWeight.onItemClickListener = object: AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 >= 0) {
                    mSelectedWeight = p0?.getItemAtPosition(p2) as String

                    when (mSelectedWeight) {
                        WEIGHT_GRAM_CODE -> {
                            if (mSelectedCurrency == CURRENCY_EUR_CODE)
                                displayPricesInEURAndGrams()
                            if (mSelectedCurrency == CURRENCY_USD_CODE)
                                displayPricesInUSDAndGrams()
                        }
                        WEIGHT_TROY_OUNCE_CODE -> {
                            if (mSelectedCurrency == CURRENCY_EUR_CODE)
                                displayPricesInEURAndTroyOunce()
                            if (mSelectedCurrency == CURRENCY_USD_CODE)
                                displayPricesInUSDAndTroyOunce()
                        }
                    }
                }
            }
        }


        val arrayAdapterCurrency = ArrayAdapter(requireContext(), R.layout.item_menu_dropdown, currencyDropdownList)
        mAutoCompleteTextViewCurrency.setAdapter(arrayAdapterCurrency)

        mAutoCompleteTextViewCurrency.onItemClickListener = object: AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 >= 0) {
                    mSelectedCurrency = p0?.getItemAtPosition(p2) as String
                    displayCurrentMarketPrices()
                }
            }
        }
    } // handleDropDownMenus


    private fun displayCurrentMarketPrices() {
        when (mSelectedCurrency) {
            CURRENCY_EUR_CODE -> {
                if (mSelectedWeight == WEIGHT_GRAM_CODE)
                    displayPricesInEURAndGrams()
                if (mSelectedWeight == WEIGHT_TROY_OUNCE_CODE)
                    displayPricesInEURAndTroyOunce()
            }
            CURRENCY_USD_CODE -> {
                if (mSelectedWeight == WEIGHT_GRAM_CODE)
                    displayPricesInUSDAndGrams()
                if (mSelectedWeight == WEIGHT_TROY_OUNCE_CODE)
                    displayPricesInUSDAndTroyOunce()
            }
        }
    }


    private fun displayDefaultZeroValues() {
        when (mSelectedCurrency) {
            CURRENCY_USD_CODE -> {
                mTextViewGoldCurrentPrice.text = mLocaleUS.format(0)
                mTextViewSilverCurrentPrice.text = mLocaleUS.format(0)
                mTextViewPlatinumCurrentPrice.text = mLocaleUS.format(0)
                mTextViewPalladiumCurrentPrice.text = mLocaleUS.format(0)
            }
            CURRENCY_EUR_CODE -> {
                mTextViewGoldCurrentPrice.text = mLocaleEUR.format(0)
                mTextViewSilverCurrentPrice.text = mLocaleEUR.format(0)
                mTextViewPlatinumCurrentPrice.text = mLocaleEUR.format(0)
                mTextViewPalladiumCurrentPrice.text = mLocaleEUR.format(0)
            }
        }
    }


    private fun displayPricesInUSDAndTroyOunce() {
        mTextViewGoldCurrentPrice.text = mLocaleUS.format(mPriceOfOneOztOfGoldInUSD)
        mTextViewSilverCurrentPrice.text = mLocaleUS.format(mPriceOfOneOztOfSilverInUSD)
        mTextViewPlatinumCurrentPrice.text = mLocaleUS.format(mPriceOfOneOztOfPlatinumInUSD)
        mTextViewPalladiumCurrentPrice.text = mLocaleUS.format(mPriceOfOneOztOfPalladiumInUSD)
    }


    private fun displayPricesInEURAndTroyOunce() {
        mTextViewGoldCurrentPrice.text = mLocaleEUR.format(mPriceOfOneOztOfGoldInEUR)
        mTextViewSilverCurrentPrice.text = mLocaleEUR.format(mPriceOfOneOztOfSilverInEUR)
        mTextViewPlatinumCurrentPrice.text = mLocaleEUR.format(mPriceOfOneOztOfPlatinumInEUR)
        mTextViewPalladiumCurrentPrice.text = mLocaleEUR.format(mPriceOfOneOztOfPalladiumInEUR)
    }


    private fun displayPricesInUSDAndGrams() {
        mTextViewGoldCurrentPrice.text = mLocaleUS.format(mPriceOfOneGramOfGoldInUSD)
        mTextViewSilverCurrentPrice.text = mLocaleUS.format(mPriceOfOneGramOfSilverInUSD)
        mTextViewPlatinumCurrentPrice.text = mLocaleUS.format(mPriceOfOneGramOfPlatinumInUSD)
        mTextViewPalladiumCurrentPrice.text = mLocaleUS.format(mPriceOfOneGramOfPalladiumInUSD)
    }


    private fun displayPricesInEURAndGrams() {
        mTextViewGoldCurrentPrice.text = mLocaleEUR.format(mPriceOfOneGramOfGoldInEUR)
        mTextViewSilverCurrentPrice.text = mLocaleEUR.format(mPriceOfOneGramOfSilverInEUR)
        mTextViewPlatinumCurrentPrice.text = mLocaleEUR.format(mPriceOfOneGramOfPlatinumInEUR)
        mTextViewPalladiumCurrentPrice.text = mLocaleEUR.format(mPriceOfOneGramOfPalladiumInEUR)
    }


    private fun showPricesContainer() {
        mPricesContainer.visibility = View.VISIBLE
    }



}