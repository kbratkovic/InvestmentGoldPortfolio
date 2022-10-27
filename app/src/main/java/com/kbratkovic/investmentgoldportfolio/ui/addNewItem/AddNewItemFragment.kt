package com.kbratkovic.investmentgoldportfolio.ui.addNewItem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.kbratkovic.investmentgoldportfolio.R


class AddNewItemFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_addnewitem, container, false)
        val addNewItemViewModel = ViewModelProvider(this).get(AddNewItemViewModel::class.java)

        val itemName: TextInputEditText = view.findViewById(R.id.editTextItemName)
        val textView: TextView = view.findViewById(R.id.text_addNewItem)

        addNewItemViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return view
    }

}