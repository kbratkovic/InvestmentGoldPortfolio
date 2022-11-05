package com.kbratkovic.investmentgoldportfolio.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputLayout
import com.kbratkovic.investmentgoldportfolio.R
import com.kbratkovic.investmentgoldportfolio.models.GoldPriceResponse
import com.kbratkovic.investmentgoldportfolio.ui.MainViewModel
import com.kbratkovic.investmentgoldportfolio.util.Resource
import timber.log.Timber
import java.text.DateFormat
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
    private lateinit var pricesContainer: ConstraintLayout

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

        autoCompleteTextViewMetal.setOnItemClickListener{ adapterView, view, i, l ->
            Toast.makeText(requireContext(), "Item selected", Toast.LENGTH_SHORT).show()
        }


        mMainViewModel.getCurrentGoldPrice("XAU", "USD")


        mMainViewModel.currentGoldPrice.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { goldPriceResponse ->
                        showPrices()
                        textViewTimeAndDate.text = getString(R.string.time_and_date, formatDateAndTime(goldPriceResponse))
//                        textViewMetal.text = getString(R.string.metal, goldPriceResponse.metal)
//                        textViewCurrency.text = getString(R.string.currency, goldPriceResponse.currency)
                        textViewMetalHighPrice.text = goldPriceResponse.high_price.toString()
                        textViewMetalCurrentPrice.text = goldPriceResponse.price.toString()
                        textViewMetalLowPrice.text = goldPriceResponse.low_price.toString()
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


    private fun hideProgressBar() {
        linearProgressIndicator.visibility = View.INVISIBLE
    }


    private fun showProgressBar() {
        linearProgressIndicator.visibility = View.VISIBLE
    }


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

        hidePrices()
    }


    private fun hidePrices() {
        pricesContainer.visibility = View.INVISIBLE
    }


    private fun showPrices() {
        pricesContainer.visibility = View.VISIBLE
    }


    override fun onResume() {
        super.onResume()

    }

}