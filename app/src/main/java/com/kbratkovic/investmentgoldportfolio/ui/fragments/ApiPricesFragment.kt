package com.kbratkovic.investmentgoldportfolio.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputLayout
import com.kbratkovic.investmentgoldportfolio.ui.MainViewModel
import com.kbratkovic.investmentgoldportfolio.R
import com.kbratkovic.investmentgoldportfolio.util.Resource
import timber.log.Timber
import java.util.*


class ApiPricesFragment : Fragment() {

    private lateinit var textViewTimeAndDate: TextView
    private lateinit var textViewMetal: TextView
    private lateinit var textViewCurrency: TextView
    private lateinit var textViewMetalPrice: TextView
    private lateinit var dropdownMenuMetal: TextInputLayout
    private lateinit var dropdownMenuCurrency: TextInputLayout

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

        mMainViewModel.getCurrentGoldPrice("XAU", "EUR")

//        val currentGoldPriceObserver = Observer<Resource<GoldPriceResponse>> {
//            symbol.text = it.data?.symbol
//        }

        mMainViewModel.currentGoldPrice.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { goldPriceResponse ->
                        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd, HH:mm:ss")
                        val date = Date(goldPriceResponse.timestamp.toLong() * 1000)
                        textViewTimeAndDate.text = getString(R.string.time_and_date, sdf.format(date))
//                        textViewMetal.text = getString(R.string.metal, goldPriceResponse.metal)
//                        textViewCurrency.text = getString(R.string.currency, goldPriceResponse.currency)
                        textViewMetalPrice.text = goldPriceResponse.price.toString()
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

                }
            }
        })

    } // onViewCreated


    private fun hideProgressBar() {

    }

    override fun onResume() {
        super.onResume()

    }

    private fun initializeLayoutViews(view: View) {
        textViewTimeAndDate = view.findViewById(R.id.time_and_date)
//        textViewMetal = view.findViewById(R.id.metal)
//        textViewCurrency = view.findViewById(R.id.currency)
        dropdownMenuMetal = view.findViewById(R.id.menu_metal)
        dropdownMenuCurrency = view.findViewById(R.id.menu_currency)
        textViewMetalPrice = view.findViewById(R.id.metal_price)
    }


}