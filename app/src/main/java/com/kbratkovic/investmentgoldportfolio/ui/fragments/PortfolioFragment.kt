package com.kbratkovic.investmentgoldportfolio.ui.fragments

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.kbratkovic.investmentgoldportfolio.ui.MainViewModel
import com.kbratkovic.investmentgoldportfolio.R
import com.kbratkovic.investmentgoldportfolio.domain.models.InvestmentItem
import com.kbratkovic.investmentgoldportfolio.domain.models.MetalPrice
import com.kbratkovic.investmentgoldportfolio.ui.adapters.PortfolioAdapter
import com.kbratkovic.investmentgoldportfolio.util.*
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*


class PortfolioFragment : Fragment() {

    private var mSelectedCurrency = Constants.CURRENCY_EUR_CODE
    private var mSelectedWeight = Constants.WEIGHT_GRAM_CODE
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

    private var mIsApiResponseTrue = false

    private val mLocaleEUR = NumberFormat.getCurrencyInstance(Locale.GERMANY)
    private val mLocaleUS = NumberFormat.getCurrencyInstance(Locale.US)

    private var mBottomNavigationView: BottomNavigationView? = null
    private lateinit var mSharedPreferences: SharedPreferences


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

        getSharedPreference()
        initializeLayoutViews(view)
        startOnDataChangeListener()
        handleRecyclerViewAndAdapter()
        displayDefaultZeroValues()
        observeInvestmentItemsChange()
        observeCurrentGoldPriceChangeFromMetalPriceApiCom()
        enableSwipeToDeleteAndUndo()


    } // end onViewCreated


    override fun onResume() {
        super.onResume()
        getSharedPreference()
        handleDropDownMenus()
        setValueToDropDownMenu()
        getMetalPriceFromApi()

        setTotalPurchasePrice()
        setTotalWeight()
        setCurrentMarketValue()
        setTotalProfitValue()
    } // onResume


    override fun onPause() {
        super.onPause()
        putValuesToSharedPreferences()
    }


    private fun getSharedPreference() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        mSelectedCurrency = mSharedPreferences.getString("currency", "").toString()
        mSelectedWeight = mSharedPreferences.getString("weight", "").toString()
    }


    private fun putValuesToSharedPreferences() {
        val editor = mSharedPreferences.edit()
        editor.putString("currency", mSelectedCurrency)
        editor.putString("weight", mSelectedWeight)
        editor.apply()
    }


    private fun setValueToDropDownMenu() {
        mAutoCompleteTextViewCurrency.setText(mSelectedCurrency, false)
        mAutoCompleteTextViewWeight.setText(mSelectedWeight, false)
    }


    private fun getMetalPriceFromApi() {
        if (NetworkConnection.hasInternetConnection(requireContext())) {
            mMainViewModel.getMetalPriceFromApi()
        }
        else {
            hideLinearProgressIndicator()
            if (mBottomNavigationView != null) {
                // android.R.id.content gives you the root element of a view, without having to know its actual name/type/ID.
                Utils.showSnackBar(requireActivity().findViewById(android.R.id.content),
                    getString(R.string.error_network_connection), mBottomNavigationView!!
                )
            }
        }
    }


    private fun displayDefaultZeroValues() {
        when (mSelectedCurrency) {
            Constants.CURRENCY_USD_CODE -> {
                mCurrentMarketValue.text = mLocaleUS.format(0)
                mTotalProfitValue.text = mLocaleUS.format(0)
            }
            Constants.CURRENCY_EUR_CODE -> {
                mCurrentMarketValue.text = mLocaleEUR.format(0)
                mTotalProfitValue.text = mLocaleEUR.format(0)
            }
        }
    }


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
                        setCurrentMarketValue()
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
    } // setTotalPurchasePrice


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
    } // setTotalWeight


    private fun setCurrentMarketValue() {
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
    } // setCurrentMarketValue


    private fun setTotalProfitValue() {
        // if no api response Total Profit will be negative Total Purchase Price
        when (mSelectedCurrency) {
            Constants.CURRENCY_USD_CODE -> {
                val totalProfit = mTotalValueInUSD.minus(mTotalPurchasePriceInUSD)
                if (totalProfit < BigDecimal.ZERO)
                    mTotalProfitValue.setTextColor(Color.RED)
                else if (totalProfit > BigDecimal.ZERO)
                    mTotalProfitValue.setTextColor(Color.GREEN)
                mTotalProfitValue.text = mLocaleUS.format(totalProfit)

                if (!mIsApiResponseTrue)
                    mTotalProfitValue.text = mLocaleUS.format(0)
            }
            Constants.CURRENCY_EUR_CODE -> {
                val totalProfit = mTotalValueInEUR.minus(mTotalPurchasePriceInEUR)
                if (totalProfit < BigDecimal.ZERO)
                    mTotalProfitValue.setTextColor(Color.RED)
                else if (totalProfit > BigDecimal.ZERO)
                    mTotalProfitValue.setTextColor(Color.GREEN)
                mTotalProfitValue.text = mLocaleEUR.format(totalProfit)

                if (!mIsApiResponseTrue)
                    mTotalProfitValue.text = mLocaleEUR.format(0)
            }
        }
    } // setTotalProfitValue


    private fun calculateExchangeRatesAndGoldPrices(metalPrice: MetalPrice) {
        mExchangeRateUSD = (1).div(metalPrice.rates.EUR)
        mExchangeRateEUR = metalPrice.rates.EUR

        mPriceOfOneOztOfGoldInUSD = (1).div(metalPrice.rates.XAU)
        mPriceOfOneOztOfGoldInEUR = (1).div(metalPrice.rates.XAU).times(mExchangeRateEUR)
        mPriceOfOneGramOfGoldInUSD = mPriceOfOneOztOfGoldInUSD.div(Constants.CONVERT_TROY_OUNCE_CODE)
        mPriceOfOneGramOfGoldInEUR = mPriceOfOneOztOfGoldInEUR.div(Constants.CONVERT_TROY_OUNCE_CODE)
    } // calculateExchangeRatesAndGoldPrices


    private fun observeInvestmentItemsChange() {
        mMainViewModel.allInvestmentItems.observe(viewLifecycleOwner) { listOfInvestmentItems ->
            clearDisplayedData()
            mDataSet.clear()
            mDataSet.addAll(listOfInvestmentItems)
            mPortfolioAdapter.sendDataToAdapter(mDataSet, mSelectedCurrency, mSelectedWeight)

            for (item in mDataSet) {
                mTotalPurchasePriceInEUR = mTotalPurchasePriceInEUR.add(item.purchasePriceInEUR)
                mTotalPurchasePriceInUSD = mTotalPurchasePriceInUSD.add(item.purchasePriceInUSD)
                mTotalWeightInGrams = mTotalWeightInGrams.plus(item.weightInGrams)
                mTotalWeightInTroyOunce = mTotalWeightInTroyOunce.plus(item.weightInTroyOunce)
            }

            setTotalPurchasePrice()
            setTotalWeight()
            setCurrentMarketValue()
            setTotalProfitValue()
        }
    } // observeInvestmentItemsChange


    private fun observeCurrentGoldPriceChangeFromMetalPriceApiCom() {
        mMainViewModel.metalPrice.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideLinearProgressIndicator()
                    response.data?.let { metalPrice ->
                        mIsApiResponseTrue = true
                        calculateExchangeRatesAndGoldPrices(metalPrice)
                        setCurrentMarketValue()
                        setTotalProfitValue()
                    }
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        mIsApiResponseTrue = false
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


    private fun initializeLayoutViews(view: View) {
        mRecyclerView = view.findViewById(R.id.recyclerView)
        mLinearProgressIndicator = view.findViewById(R.id.linear_progress_indicator)
        mAutoCompleteTextViewWeight = view.findViewById(R.id.auto_complete_text_view_weight)
        mAutoCompleteTextViewCurrency = view.findViewById(R.id.auto_complete_text_view_currency)
        mTotalPurchasePriceValue = view.findViewById(R.id.total_purchase_price_value)
        mTotalWeightValue = view.findViewById(R.id.total_weight_value)
        mCurrentMarketValue = view.findViewById(R.id.current_market_value_value)
        mTotalProfitValue = view.findViewById(R.id.total_profit_value)

        mBottomNavigationView = activity?.findViewById(R.id.bottom_navigation)
    }


    private fun handleRecyclerViewAndAdapter() {
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


    private fun enableSwipeToDeleteAndUndo() {
        val swipeToDeleteCallback: SwipeToDeleteCallback = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                val position: Int = viewHolder.bindingAdapterPosition
                val item: InvestmentItem = mPortfolioAdapter.getDataSet()[position]
                mMainViewModel.deleteInvestmentItem(item)
                Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        getString(R.string.info_item_removed),
                        Snackbar.LENGTH_LONG
                    ).apply {
                        setAction(getString(R.string.undo), object : View.OnClickListener {
                            override fun onClick(view: View?) {
                                mMainViewModel.undoDeleteInvestmentItem(item)
                                mRecyclerView.scrollToPosition(position)
                            }
                        })
                        setActionTextColor(Color.YELLOW)
                        anchorView = mBottomNavigationView
                    }.show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(mRecyclerView)
    }




}