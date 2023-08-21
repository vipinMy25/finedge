package com.moreyeahs.financeapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.moreyeahs.financeapp.data.local.entity.AccountEntity
import com.moreyeahs.financeapp.data.local.entity.TransactionEntity

@Dao
interface FinanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(sms: TransactionEntity)

    @Query("SELECT * FROM TransactionEntity ORDER BY timestamp DESC")
    suspend fun getAllTransaction(): List<TransactionEntity>

    @Query("DELETE FROM TransactionEntity WHERE id = :id")
    suspend fun deleteTransaction(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(bankItem: AccountEntity)

    @Query("SELECT * FROM AccountEntity")
    suspend fun getAllAccount(): List<AccountEntity>

    @Query("UPDATE AccountEntity SET isDefault= :isDefault WHERE id = :id")
    suspend fun updateAccountDefault(id: String, isDefault: Boolean)

}