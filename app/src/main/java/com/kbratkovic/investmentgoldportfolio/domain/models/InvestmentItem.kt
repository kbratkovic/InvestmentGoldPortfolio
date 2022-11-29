package com.kbratkovic.investmentgoldportfolio.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "investment_item")
data class InvestmentItem(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String = "",
    var metal: String = "",
    var weightMeasurement: String = "",
    var weightInGrams: Double = 0.0,
    var weightInTroyOunce: Double = 0.0,
    var numberOfUnitsPurchased: Int = 0,
    var purchasePriceInUSD: BigDecimal? = null,
    var purchasePriceInEUR: BigDecimal = BigDecimal.ZERO,
    var epochtime: Long = 0
) {
}
