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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.kbratkovic.investmentgoldportfolio.R
import com.kbratkovic.investmentgoldportfolio.models.InvestmentItem
import com.kbratkovic.investmentgoldportfolio.ui.MainViewModel


class AddNewItemFragment : Fragment() {

    private lateinit var textViewItemsPurchased: TextView
    private lateinit var textViewItemPrice: TextView

    private lateinit var editTextItemName: EditText
    private lateinit var editTextWeight: EditText
    private lateinit var editTextItemsPurchased: EditText
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
    private var selectedCurrency = "USD"

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

        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        fab?.visibility = View.GONE

        initializeLayoutViews(view)
        manageDropDownMenus()

        manageButtonSave()



    } // end onViewCreated



    private fun manageButtonSave() {
        buttonSave.setOnClickListener {
            if (!checkIfAllDataIsEntered()) {
                Toast.makeText(requireContext(), "Not all data is entered!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            investmentItem.metal = selectedMetal
            investmentItem.weightMeasurement = selectedWeight
            investmentItem.currency = selectedCurrency
            investmentItem.name = editTextItemName.text.toString().trim()
            investmentItem.weight = editTextWeight.text.toString().trim()
            investmentItem.numberOfUnitsPurchased = editTextItemsPurchased.text.toString().toInt()
            investmentItem.purchasePricePerUnit = editTextItemPrice.text.toString().toDouble()

            saveInvestmentItem(investmentItem)
        }
    }

    private fun saveInvestmentItem(item: InvestmentItem) {

    }

    private fun checkIfAllDataIsEntered(): Boolean {
        if (TextUtils.isEmpty(editTextItemName.text.toString()) || TextUtils.isEmpty(editTextWeight.text.toString()) ||
            TextUtils.isEmpty(editTextItemsPurchased.text.toString()) || TextUtils.isEmpty(editTextItemPrice.text.toString())) {
            return false
        }
        return true
    }

    private fun manageDropDownMenus() {
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
        editTextItemsPurchased = view.findViewById(R.id.edit_text_items_purchased)
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