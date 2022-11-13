package com.kbratkovic.investmentgoldportfolio.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kbratkovic.investmentgoldportfolio.R
import com.kbratkovic.investmentgoldportfolio.models.InvestmentItem

class PortfolioAdapter(
    private var dataSet: List<InvestmentItem>
    ) : RecyclerView.Adapter<PortfolioAdapter.ViewHolder>()
{


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView
        val itemPrice: TextView

        init {
            itemName = view.findViewById(R.id.item_name)
            itemPrice = view.findViewById(R.id.item_price)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_portfolio_adapter, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemName.text = dataSet[position].name
        holder.itemPrice.text = dataSet[position].purchasePricePerUnit.toString()
    }


    override fun getItemCount(): Int {
        return dataSet.size
    }


    fun sendDataToAdapter(dataSet: List<InvestmentItem>) {
        this.dataSet = dataSet
        notifyDataSetChanged()
    }

}