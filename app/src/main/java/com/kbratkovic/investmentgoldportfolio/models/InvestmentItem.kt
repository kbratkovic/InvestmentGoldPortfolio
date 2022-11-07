package com.kbratkovic.investmentgoldportfolio.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "investment_item")
data class InvestmentItem(
    @PrimaryKey
    val id: Int,
    val name: String,
    val metal: String,
    val weight:String,
    val currency: String,
    val numberOfUnitsPurchased: Int,
    val purchasePricePerUnit: Double
)
