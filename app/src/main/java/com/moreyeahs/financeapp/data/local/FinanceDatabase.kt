package com.moreyeahs.financeapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.moreyeahs.financeapp.data.local.dao.FinanceDao
import com.moreyeahs.financeapp.data.local.entity.AccountEntity
import com.moreyeahs.financeapp.data.local.entity.TransactionEntity

@Database(entities = [TransactionEntity::class, AccountEntity::class], version = 1, exportSchema = false)
abstract class FinanceDatabase : RoomDatabase() {
    abstract fun getFinanceDao(): FinanceDao
    //
}