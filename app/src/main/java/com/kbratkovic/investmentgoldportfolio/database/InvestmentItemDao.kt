package com.kbratkovic.investmentgoldportfolio.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kbratkovic.investmentgoldportfolio.domain.models.InvestmentItem

@Dao
interface InvestmentItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addInvestmentItem(item: InvestmentItem)


    @Query("SELECT * FROM investment_item WHERE deleted = 0 ORDER BY id ASC")
    fun getAllInvestmentItems(): LiveData<List<InvestmentItem>>


    @Query("UPDATE investment_item SET deleted = 1 WHERE id = :id")
    suspend fun deleteInvestmentItem(id: Int)


    @Query("UPDATE investment_item SET deleted = 0 WHERE id = :id")
    suspend fun undoDeleteInvestmentItem(id: Int)

}