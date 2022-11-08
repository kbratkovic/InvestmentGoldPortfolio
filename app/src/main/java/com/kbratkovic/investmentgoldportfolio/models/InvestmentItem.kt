package com.kbratkovic.investmentgoldportfolio.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "investment_item")
data class InvestmentItem(
    @PrimaryKey
    var id: Int,
    var name: String,
    var metal: String,
    var weight:String,
    var weightMeasurement: String,
    var numberOfUnitsPurchased: Int,
    var purchasePricePerUnit: Double,
    var currency: String
) {
    constructor() : this(0, "", "", "", "", 0, 0.0, "")
}
