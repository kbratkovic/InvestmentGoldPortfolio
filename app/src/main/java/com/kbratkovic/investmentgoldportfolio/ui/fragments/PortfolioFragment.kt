package com.kbratkovic.investmentgoldportfolio.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.kbratkovic.investmentgoldportfolio.ui.MainViewModel
import com.kbratkovic.investmentgoldportfolio.R
import com.kbratkovic.investmentgoldportfolio.models.InvestmentItem
import com.kbratkovic.investmentgoldportfolio.ui.adapters.PortfolioAdapter

//import com.kbratkovic.investmentgoldportfolio.databinding.FragmentHomeBinding

class PortfolioFragment : Fragment() {

    private var dataSet: List<InvestmentItem> = listOf()
    private val mMainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(R.layout.fragment_portfolio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = PortfolioAdapter(dataSet)
        recyclerView.adapter = adapter

        mMainViewModel.allInvestmentItems.observe(viewLifecycleOwner) {
            adapter.sendDataToAdapter(it)
        }
    }
}