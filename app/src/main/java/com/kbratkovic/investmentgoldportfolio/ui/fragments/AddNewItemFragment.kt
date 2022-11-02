package com.kbratkovic.investmentgoldportfolio.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import com.google.android.material.textfield.TextInputEditText
import com.kbratkovic.investmentgoldportfolio.R
import com.kbratkovic.investmentgoldportfolio.models.InvestmentItem
import com.kbratkovic.investmentgoldportfolio.ui.MainViewModel


class AddNewItemFragment : Fragment() {

    private lateinit var itemName: TextInputEditText
    private lateinit var textView: TextView
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

        //        val repository = Repository(AppDatabase.getDatabase(requireContext().applicationContext))
//        val viewModelProviderFactory = ViewModelProviderFactory(repository)
//        mAddNewItemViewModel = ViewModelProvider(this, viewModelProviderFactory).get(AddNewItemViewModel::class.java)

//        mAddNewItemViewModel = ViewModelProvider(this)[AddNewItemViewModel::class.java]


//        mAddNewItemViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return inflater.inflate(R.layout.fragment_addnewitem, container, false)


    } // end onCreateView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeLayoutViews(view)

        val items: LiveData<List<InvestmentItem>> = mMainViewModel.allInvestmentItems
    } // end onViewCreated


    private fun initializeLayoutViews(view: View) {
        itemName = view.findViewById(R.id.editTextItemName)
        textView = view.findViewById(R.id.text_addNewItem)
    }
}