package com.kbratkovic.investmentgoldportfolio

import androidx.lifecycle.LiveData
import com.kbratkovic.investmentgoldportfolio.database.AppDatabase
import com.kbratkovic.investmentgoldportfolio.database.InvestmentItem
import com.kbratkovic.investmentgoldportfolio.database.InvestmentItemDao

class Repository (db: AppDatabase) {

    val getAllInvestmentItems: LiveData<List<InvestmentItem>> = db.investmentItemDao().getAllInvestmentItems()
//
//    suspend fun addInvestmentItem(item: InvestmentItem) {
//        investmentItemDao.addInvestmentItem(item)
//    }

}