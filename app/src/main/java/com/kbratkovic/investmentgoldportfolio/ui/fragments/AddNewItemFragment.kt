package com.kbratkovic.investmentgoldportfolio.ui.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.kbratkovic.investmentgoldportfolio.R
import com.kbratkovic.investmentgoldportfolio.domain.models.InvestmentItem
import com.kbratkovic.investmentgoldportfolio.domain.models.MetalPrice
import com.kbratkovic.investmentgoldportfolio.ui.MainViewModel
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.CONVERT_TROY_OUNCE_CODE
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.CURRENCY_EUR_CODE
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.CURRENCY_USD_CODE
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.WEIGHT_GRAM_CODE
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.WEIGHT_TROY_OUNCE_CODE
import com.kbratkovic.investmentgoldportfolio.util.NetworkConnection
import com.kbratkovic.investmentgoldportfolio.util.Resource
import com.kbratkovic.investmentgoldportfolio.util.Utils
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode


class AddNewItemFragment : Fragment() {

    private lateinit var mTextViewItemsPurchased: TextView
    private lateinit var mTextViewItemPrice: TextView

    private lateinit var mEditTextItemName: EditText
    private lateinit var mEditTextWeight: EditText
    private lateinit var mEditTextUnitsPurchased: EditText
    private lateinit var mEditTextItemPrice: EditText

    private lateinit var mMenuCurrency: TextInputLayout
    private lateinit var mMenuMetal: TextInputLayout
    private lateinit var mMenuWeight: TextInputLayout

    private lateinit var mLayoutWeight: LinearLayout
    private lateinit var mLayoutPurchased: LinearLayout
    private lateinit var mLayoutPrice: LinearLayout

    private lateinit var mAutoCompleteTextViewMetal: AutoCompleteTextView
    private lateinit var mAutoCompleteTextViewWeight: AutoCompleteTextView
    private lateinit var mAutoCompleteTextViewCurrency: AutoCompleteTextView

    private lateinit var mButtonSave: MaterialButton

    private var mSelectedMetal = ""
    private var mSelectedWeight = ""
    private var mSelectedCurrency = ""

    private var mExchangeRateUSD = 0.0
    private var mExchangeRateEUR = 0.0

    private var mInvestmentItem = InvestmentItem()

    private val mMainViewModel: MainViewModel by activityViewModels()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(R.layout.fragment_addnewitem, container, false)
    } // end onCreateView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeLayoutViews(view)
        handleDropDownMenus()
        handleButtonSave()
        handleEditTextFocusListeners()
        observeCurrentGoldPriceChangeFromMetalPriceApiCom()

    } // end onViewCreated


    override fun onResume() {
        super.onResume()
        handleDropDownMenus()
        handleEditTextFocusListeners()
        getValuesFromDropdownMenus()

        if (!NetworkConnection.hasInternetConnection(requireContext())) {
            mButtonSave.visibility = View.GONE
            val bottomNavigationView: BottomNavigationView? = activity?.findViewById(R.id.bottom_navigation)
            if (bottomNavigationView != null) {
                Utils.showSnackBar(requireActivity().findViewById(android.R.id.content),
                    getString(R.string.error_network_connection), bottomNavigationView)
            }
        }
        else {
            mButtonSave.visibility = View.VISIBLE
        }
    }


    private fun getValuesFromDropdownMenus() {
        mSelectedMetal = mAutoCompleteTextViewMetal.text.toString()
        mSelectedWeight = mAutoCompleteTextViewWeight.text.toString()
        mSelectedCurrency = mAutoCompleteTextViewCurrency.text.toString()
    }


    private fun handleEditTextFocusListeners() {
        mEditTextItemName.requestFocus()
        mEditTextItemName.setOnFocusChangeListener { view, b ->
            if (!TextUtils.isEmpty(mEditTextItemName.text.toString()))
                mEditTextUnitsPurchased.setText("1")
        }

        mEditTextWeight.setOnFocusChangeListener { view, b ->
            Utils.editTextSelectAll(view as EditText)
        }

        mEditTextUnitsPurchased.setOnFocusChangeListener { v, hasFocus ->
            Utils.editTextSelectAll(v as EditText)
        }

        mEditTextItemPrice.setOnFocusChangeListener { v, hasFocus ->
            Utils.editTextSelectAll(v as EditText)
        }
    }


    private fun observeCurrentGoldPriceChangeFromMetalPriceApiCom() {
        mMainViewModel.metalPrice.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { metalPrice ->
                        calculateExchangeRates(metalPrice)
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


    private fun calculateExchangeRates(metalPrice: MetalPrice) {
        mExchangeRateUSD = (1).div(metalPrice.rates.EUR)
        mExchangeRateEUR = metalPrice.rates.EUR
    } // calculateExchangeRatesAndGoldPrices


    private fun handleButtonSave() {
        mButtonSave.setOnClickListener {
            if (!checkIfAllDataIsEntered()) {
                Toast.makeText(requireContext(), getString(R.string.error_not_all_data_entered), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val unitsPurchased = mEditTextUnitsPurchased.text.toString().toBigDecimal()

            mInvestmentItem.metal = mSelectedMetal
            mInvestmentItem.weightMeasurement = mSelectedWeight
            mInvestmentItem.name = mEditTextItemName.text.toString().trim()
            mInvestmentItem.numberOfUnitsPurchased = mEditTextUnitsPurchased.text.toString().toInt()
            mInvestmentItem.epochtime = System.currentTimeMillis()

            if (mSelectedWeight == WEIGHT_GRAM_CODE) {
                mInvestmentItem.weightInGrams = mEditTextWeight.text.toString().toDouble().times(unitsPurchased.toDouble())
                mInvestmentItem.weightInTroyOunce = convertGramsToTroyOunce(mInvestmentItem.weightInGrams)
            }

            if (mSelectedWeight == WEIGHT_TROY_OUNCE_CODE) {
                mInvestmentItem.weightInTroyOunce = mEditTextWeight.text.toString().toDouble().times(unitsPurchased.toDouble())
                mInvestmentItem.weightInGrams = convertTroyOunceToGrams(mInvestmentItem.weightInTroyOunce)
            }

            when (mSelectedCurrency) {
                CURRENCY_USD_CODE -> {
                    val priceInUSD = mEditTextItemPrice.text.toString().toBigDecimal().multiply(unitsPurchased)
                    mInvestmentItem.purchasePriceInUSD = priceInUSD
                    mInvestmentItem.purchasePriceInEUR = convertUSDToEUR(priceInUSD)
                }
                CURRENCY_EUR_CODE -> {
                    val priceInEUR = mEditTextItemPrice.text.toString().toBigDecimal().multiply(unitsPurchased)
                    mInvestmentItem.purchasePriceInEUR = priceInEUR
                    mInvestmentItem.purchasePriceInUSD = convertEURToUSD(priceInEUR)
                }
            }

            if (!NetworkConnection.hasInternetConnection(requireContext())) {
                val bottomNavigationView: BottomNavigationView? = activity?.findViewById(R.id.bottom_navigation)
                if (bottomNavigationView != null) {
                    Utils.showSnackBar(requireActivity().findViewById(android.R.id.content),
                        getString(R.string.error_network_connection), bottomNavigationView)
                }
            } else {
                saveInvestmentItem(mInvestmentItem)
            }

        }
    } // handleButtonSave


    private fun saveInvestmentItem(item: InvestmentItem) {
        mMainViewModel.addInvestmentItem(item)
        clearInputFields()
    }


    private fun convertUSDToEUR(priceInUSD: BigDecimal) : BigDecimal {
        try {
            return priceInUSD.multiply(mExchangeRateEUR.toBigDecimal()).setScale(2, RoundingMode.HALF_EVEN)
        }
        catch (e: Exception) {
            Timber.e(e.localizedMessage)
        }
        return BigDecimal.valueOf(0.0)
    }


    private fun convertEURToUSD(priceInEUR: BigDecimal) : BigDecimal {
        try {
            return priceInEUR.multiply(mExchangeRateUSD.toBigDecimal()).setScale(2, RoundingMode.HALF_EVEN)
        }
        catch (e: Exception) {
            Timber.e(e.localizedMessage)
        }
        return BigDecimal.valueOf(0.0)
    }


    private fun convertGramsToTroyOunce(weightInGrams: Double) : Double {
        val double = weightInGrams.div(CONVERT_TROY_OUNCE_CODE)
        val decimal = BigDecimal(double).setScale(2, RoundingMode.HALF_EVEN)
        return decimal.toDouble()
    }


    private fun convertTroyOunceToGrams(weightInTroyOunce: Double) : Double {
        val double = weightInTroyOunce * CONVERT_TROY_OUNCE_CODE
        val decimal = BigDecimal(double).setScale(2, RoundingMode.HALF_EVEN)
        return decimal.toDouble()
    }


    private fun clearInputFields() {
        mEditTextItemName.text.clear()
        mEditTextItemName.requestFocus()
        mEditTextWeight.setText(getString(R.string.zero_value))
        mEditTextUnitsPurchased.setText(getString(R.string.zero_value))
        mEditTextItemPrice.setText(getString(R.string.zero_value))
    }


    private fun checkIfAllDataIsEntered(): Boolean {
        if (TextUtils.isEmpty(mEditTextItemName.text.toString()) || TextUtils.isEmpty(mEditTextWeight.text.toString()) ||
            TextUtils.isEmpty(mEditTextUnitsPurchased.text.toString()) || TextUtils.isEmpty(mEditTextItemPrice.text.toString())) {
            return false
        }
        return true
    }


    private fun handleDropDownMenus() {
        val metalDropdownList = resources.getStringArray(R.array.metal_items)
        val weightDropdownList = resources.getStringArray(R.array.weight_items)
        val currencyDropdownList = resources.getStringArray(R.array.currency_items)

        val arrayAdapterMetal = ArrayAdapter(requireContext(), R.layout.item_menu_dropdown, metalDropdownList)
        mAutoCompleteTextViewMetal.setAdapter(arrayAdapterMetal)

        mAutoCompleteTextViewMetal.onItemClickListener = object: AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 >= 0) {
                    mSelectedMetal = p0?.getItemAtPosition(p2) as String
                    mInvestmentItem.metal = mSelectedMetal
                }
            }
        }

        val arrayAdapterWeight = ArrayAdapter(requireContext(), R.layout.item_menu_dropdown, weightDropdownList)
        mAutoCompleteTextViewWeight.setAdapter(arrayAdapterWeight)

        mAutoCompleteTextViewWeight.onItemClickListener = object: AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                mSelectedWeight = p0?.getItemAtPosition(p2) as String
                mInvestmentItem.weightMeasurement = mSelectedWeight
            }
        }

        val arrayAdapterCurrency = ArrayAdapter(requireContext(), R.layout.item_menu_dropdown, currencyDropdownList)
        mAutoCompleteTextViewCurrency.setAdapter(arrayAdapterCurrency)

        mAutoCompleteTextViewCurrency.onItemClickListener = object: AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 >= 0) {
                    mSelectedCurrency = p0?.getItemAtPosition(p2) as String
                }
            }
        }
    } // handleDropDownMenus


    private fun initializeLayoutViews(view: View) {
        mTextViewItemsPurchased = view.findViewById(R.id.text_view_items_purchased)
        mTextViewItemPrice = view.findViewById(R.id.text_view_item_price)

        mEditTextItemName = view.findViewById(R.id.edit_text_item_name)
        mEditTextWeight = view.findViewById(R.id.edit_text_weight)
        mEditTextUnitsPurchased = view.findViewById(R.id.edit_text_units_purchased)
        mEditTextItemPrice = view.findViewById(R.id.edit_text_item_price)

        mMenuCurrency = view.findViewById(R.id.menu_currency)
        mMenuMetal = view.findViewById(R.id.menu_metal)
        mMenuWeight = view.findViewById(R.id.menu_weight)

        mLayoutWeight = view.findViewById(R.id.layout_weight)
        mLayoutPurchased = view.findViewById(R.id.layout_purchased)
        mLayoutPrice = view.findViewById(R.id.layout_price)

        mAutoCompleteTextViewMetal = view.findViewById(R.id.auto_complete_text_view_metal)
        mAutoCompleteTextViewWeight = view.findViewById(R.id.auto_complete_text_view_weight)
        mAutoCompleteTextViewCurrency = view.findViewById(R.id.auto_complete_text_view_currency)

        mButtonSave = view.findViewById(R.id.button_save)
    } // initializeLayoutViews

}