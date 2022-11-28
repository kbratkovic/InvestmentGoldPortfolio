package com.kbratkovic.investmentgoldportfolio.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.kbratkovic.investmentgoldportfolio.ui.MainViewModel
import com.kbratkovic.investmentgoldportfolio.R
import com.kbratkovic.investmentgoldportfolio.domain.models.InvestmentItem
import com.kbratkovic.investmentgoldportfolio.domain.models.MetalPriceApiCom
import com.kbratkovic.investmentgoldportfolio.ui.adapters.PortfolioAdapter
import com.kbratkovic.investmentgoldportfolio.util.Constants
import com.kbratkovic.investmentgoldportfolio.util.Resource
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*


class PortfolioFragment : Fragment() {

    private var mSelectedCurrency: String = ""
    private var mSelectedWeight: String = ""
    private var mDataSet: MutableList<InvestmentItem> = mutableListOf()

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mPortfolioAdapter: PortfolioAdapter

    private lateinit var mLinearProgressIndicator: LinearProgressIndicator

    private lateinit var mAutoCompleteTextViewWeight: AutoCompleteTextView
    private lateinit var mAutoCompleteTextViewCurrency: AutoCompleteTextView

    private lateinit var mTotalPurchasePriceValue: TextView
    private lateinit var mTotalWeightValue: TextView
    private lateinit var mCurrentMarketValue: TextView
    private lateinit var mTotalProfitValue: TextView

    private var mPriceOfOneOztOfGoldInUSD = 0.0
    private var mPriceOfOneOztOfGoldInEUR = 0.0
    private var mPriceOfOneGramOfGoldInUSD = 0.0
    private var mPriceOfOneGramOfGoldInEUR = 0.0
    private var mExchangeRateUSD = 0.0
    private var mExchangeRateEUR = 0.0

    private var mTotalPurchasePriceInEUR = BigDecimal.ZERO
    private var mTotalPurchasePriceInUSD = BigDecimal.ZERO
    private var mTotalWeightInGrams = 0.0
    private var mTotalWeightInTroyOunce = 0.0

    private var mTotalValueInUSD = BigDecimal.ZERO
    private var mTotalValueInEUR = BigDecimal.ZERO

    private var mApiResponse = false

    private val mLocaleEUR = NumberFormat.getCurrencyInstance(Locale.GERMANY)
    private val mLocaleUS = NumberFormat.getCurrencyInstance(Locale.US)

    private val mMainViewModel: MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(R.layout.fragment_portfolio, container, false)

    } // end onCreateView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeLayoutViews(view)
        startOnDataChangeListener()
        getValuesFromDropdownMenus()
        handleRecyclerView()
        observeInvestmentItemsChange()
//        observeCurrentGoldPriceChangeFromMetalPriceApiCom()

    } // end onViewCreated


    override fun onResume() {
        super.onResume()
        handleDropDownMenus()
//        mMainViewModel.getCurrentGoldPriceFromMetalPriceApiCom()

    } // onResume


    private fun startOnDataChangeListener() {
        mMainViewModel.setOnDataChangeListener(object: MainViewModel.OnDataChangeListener {
            override fun onDataChanged(message: String?) {
                if (message.equals(getString(R.string.network_error))) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
                hideLinearProgressIndicator()
            }
        })
    }


    private fun handleDropDownMenus() {
        val currencyDropdownList = resources.getStringArray(R.array.currency_items)
        val weightDropdownList = resources.getStringArray(R.array.weight_items)

        val arrayAdapterCurrency =
            ArrayAdapter(requireContext(), R.layout.item_menu_dropdown, currencyDropdownList)
        mAutoCompleteTextViewCurrency.setAdapter(arrayAdapterCurrency)


        mAutoCompleteTextViewCurrency.onItemClickListener = object : AdapterView.OnItemClickListener {
                override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (p2 >= 0) {
                        mSelectedCurrency = p0?.getItemAtPosition(p2) as String

                        setTotalPurchasePrice()
                        setTotalCurrentValue()
                        setTotalProfitValue()
                        mPortfolioAdapter.sendDataToAdapter(mDataSet, mSelectedCurrency, mSelectedWeight)
                    }
                }
            }


        val arrayAdapterWeight =
            ArrayAdapter(requireContext(), R.layout.item_menu_dropdown, weightDropdownList)
        mAutoCompleteTextViewWeight.setAdapter(arrayAdapterWeight)

        mAutoCompleteTextViewWeight.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                mSelectedWeight = p0?.getItemAtPosition(p2) as String
                setTotalWeight()
                mPortfolioAdapter.sendDataToAdapter(mDataSet, mSelectedCurrency, mSelectedWeight)
            }
        }
    } // handleDropDownMenus


    private fun setTotalPurchasePrice() {
        when (mSelectedCurrency) {
            Constants.CURRENCY_USD_CODE -> {
                mTotalPurchasePriceValue.text = mLocaleUS.format(mTotalPurchasePriceInUSD)
            }
            Constants.CURRENCY_EUR_CODE -> {
                mTotalPurchasePriceValue.text = mLocaleEUR.format(mTotalPurchasePriceInEUR)
            }
        }
    }


    private fun setTotalWeight() {
        when (mSelectedWeight) {
            Constants.WEIGHT_GRAM_CODE -> {
                mTotalWeightValue.text =
                    getString(
                        R.string.total_weight_value,
                        BigDecimal(mTotalWeightInGrams).setScale(2, RoundingMode.HALF_EVEN).toString(),
                        Constants.WEIGHT_GRAM_SHORT_CODE
                    )
            }
            Constants.WEIGHT_TROY_OUNCE_CODE -> {
                mTotalWeightValue.text =
                    getString(
                        R.string.total_weight_value,
                        BigDecimal(mTotalWeightInTroyOunce).setScale(2, RoundingMode.HALF_EVEN).toString(),
                        Constants.WEIGHT_TROY_OUNCE_SHORT_CODE
                    )
            }
        }
    }


    private fun setTotalCurrentValue() {
        mTotalValueInUSD = mTotalWeightInGrams.toBigDecimal().multiply(mPriceOfOneGramOfGoldInUSD.toBigDecimal())
        mTotalValueInEUR = mTotalWeightInGrams.toBigDecimal().multiply(mPriceOfOneGramOfGoldInEUR.toBigDecimal())

        when (mSelectedCurrency) {
            Constants.CURRENCY_USD_CODE -> {
                mCurrentMarketValue.text = mLocaleUS.format(mTotalValueInUSD)
            }
            Constants.CURRENCY_EUR_CODE -> {
                mCurrentMarketValue.text = mLocaleEUR.format(mTotalValueInEUR)
            }
        }
    } // setTotalCurrentValue


    private fun setTotalProfitValue() {
        if (mApiResponse) {
            when (mSelectedCurrency) {
                Constants.CURRENCY_USD_CODE -> {
                    val totalProfit = mTotalValueInUSD.minus(mTotalPurchasePriceInUSD)
                    if (totalProfit < BigDecimal.ZERO)
                        mTotalProfitValue.setTextColor(Color.RED)
                    else if (totalProfit > BigDecimal.ZERO)
                        mTotalProfitValue.setTextColor(Color.GREEN)
                    mTotalProfitValue.text = mLocaleUS.format(totalProfit)
                }
                Constants.CURRENCY_EUR_CODE -> {
                    val totalProfit = mTotalValueInEUR.minus(mTotalPurchasePriceInEUR)
                    if (totalProfit < BigDecimal.ZERO)
                        mTotalProfitValue.setTextColor(Color.RED)
                    else if (totalProfit > BigDecimal.ZERO)
                        mTotalProfitValue.setTextColor(Color.GREEN)
                    mTotalProfitValue.text = mLocaleEUR.format(totalProfit)
                }
            }
        }
    } // setTotalProfitValue


    private fun calculateExchangeRatesAndGoldPrices(metalPrice: MetalPriceApiCom) {
        mExchangeRateUSD = (1).div(metalPrice.rates.EUR)
        mExchangeRateEUR = metalPrice.rates.EUR

        mPriceOfOneOztOfGoldInUSD = (1).div(metalPrice.rates.XAU)
        mPriceOfOneOztOfGoldInEUR = (1).div(metalPrice.rates.XAU).times(mExchangeRateEUR)
        mPriceOfOneGramOfGoldInUSD = mPriceOfOneOztOfGoldInUSD.div(Constants.CONVERT_TROY_OUNCE_CODE)
        mPriceOfOneGramOfGoldInEUR = mPriceOfOneOztOfGoldInEUR.div(Constants.CONVERT_TROY_OUNCE_CODE)
    } // calculateExchangeRatesAndGoldPrices


    private fun observeInvestmentItemsChange() {
        mMainViewModel.allInvestmentItems.observe(viewLifecycleOwner) { listOfInvestmentItems ->
            mDataSet.addAll(listOfInvestmentItems)
            mPortfolioAdapter.sendDataToAdapter(mDataSet, mSelectedCurrency, mSelectedWeight)

            for (item in mDataSet) {
//                clearDisplayedData()
                mTotalPurchasePriceInEUR = mTotalPurchasePriceInEUR.add(item.purchasePriceInEUR)
                mTotalPurchasePriceInUSD = mTotalPurchasePriceInUSD.add(item.purchasePriceInUSD)
                mTotalWeightInGrams = mTotalWeightInGrams.plus(item.weightInGrams)
                mTotalWeightInTroyOunce = mTotalWeightInTroyOunce.plus(item.weightInTroyOunce)
            }

            setTotalPurchasePrice()
            setTotalWeight()
        }
    } // observeInvestmentItemsChange


    private fun observeCurrentGoldPriceChangeFromMetalPriceApiCom() {
        mMainViewModel.currentGoldPriceFromMetalPriceApiCom.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideLinearProgressIndicator()
                    response.data?.let { metalPrice ->
                        mApiResponse = true
                        calculateExchangeRatesAndGoldPrices(metalPrice)
                        setTotalCurrentValue()
                        setTotalProfitValue()
                    }
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        mApiResponse = false
                        hideLinearProgressIndicator()
                        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
                        toast.show()
                        Timber.e(message)
                    }
                }
                is Resource.Loading -> {
                    showLinearProgressIndicator()
                }
            }
        }
    } // observeCurrentGoldPriceChangeFromMetalPriceApiCom


    private fun clearDisplayedData() {
        mTotalPurchasePriceInEUR = BigDecimal.ZERO
        mTotalPurchasePriceInUSD = BigDecimal.ZERO
        mTotalWeightInGrams = 0.0
        mTotalWeightInTroyOunce = 0.0
    }


    private fun getValuesFromDropdownMenus() {
        mSelectedWeight = mAutoCompleteTextViewWeight.text.toString()
        mSelectedCurrency = mAutoCompleteTextViewCurrency.text.toString()
    }


    private fun initializeLayoutViews(view: View) {
        mRecyclerView = view.findViewById(R.id.recyclerView)
        mLinearProgressIndicator = view.findViewById(R.id.linear_progress_indicator)
        mAutoCompleteTextViewWeight = view.findViewById(R.id.auto_complete_text_view_weight)
        mAutoCompleteTextViewCurrency = view.findViewById(R.id.auto_complete_text_view_currency)
        mTotalPurchasePriceValue = view.findViewById(R.id.total_purchase_price_value)
        mTotalWeightValue = view.findViewById(R.id.total_weight_value)
        mCurrentMarketValue = view.findViewById(R.id.current_market_value_value)
        mTotalProfitValue = view.findViewById(R.id.total_profit_value)
    }


    private fun handleRecyclerView() {
        mPortfolioAdapter = PortfolioAdapter(requireContext(), mDataSet, mSelectedCurrency, mSelectedWeight)
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mRecyclerView.adapter = mPortfolioAdapter
    }


    private fun showLinearProgressIndicator() {
        mLinearProgressIndicator.visibility = View.VISIBLE
    }


    fun hideLinearProgressIndicator() {
        mLinearProgressIndicator.visibility = View.GONE
    }

}