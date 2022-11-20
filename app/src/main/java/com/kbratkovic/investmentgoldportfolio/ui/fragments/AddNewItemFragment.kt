package com.kbratkovic.investmentgoldportfolio.ui.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.kbratkovic.investmentgoldportfolio.R
import com.kbratkovic.investmentgoldportfolio.models.InvestmentItem
import com.kbratkovic.investmentgoldportfolio.ui.MainViewModel
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.CONVERT_TROY_OUNCE_CODE
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.CURRENCY_EUR_CODE
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.CURRENCY_USD_CODE
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.WEIGHT_GRAM_CODE
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.WEIGHT_TROY_OUNCE_CODE
import com.kbratkovic.investmentgoldportfolio.util.Resource
import com.kbratkovic.investmentgoldportfolio.util.Utils
import timber.log.Timber
import java.util.*


class AddNewItemFragment : Fragment() {

    private lateinit var textViewItemsPurchased: TextView
    private lateinit var textViewItemPrice: TextView

    private lateinit var editTextItemName: EditText
    private lateinit var editTextWeight: EditText
    private lateinit var editTextUnitsPurchased: EditText
    private lateinit var editTextItemPrice: EditText

    private lateinit var menuCurrency: TextInputLayout
    private lateinit var menuMetal: TextInputLayout
    private lateinit var menuWeight: TextInputLayout

    private lateinit var layoutWeight: LinearLayout
    private lateinit var layoutPurchased: LinearLayout
    private lateinit var layoutPrice: LinearLayout

    private lateinit var autoCompleteTextViewMetal: AutoCompleteTextView
    private lateinit var autoCompleteTextViewWeight: AutoCompleteTextView
    private lateinit var autoCompleteTextViewCurrency: AutoCompleteTextView

    private lateinit var buttonSave: MaterialButton

    private var selectedMetal = "Gold"
    private var selectedWeight = "gram"
    private var selectedCurrency = "EUR"

    private var usdExchangeRate: Double = 0.0
    private var eurExchangeRate: Double = 0.0

    private var investmentItem = InvestmentItem()

    private val mMainViewModel: MainViewModel by activityViewModels()


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    } // end onCreate

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
        handleButtonSave(view)
        handleEditTextFocusListeners()


        mMainViewModel.currencyRates.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                   response.data?.let {
                       usdExchangeRate = it.rates.USD
                       eurExchangeRate = it.rates.EUR

//                       when (selectedCurrency) {
//                           CURRENCY_USD_CODE -> {
//                               val usdExchangeRate = it.rates.USD
//                           }
//                           CURRENCY_EUR_CODE -> {
//                               val eurExchangeRate = it.rates.EUR
//                           }
//                       }
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

    } // end onViewCreated


//    private fun convertUSDToEUR(usd: Double) : String {
//
//    }

    override fun onResume() {
        super.onResume()
        mMainViewModel.getCurrencyRates(selectedCurrency)
    }

    private fun handleEditTextFocusListeners() {
        editTextItemName.requestFocus()
        editTextItemName.setOnFocusChangeListener { view, b ->
            if (!TextUtils.isEmpty(editTextItemName.text.toString()))
                editTextUnitsPurchased.setText("1")
        }

        editTextWeight.setOnFocusChangeListener { view, b ->
            Utils.editTextSelectAll(view as EditText)
        }

        editTextUnitsPurchased.setOnFocusChangeListener { v, hasFocus ->
            Utils.editTextSelectAll(v as EditText)
        }

        editTextItemPrice.setOnFocusChangeListener { v, hasFocus ->
            Utils.editTextSelectAll(v as EditText)
        }
    }


    private fun handleButtonSave(view: View) {
        buttonSave.setOnClickListener {
            if (!checkIfAllDataIsEntered()) {
                Toast.makeText(requireContext(), "Not all data is entered!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            investmentItem.metal = selectedMetal
            investmentItem.weightMeasurement = selectedWeight
            investmentItem.currency = selectedCurrency
            investmentItem.name = editTextItemName.text.toString().trim()
            investmentItem.numberOfUnitsPurchased = editTextUnitsPurchased.text.toString().toInt()
            investmentItem.purchasePricePerUnit = editTextItemPrice.text.toString().toBigDecimal()

            if (selectedWeight == WEIGHT_GRAM_CODE) {
                investmentItem.weightInGrams = editTextWeight.text.toString().toDouble()
                investmentItem.weightInTroyOunce = convertGramsToTroyOunce(investmentItem.weightInGrams)
            }

            if (selectedWeight == WEIGHT_TROY_OUNCE_CODE) {
                investmentItem.weightInTroyOunce = editTextWeight.text.toString().toDouble()
                investmentItem.weightInGrams = convertTroyOunceToGrams(investmentItem.weightInTroyOunce)
            }

            when (selectedCurrency) {
                CURRENCY_USD_CODE -> {
//                    val locale = NumberFormat.getCurrencyInstance(Locale.GERMANY)
                    investmentItem.purchasePriceInUSD = editTextItemPrice.text.toString().toBigDecimal()
//                    investmentItem.purchasePriceInEUR = locale.format(investmentItem.purchasePriceInUSD).toString().toBigDecimal()
                }
                CURRENCY_EUR_CODE -> {
                    investmentItem.purchasePriceInEUR = editTextItemPrice.text.toString().toBigDecimal()
                }
            }

            saveInvestmentItem(investmentItem)
        }
    }


    private fun saveInvestmentItem(item: InvestmentItem) {
        mMainViewModel.addInvestmentItem(item)
        clearInputFields()
    }


    private fun convertGramsToTroyOunce(weightInGrams: Double) : Double {
        return weightInGrams / CONVERT_TROY_OUNCE_CODE
    }

    private fun convertTroyOunceToGrams(weightInTroyOunce: Double) : Double {
        return weightInTroyOunce * CONVERT_TROY_OUNCE_CODE
    }


    private fun clearInputFields() {
        editTextItemName.text.clear()
        editTextItemName.requestFocus()
        editTextWeight.setText(getString(R.string.zero_value))
        editTextUnitsPurchased.setText(getString(R.string.zero_value))
        editTextItemPrice.setText(getString(R.string.zero_value))
    }

    private fun checkIfAllDataIsEntered(): Boolean {
        if (TextUtils.isEmpty(editTextItemName.text.toString()) || TextUtils.isEmpty(editTextWeight.text.toString()) ||
            TextUtils.isEmpty(editTextUnitsPurchased.text.toString()) || TextUtils.isEmpty(editTextItemPrice.text.toString())) {
            return false
        }
        return true
    }

    private fun handleDropDownMenus() {
        val metalDropdownList = resources.getStringArray(R.array.metal_items)
        val weightDropdownList = resources.getStringArray(R.array.weight_items)
        val currencyDropdownList = resources.getStringArray(R.array.currency_items)

        val arrayAdapterMetal = ArrayAdapter(requireContext(), R.layout.item_menu_dropdown, metalDropdownList)
        autoCompleteTextViewMetal.setAdapter(arrayAdapterMetal)

        val arrayAdapterWeight = ArrayAdapter(requireContext(), R.layout.item_menu_dropdown, weightDropdownList)
        autoCompleteTextViewWeight.setAdapter(arrayAdapterWeight)

        val arrayAdapterCurrency = ArrayAdapter(requireContext(), R.layout.item_menu_dropdown, currencyDropdownList)
        autoCompleteTextViewCurrency.setAdapter(arrayAdapterCurrency)

        autoCompleteTextViewMetal.onItemClickListener = object: AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 >= 0) {
                    selectedMetal = p0?.getItemAtPosition(p2) as String
                    investmentItem.metal = selectedMetal
                }
            }
        }

        autoCompleteTextViewWeight.onItemClickListener = object: AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedWeight = p0?.getItemAtPosition(p2) as String
                investmentItem.weightMeasurement = selectedWeight
            }
        }

        autoCompleteTextViewCurrency.onItemClickListener = object: AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 >= 0) {
                    selectedCurrency = p0?.getItemAtPosition(p2) as String
                    investmentItem.currency = selectedCurrency
                }
            }
        }
    }


    private fun initializeLayoutViews(view: View) {
        textViewItemsPurchased = view.findViewById(R.id.text_view_items_purchased)
        textViewItemPrice = view.findViewById(R.id.text_view_item_price)

        editTextItemName = view.findViewById(R.id.edit_text_item_name)
        editTextWeight = view.findViewById(R.id.edit_text_weight)
        editTextUnitsPurchased = view.findViewById(R.id.edit_text_units_purchased)
        editTextItemPrice = view.findViewById(R.id.edit_text_item_price)

        menuCurrency = view.findViewById(R.id.menu_currency)
        menuMetal = view.findViewById(R.id.menu_metal)
        menuWeight = view.findViewById(R.id.menu_weight)

        layoutWeight = view.findViewById(R.id.layout_weight)
        layoutPurchased = view.findViewById(R.id.layout_purchased)
        layoutPrice = view.findViewById(R.id.layout_price)

        autoCompleteTextViewMetal = view.findViewById(R.id.auto_complete_text_view_metal)
        autoCompleteTextViewWeight = view.findViewById(R.id.auto_complete_text_view_weight)
        autoCompleteTextViewCurrency = view.findViewById(R.id.auto_complete_text_view_currency)

        buttonSave = view.findViewById(R.id.button_save)
    }
}