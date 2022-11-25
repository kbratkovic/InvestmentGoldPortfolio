package com.kbratkovic.investmentgoldportfolio.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kbratkovic.investmentgoldportfolio.ui.MainViewModel
import com.kbratkovic.investmentgoldportfolio.R
import com.kbratkovic.investmentgoldportfolio.domain.models.GoldPrice
import com.kbratkovic.investmentgoldportfolio.domain.models.InvestmentItem
import com.kbratkovic.investmentgoldportfolio.domain.models.MetalPriceApiCom
import com.kbratkovic.investmentgoldportfolio.ui.adapters.PortfolioAdapter
import com.kbratkovic.investmentgoldportfolio.util.Constants
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.GOLD_CODE
import com.kbratkovic.investmentgoldportfolio.util.Resource
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*


class PortfolioFragment : Fragment() {

    private var mSelectedCurrency: String = ""
    private var mSelectedWeight: String = ""
    private var mDataSet: List<InvestmentItem> = listOf()

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mPortfolioAdapter: PortfolioAdapter

    private lateinit var mAutoCompleteTextViewWeight: AutoCompleteTextView
    private lateinit var mAutoCompleteTextViewCurrency: AutoCompleteTextView

    private lateinit var mTotalPurchasePriceValue: TextView
    private lateinit var mTotalWeightValue: TextView
    private lateinit var mTotalCurrentValue: TextView
    private lateinit var mTotalProfitValue: TextView

    private var mSumInEUR = BigDecimal.ZERO
    private var mSumInUSD = BigDecimal.ZERO
    private var mSumInGrams = 0.0
    private var mSumInTroyOunce = 0.0

    private var mTotalValueInUSD = BigDecimal.ZERO
    private var mTotalValueInEUR = BigDecimal.ZERO

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
        handleRecyclerView()
        getValuesFromDropdownMenus()
        observeInvestmentItemsChange()
        observeCurrentGoldPriceChange()
//        observeCurrentGoldPriceChangeFromMetalPriceApiCom()

    } // end onViewCreated


    override fun onResume() {
        super.onResume()
        handleDropDownMenus()
//        mMainViewModel.getCurrentGoldPrice(GOLD_CODE, mSelectedCurrency)
//        mMainViewModel.getCurrentGoldPriceFromMetalPriceApiCom()

    } // onResume


    private fun handleDropDownMenus() {
        val currencyDropdownList = resources.getStringArray(R.array.currency_items)
        val weightDropdownList = resources.getStringArray(R.array.weight_items)

        val arrayAdapterCurrency = ArrayAdapter(requireContext(), R.layout.item_menu_dropdown, currencyDropdownList)
        mAutoCompleteTextViewCurrency.setAdapter(arrayAdapterCurrency)

        val arrayAdapterWeight = ArrayAdapter(requireContext(), R.layout.item_menu_dropdown, weightDropdownList)
        mAutoCompleteTextViewWeight.setAdapter(arrayAdapterWeight)

        mAutoCompleteTextViewCurrency.onItemClickListener = object: AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 >= 0) {
                    mSelectedCurrency = p0?.getItemAtPosition(p2) as String

                    setTotalPurchasePrice()
                }
            }
        }

        mAutoCompleteTextViewWeight.onItemClickListener = object: AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                mSelectedWeight = p0?.getItemAtPosition(p2) as String
                setTotalWeight()
            }
        }
    } // handleDropDownMenus


    private fun setTotalPurchasePrice() {
        when (mSelectedCurrency) {
            Constants.CURRENCY_USD_CODE -> {
                mTotalPurchasePriceValue.text = mLocaleUS.format(mSumInUSD)
            }
            Constants.CURRENCY_EUR_CODE -> {
                mTotalPurchasePriceValue.text = mLocaleEUR.format(mSumInEUR)
            }
        }
    }


    private fun setTotalWeight() {
        when (mSelectedWeight) {
            Constants.WEIGHT_GRAM_CODE -> {
                mTotalWeightValue.text =
                    getString(R.string.total_weight_value, BigDecimal(mSumInGrams).setScale(2, RoundingMode.HALF_EVEN).toString(), Constants.WEIGHT_GRAM_SHORT_CODE)
            }
            Constants.WEIGHT_TROY_OUNCE_CODE -> {
                mTotalWeightValue.text =
                    getString(R.string.total_weight_value, BigDecimal(mSumInTroyOunce).setScale(2, RoundingMode.HALF_EVEN).toString(), Constants.WEIGHT_TROY_OUNCE_SHORT_CODE)
            }
        }
    }


    private fun setTotalCurrentValue(metalPrice: MetalPriceApiCom) {
        val oneOztOfGoldInUSD = 1/metalPrice.rates.XAU
        val oneGramOfGoldInUSD = oneOztOfGoldInUSD / Constants.CONVERT_TROY_OUNCE_CODE
        val oneGramOfGoldInEUR = (oneGramOfGoldInUSD) / (1/metalPrice.rates.EUR)

        mTotalValueInUSD = mSumInGrams.toBigDecimal().multiply(oneGramOfGoldInUSD.toBigDecimal())
        mTotalValueInEUR = mSumInGrams.toBigDecimal().multiply(oneGramOfGoldInEUR.toBigDecimal())

        when (mSelectedCurrency) {
            Constants.CURRENCY_USD_CODE -> {
//                val value = mSumInGrams.toBigDecimal().multiply(oneGramOfGoldInUSD.toBigDecimal())
                mTotalCurrentValue.text = mLocaleUS.format(mTotalValueInUSD)
            }
            Constants.CURRENCY_EUR_CODE -> {
//                val value = mSumInGrams.toBigDecimal().multiply(oneGramOfGoldInEUR.toBigDecimal())
                mTotalCurrentValue.text = mLocaleEUR.format(mTotalValueInEUR)
            }
        }
    } // setTotalCurrentValue


    private fun setTotalProfitValue() {
        when (mSelectedCurrency) {
            Constants.CURRENCY_USD_CODE -> {
                val totalProfit = mSumInUSD.minus(mTotalValueInUSD)
                mTotalProfitValue.text = mLocaleUS.format(totalProfit)
            }
            Constants.CURRENCY_EUR_CODE -> {
                val totalProfit = mSumInEUR.minus(mTotalValueInEUR)
                mTotalProfitValue.text = mLocaleEUR.format(totalProfit)
            }
        }
    } // setTotalProfitValue



    private fun observeInvestmentItemsChange() {
        mMainViewModel.allInvestmentItems.observe(viewLifecycleOwner) { listOfInvestmentItems ->
            mPortfolioAdapter.sendDataToAdapter(listOfInvestmentItems)

            for (item in listOfInvestmentItems) {
                clearDisplayedData()
                mSumInEUR = mSumInEUR.add(item.purchasePriceInEUR)
                mSumInUSD = mSumInUSD.add(item.purchasePriceInUSD)
                mSumInGrams = mSumInGrams.plus(item.weightInGrams)
                mSumInTroyOunce = mSumInTroyOunce.plus(item.weightInTroyOunce)
            }

            setTotalPurchasePrice()
            setTotalWeight()
        }
    } // observeInvestmentItemsChange


//    private fun observeCurrentGoldPriceChangeFromMetalPriceApiCom() {
//        mMainViewModel.currentGoldPriceFromMetalPriceApiCom.observe(viewLifecycleOwner) { response ->
//            when (response) {
//                is Resource.Success -> {
//                    response.data?.let { metalPrice ->
//                        setTotalCurrentValue(metalPrice)
//                        setTotalProfitValue()
//                    }
//                }
//                is Resource.Error -> {
//                    response.message?.let { message ->
//                        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
//                        toast.show()
//                        Timber.e(message)
//                    }
//                }
//                is Resource.Loading -> {
//                }
//            }
//        }
//    } // observeCurrentGoldPriceChangeFromMetalPriceApiCom


    private fun observeCurrentGoldPriceChange() {
        mMainViewModel.currentGoldPrice.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { goldPrice ->
//                        setTotalCurrentValue(goldPrice.price_gram_24k)
                        setTotalProfitValue()
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
    } // observeCurrentGoldPriceChange


    private fun clearDisplayedData() {
        mSumInEUR = BigDecimal.ZERO
        mSumInUSD = BigDecimal.ZERO
        mSumInGrams = 0.0
        mSumInTroyOunce = 0.0
    }


    private fun getValuesFromDropdownMenus() {
        mSelectedWeight = mAutoCompleteTextViewWeight.text.toString()
        mSelectedCurrency = mAutoCompleteTextViewCurrency.text.toString()
    }


    private fun initializeLayoutViews(view: View) {
        mRecyclerView = view.findViewById(R.id.recyclerView)
        mAutoCompleteTextViewWeight = view.findViewById(R.id.auto_complete_text_view_weight)
        mAutoCompleteTextViewCurrency = view.findViewById(R.id.auto_complete_text_view_currency)
        mTotalPurchasePriceValue = view.findViewById(R.id.total_purchase_price_value)
        mTotalWeightValue = view.findViewById(R.id.total_weight_value)
        mTotalCurrentValue = view.findViewById(R.id.total_current_value_value)
        mTotalProfitValue = view.findViewById(R.id.total_profit_value)
    }


    private fun handleRecyclerView() {
        mPortfolioAdapter = PortfolioAdapter(mDataSet)
        mRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        mRecyclerView.adapter = mPortfolioAdapter
    }



}