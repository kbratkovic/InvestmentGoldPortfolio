package com.kbratkovic.investmentgoldportfolio.ui.adapters

import android.content.Context
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.TypedArrayUtils.getString
import androidx.recyclerview.widget.RecyclerView
import com.kbratkovic.investmentgoldportfolio.R
import com.kbratkovic.investmentgoldportfolio.domain.models.InvestmentItem
import com.kbratkovic.investmentgoldportfolio.util.Constants
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.CURRENCY_EUR_CODE
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.CURRENCY_USD_CODE
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.WEIGHT_GRAM_CODE
import com.kbratkovic.investmentgoldportfolio.util.Constants.Companion.WEIGHT_TROY_OUNCE_CODE
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*

class PortfolioAdapter(
    private var context: Context,
    private var dataSet: List<InvestmentItem>,
    private var selectedCurrency: String,
    private var selectedWeight: String
    ) : RecyclerView.Adapter<PortfolioAdapter.ViewHolder>()
{

    private val mLocaleEUR = NumberFormat.getCurrencyInstance(Locale.GERMANY)
    private val mLocaleUS = NumberFormat.getCurrencyInstance(Locale.US)

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView
        val itemMetal: TextView
        val itemWeight: TextView
        val itemUnitsPurchased: TextView
        val itemPurchasePrice: TextView

        init {
            itemName = view.findViewById(R.id.item_name)
            itemMetal = view.findViewById(R.id.item_metal)
            itemWeight = view.findViewById(R.id.item_weight)
            itemUnitsPurchased = view.findViewById(R.id.item_units_purchased)
            itemPurchasePrice = view.findViewById(R.id.item_purchase_price)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_portfolio_adapter, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemName.text = dataSet[position].name
        holder.itemMetal.text = dataSet[position].metal
        holder.itemUnitsPurchased.text = context.getString(R.string.total_units_purchased_holder, dataSet[position].numberOfUnitsPurchased.toString())

        when (selectedCurrency) {
            CURRENCY_USD_CODE -> {
                holder.itemPurchasePrice.text = context.getString(R.string.total_purchase_price_holder, mLocaleUS.format(dataSet[position].purchasePriceInUSD?.setScale(2, RoundingMode.HALF_EVEN)))
            }
            CURRENCY_EUR_CODE -> {
                holder.itemPurchasePrice.text = context.getString(R.string.total_purchase_price_holder, mLocaleEUR.format(dataSet[position].purchasePriceInEUR.setScale(2, RoundingMode.HALF_EVEN)))
            }
        }

        when (selectedWeight) {
            WEIGHT_GRAM_CODE -> {
                holder.itemWeight.text =  context.getString(
                    R.string.total_weight_holder,
                    BigDecimal(dataSet[position].weightInGrams).setScale(2, RoundingMode.HALF_EVEN).toString(),
                    Constants.WEIGHT_GRAM_SHORT_CODE
                )
            }
            WEIGHT_TROY_OUNCE_CODE -> {
                holder.itemWeight.text =  context.getString(
                    R.string.total_weight_holder,
                    BigDecimal(dataSet[position].weightInTroyOunce).setScale(2, RoundingMode.HALF_EVEN).toString(),
                    Constants.WEIGHT_TROY_OUNCE_SHORT_CODE
                )
            }
        }
    } // onBindViewHolder


    override fun getItemCount(): Int {
        return dataSet.size
    }


    fun sendDataToAdapter(dataSet: List<InvestmentItem>, currency: String, weight: String) {
        this.dataSet = dataSet
        this.selectedCurrency = currency
        this.selectedWeight = weight
        notifyDataSetChanged()
    }

}