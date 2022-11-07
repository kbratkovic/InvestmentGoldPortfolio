package com.kbratkovic.investmentgoldportfolio.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputLayout
import com.kbratkovic.investmentgoldportfolio.R
import com.kbratkovic.investmentgoldportfolio.models.GoldPriceResponse
import com.kbratkovic.investmentgoldportfolio.ui.MainViewModel
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.CURRENCY_EUR_CODE
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.CURRENCY_USD_CODE
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.GOLD_CODE
import com.kbratkovic.investmentgoldportfolio.util.Resource
import timber.log.Timber
import java.text.DateFormat
import java.text.NumberFormat
import java.util.*


class ApiPricesFragment : Fragment() {

    private lateinit var textViewTimeAndDate: TextView
    private lateinit var textViewMetal: TextView
    private lateinit var textViewCurrency: TextView
    private lateinit var textViewMetalHighPrice: TextView
    private lateinit var textViewMetalCurrentPrice: TextView
    private lateinit var textViewMetalLowPrice: TextView
    private lateinit var dropdownMenuMetal: TextInputLayout
    private lateinit var dropdownMenuCurrency: TextInputLayout
    private lateinit var autoCompleteTextViewMetal : AutoCompleteTextView
    private lateinit var autoCompleteTextViewCurrency : AutoCompleteTextView
    private lateinit var linearProgressIndicator: LinearProgressIndicator
    private lateinit var pricesContainer: LinearLayout

    private lateinit var selectedMetal: String
    private lateinit var selectedCurrency: String

    private val mMainViewModel: MainViewModel by activityViewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    } // end onCreate


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_api_prices, container, false)
    } // end onCreateView


    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeLayoutViews(view)
        startOnDataChangeListener()


        mMainViewModel.currentGoldPrice.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { goldPriceResponse ->
                        showPrices()
                        textViewTimeAndDate.text = getString(R.string.time_and_date, formatDateAndTime(goldPriceResponse))
                        when (goldPriceResponse.currency) {
                            CURRENCY_USD_CODE -> displayPricesInUSD(goldPriceResponse)
                            CURRENCY_EUR_CODE -> displayPricesInEUR(goldPriceResponse)
                        }
                    }
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        textViewTimeAndDate.text = message
                        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
                        toast.show()
                        Timber.e(message)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

    } // onViewCreated



    private fun initializeLayoutViews(view: View) {
        textViewTimeAndDate = view.findViewById(R.id.time_and_date)
//        textViewMetal = view.findViewById(R.id.metal)
//        textViewCurrency = view.findViewById(R.id.currency)
        dropdownMenuMetal = view.findViewById(R.id.menu_metal)
        autoCompleteTextViewMetal = view.findViewById(R.id.auto_complete_text_view_metal)
        autoCompleteTextViewCurrency = view.findViewById(R.id.auto_complete_text_view_currency)
        dropdownMenuCurrency = view.findViewById(R.id.menu_currency)
        textViewMetalHighPrice = view.findViewById(R.id.metal_high_price)
        textViewMetalCurrentPrice = view.findViewById(R.id.metal_current_price)
        textViewMetalLowPrice = view.findViewById(R.id.metal_low_price)
        linearProgressIndicator = view.findViewById(R.id.linear_progress_indicator)
        pricesContainer = view.findViewById(R.id.prices_container)
    }


    private fun startOnDataChangeListener() {
        mMainViewModel.setOnDataChangeListener(object: MainViewModel.OnDataChangeListener {
            override fun onDataChanged(message: String?) {
                if (message.equals(getString(R.string.network_error))) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
                hideProgressBar()
            }
        })
    }


    private fun formatDateAndTime(goldPriceResponse: GoldPriceResponse) : String {
        val date = Date(goldPriceResponse.timestamp.toLong() * 1000)
        val dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.US)
        val timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.US)
        val formattedDate = dateFormat.format(date)
        val formattedTime = timeFormat.format(date)
        return "$formattedDate, $formattedTime"
    }


    private fun manageDropDownMenus() {
        val metalDropdownList = resources.getStringArray(R.array.metal_items)
        val currencyDropdownList = resources.getStringArray(R.array.currency_items)

        val arrayAdapterMetal = ArrayAdapter(requireContext(), R.layout.item_menu_dropdown, metalDropdownList)
        autoCompleteTextViewMetal.setAdapter(arrayAdapterMetal)

        val arrayAdapterCurrency = ArrayAdapter(requireContext(), R.layout.item_menu_dropdown, currencyDropdownList)
        autoCompleteTextViewCurrency.setAdapter(arrayAdapterCurrency)

        autoCompleteTextViewMetal.onItemClickListener = object: AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 >= 0) {
                    selectedMetal = p0?.getItemAtPosition(p2) as String
                }
            }
        }

        autoCompleteTextViewCurrency.onItemClickListener = object: AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 >= 0) {
                    selectedCurrency = p0?.getItemAtPosition(p2) as String

                    when (selectedCurrency) {
                        CURRENCY_EUR_CODE -> mMainViewModel.getCurrentGoldPrice(GOLD_CODE, CURRENCY_EUR_CODE)
                        CURRENCY_USD_CODE -> mMainViewModel.getCurrentGoldPrice(GOLD_CODE, CURRENCY_USD_CODE)
                    }
                }
            }
        }
    }


    private fun displayPricesInUSD(goldPriceResponse: GoldPriceResponse) {
        val localeUS = NumberFormat.getCurrencyInstance(Locale.US)
        textViewMetalHighPrice.text = localeUS.format(goldPriceResponse.high_price)
        textViewMetalCurrentPrice.text = localeUS.format(goldPriceResponse.price)
        textViewMetalLowPrice.text = localeUS.format(goldPriceResponse.low_price)
    }


    private fun displayPricesInEUR(goldPriceResponse: GoldPriceResponse) {
        val localeGermany = NumberFormat.getCurrencyInstance(Locale.GERMANY)
        textViewMetalHighPrice.text = localeGermany.format(goldPriceResponse.high_price)
        textViewMetalCurrentPrice.text = localeGermany.format(goldPriceResponse.price)
        textViewMetalLowPrice.text = localeGermany.format(goldPriceResponse.low_price)
    }


    private fun hideProgressBar() {
        linearProgressIndicator.visibility = View.INVISIBLE
    }


    private fun showProgressBar() {
        linearProgressIndicator.visibility = View.VISIBLE
    }


    private fun hidePrices() {
        pricesContainer.visibility = View.INVISIBLE
    }


    private fun showPrices() {
        pricesContainer.visibility = View.VISIBLE
    }


    private fun setDefaultValueInDropDownMenuCurrency() {
        autoCompleteTextViewCurrency.setText(CURRENCY_USD_CODE, false)
    }


    override fun onResume() {
        super.onResume()
        hidePrices()
        manageDropDownMenus()
        setDefaultValueInDropDownMenuCurrency()
        mMainViewModel.getCurrentGoldPrice(GOLD_CODE, CURRENCY_USD_CODE)
    }

}