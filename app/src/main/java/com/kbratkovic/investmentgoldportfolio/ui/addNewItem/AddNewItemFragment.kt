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

class AddNewItemFragment : Fragment(R.layout.fragment_addnewitem) {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val addNewItemViewModel = ViewModelProvider(this).get(AddNewItemViewModel::class.java)


// varijable su null, ne valja ovak referenciranje
        val editTextItemName: TextInputEditText? = view?.findViewById(R.id.editTextItemName)
        val textView: TextView? = view?.findViewById(R.id.text_addNewItem)
        addNewItemViewModel.text.observe(viewLifecycleOwner) {
            textView?.text = it
        }
    return null
    }

}