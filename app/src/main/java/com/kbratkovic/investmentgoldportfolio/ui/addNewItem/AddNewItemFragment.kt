package com.kbratkovic.investmentgoldportfolio.ui.addNewItem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.kbratkovic.investmentgoldportfolio.MainActivity
import com.kbratkovic.investmentgoldportfolio.R
import com.kbratkovic.investmentgoldportfolio.Repository
import com.kbratkovic.investmentgoldportfolio.database.AppDatabase
import com.kbratkovic.investmentgoldportfolio.database.InvestmentItem
import com.kbratkovic.investmentgoldportfolio.ui.ViewModelProviderFactory


class AddNewItemFragment : Fragment() {

    private lateinit var itemName: TextInputEditText
    private lateinit var textView: TextView
    private val mAddNewItemViewModel: AddNewItemViewModel by activityViewModels()


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    } // end onCreate

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_addnewitem, container, false)

//        val repository = Repository(AppDatabase.getDatabase(requireContext().applicationContext))
//        val viewModelProviderFactory = ViewModelProviderFactory(repository)
//        mAddNewItemViewModel = ViewModelProvider(this, viewModelProviderFactory).get(AddNewItemViewModel::class.java)

//        mAddNewItemViewModel = ViewModelProvider(this)[AddNewItemViewModel::class.java]



//        mAddNewItemViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return view


    } // end onCreateView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeLayoutViews(view)

        val items: LiveData<List<InvestmentItem>> = mAddNewItemViewModel.allInvestmentItems
    } // end onViewCreated


    private fun initializeLayoutViews(view: View) {
        itemName = view.findViewById(R.id.editTextItemName)
        textView = view.findViewById(R.id.text_addNewItem)
    }
}