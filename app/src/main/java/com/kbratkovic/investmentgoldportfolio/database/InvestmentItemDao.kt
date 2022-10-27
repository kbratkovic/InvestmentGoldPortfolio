package com.kbratkovic.investmentgoldportfolio.database

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

interface InvestmentItemDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addInvestmentItem(item: InvestmentItem)

    @Query("SELECT * FROM investment_item ORDER BY id ASC")
    suspend fun getAllInvestmentItems(): LiveData<List<InvestmentItem>>
}