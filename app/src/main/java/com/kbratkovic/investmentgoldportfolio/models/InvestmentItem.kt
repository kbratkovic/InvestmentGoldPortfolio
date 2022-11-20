package com.kbratkovic.investmentgoldportfolio.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "investment_item")
data class InvestmentItem(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String,
    var metal: String,
    var weightMeasurement: String,
    var weightInGrams: Double,
    var weightInTroyOunce: Double,
    var numberOfUnitsPurchased: Int,
    var purchasePricePerUnit: BigDecimal?,
    var purchasePriceInUSD: BigDecimal?,
    var purchasePriceInEUR: BigDecimal?,
    var currency: String
) {
    constructor() : this(0, "",  "", "", 0.0, 0.0,0,
        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,"")
}
