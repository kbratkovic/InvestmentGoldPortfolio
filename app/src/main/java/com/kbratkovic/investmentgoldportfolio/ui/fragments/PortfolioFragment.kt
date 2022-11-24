package com.kbratkovic.investmentgoldportfolio.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kbratkovic.investmentgoldportfolio.ui.MainViewModel
import com.kbratkovic.investmentgoldportfolio.R
import com.kbratkovic.investmentgoldportfolio.domain.models.GoldPrice
import com.kbratkovic.investmentgoldportfolio.domain.models.InvestmentItem
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

    private var selectedCurrency: String = ""
    private var selectedWeight: String = ""
    private var dataSet: List<InvestmentItem> = listOf()

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: PortfolioAdapter

    private lateinit var autoCompleteTextViewWeight: AutoCompleteTextView
    private lateinit var autoCompleteTextViewCurrency: AutoCompleteTextView

    private lateinit var totalPurchasePriceValue: TextView
    private lateinit var totalWeightValue: TextView
    private lateinit var totalCurrentValue: TextView
    private lateinit var totalProfitValue: TextView

    private var sumInEUR = BigDecimal.ZERO
    private var sumInUSD = BigDecimal.ZERO

    private var sumInGrams = 0.0
    private var sumInTroyOunce = 0.0

    private lateinit var goldPrice: GoldPrice


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

    } // end onViewCreated


    override fun onResume() {
        super.onResume()
        handleDropDownMenus()
//        getValuesFromDropdownMenus()
        mMainViewModel.getCurrentGoldPrice(GOLD_CODE, selectedCurrency)

    }


    private fun handleDropDownMenus() {
        val currencyDropdownList = resources.getStringArray(R.array.currency_items)
        val weightDropdownList = resources.getStringArray(R.array.weight_items)

        val arrayAdapterCurrency = ArrayAdapter(requireContext(), R.layout.item_menu_dropdown, currencyDropdownList)
        autoCompleteTextViewCurrency.setAdapter(arrayAdapterCurrency)

        val arrayAdapterWeight = ArrayAdapter(requireContext(), R.layout.item_menu_dropdown, weightDropdownList)
        autoCompleteTextViewWeight.setAdapter(arrayAdapterWeight)

        autoCompleteTextViewCurrency.onItemClickListener = object: AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 >= 0) {
                    selectedCurrency = p0?.getItemAtPosition(p2) as String

                    setTotalPurchasePrice()
                }
            }
        }

        autoCompleteTextViewWeight.onItemClickListener = object: AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedWeight = p0?.getItemAtPosition(p2) as String

                setTotalWeight()
            }
        }
    } // end handleDropDownMenus


    private fun setTotalPurchasePrice() {
        when (selectedCurrency) {
            Constants.CURRENCY_USD_CODE -> {
                val localeUS = NumberFormat.getCurrencyInstance(Locale.US)
                totalPurchasePriceValue.text = localeUS.format(sumInUSD)
            }
            Constants.CURRENCY_EUR_CODE -> {
                val localeEUR = NumberFormat.getCurrencyInstance(Locale.GERMANY)
                totalPurchasePriceValue.text = localeEUR.format(sumInEUR)
            }
        }
    }


    private fun setTotalWeight() {
        when (selectedWeight) {
            Constants.WEIGHT_GRAM_CODE -> {
                totalWeightValue.text =
                    getString(R.string.total_weight_value, BigDecimal(sumInGrams).setScale(2, RoundingMode.HALF_EVEN).toString(), Constants.WEIGHT_GRAM_SHORT_CODE)

            }
            Constants.WEIGHT_TROY_OUNCE_CODE -> {
                totalWeightValue.text =
                    getString(R.string.total_weight_value, BigDecimal(sumInTroyOunce).setScale(2, RoundingMode.HALF_EVEN).toString(), Constants.WEIGHT_TROY_OUNCE_SHORT_CODE)
            }
        }
    }


    private fun observeInvestmentItemsChange() {
        mMainViewModel.allInvestmentItems.observe(viewLifecycleOwner) { listOfInvestmentItems ->
            adapter.sendDataToAdapter(listOfInvestmentItems)

            for (item in listOfInvestmentItems) {
                clearDisplayedData()
                sumInEUR = sumInEUR.add(item.purchasePriceInEUR)
                sumInUSD = sumInUSD.add(item.purchasePriceInUSD)
                sumInGrams = sumInGrams.plus(item.weightInGrams)
                sumInTroyOunce = sumInTroyOunce.plus(item.weightInTroyOunce)
            }

            setTotalPurchasePrice()
            setTotalWeight()
        }
    } // end observeInvestmentItemsChange


    private fun clearDisplayedData() {
        sumInEUR = BigDecimal.ZERO
        sumInUSD = BigDecimal.ZERO
        sumInGrams = 0.0
        sumInTroyOunce = 0.0
    }


    private fun setTotalCurrentValue(pricePerGram: Double) {
        when (selectedCurrency) {
            Constants.CURRENCY_USD_CODE -> {
                val localeUS = NumberFormat.getCurrencyInstance(Locale.US)
                val value = sumInUSD.multiply(pricePerGram.toBigDecimal())
                totalCurrentValue.text = localeUS.format(value)
            }
            Constants.CURRENCY_EUR_CODE -> {
                val localeEUR = NumberFormat.getCurrencyInstance(Locale.GERMANY)
                val value = sumInEUR.multiply(pricePerGram.toBigDecimal())
                totalCurrentValue.text = localeEUR.format(value)
            }
        }
    }


    private fun observeCurrentGoldPriceChange() {
        mMainViewModel.currentGoldPrice.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { goldPrice ->
                        this.goldPrice = goldPrice
                        setTotalCurrentValue(goldPrice.price_gram_24k)
//                        totalCurrentValue.text = (goldPrice.price_gram_24k * sum
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
    } // end observeCurrentGoldPriceChange


    private fun getValuesFromDropdownMenus() {
        selectedWeight = autoCompleteTextViewWeight.text.toString()
        selectedCurrency = autoCompleteTextViewCurrency.text.toString()
    }


    private fun initializeLayoutViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        adapter = PortfolioAdapter(dataSet)
        autoCompleteTextViewWeight = view.findViewById(R.id.auto_complete_text_view_weight)
        autoCompleteTextViewCurrency = view.findViewById(R.id.auto_complete_text_view_currency)
        totalPurchasePriceValue = view.findViewById(R.id.total_purchase_price_value)
        totalWeightValue = view.findViewById(R.id.total_weight_value)
        totalCurrentValue = view.findViewById(R.id.total_current_value_value)
        totalProfitValue = view.findViewById(R.id.total_profit_value)
    }


    private fun handleRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }



}