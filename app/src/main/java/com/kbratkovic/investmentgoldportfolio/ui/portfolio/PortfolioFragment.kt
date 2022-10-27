package com.kbratkovic.investmentgoldportfolio.ui.portfolio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.kbratkovic.investmentgoldportfolio.R
import com.kbratkovic.investmentgoldportfolio.ui.addNewItem.AddNewItemViewModel

//import com.kbratkovic.investmentgoldportfolio.databinding.FragmentHomeBinding

class PortfolioFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_portfolio, container, false)

        val portfolioViewModel = ViewModelProvider(this).get(PortfolioViewModel::class.java)

        val itemName: TextInputEditText = view.findViewById(R.id.editText)
        val textView: TextView = view.findViewById(R.id.text_home)
        portfolioViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        return view
    }


}