package com.kbratkovic.investmentgoldportfolio.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kbratkovic.investmentgoldportfolio.R
import com.kbratkovic.investmentgoldportfolio.domain.models.InvestmentItem

class PortfolioAdapter(
    private var dataSet: List<InvestmentItem>
    ) : RecyclerView.Adapter<PortfolioAdapter.ViewHolder>()
{


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView
        val itemMetal: TextView
        val itemWeightInGrams: TextView
        val itemWeightInTroyOunce: TextView
        val itemUnitsPurchased: TextView
        val itemPurchasePriceInUSD: TextView
        val itemPurchasePriceInEUR: TextView

        init {
            itemName = view.findViewById(R.id.item_name)
            itemMetal = view.findViewById(R.id.item_metal)
            itemWeightInGrams = view.findViewById(R.id.item_weight_in_grams)
            itemWeightInTroyOunce = view.findViewById(R.id.item_weight_in_troy_ounce)
            itemUnitsPurchased = view.findViewById(R.id.item_units_purchased)
            itemPurchasePriceInUSD = view.findViewById(R.id.item_purchase_price_in_usd)
            itemPurchasePriceInEUR = view.findViewById(R.id.item_purchase_price_in_eur)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_portfolio_adapter, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemName.text = dataSet[position].name
        holder.itemMetal.text = dataSet[position].metal
        holder.itemWeightInGrams.text = dataSet[position].weightInGrams.toString()
        holder.itemWeightInTroyOunce.text = dataSet[position].weightInTroyOunce.toString()
        holder.itemUnitsPurchased.text = dataSet[position].numberOfUnitsPurchased.toString()
        holder.itemPurchasePriceInUSD.text = dataSet[position].purchasePriceInUSD.toString()
        holder.itemPurchasePriceInEUR.text = dataSet[position].purchasePriceInEUR.toString()
    }


    override fun getItemCount(): Int {
        return dataSet.size
    }


    fun sendDataToAdapter(dataSet: List<InvestmentItem>) {
        this.dataSet = dataSet
        notifyDataSetChanged()
    }

}