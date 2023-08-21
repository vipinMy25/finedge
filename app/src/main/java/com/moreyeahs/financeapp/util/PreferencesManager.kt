package com.moreyeahs.financeapp.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

class PreferencesManager(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences_manager")

    suspend fun putString(key: String, value: String) {
        context.dataStore.edit {
            it[stringPreferencesKey(key)] = value
        }
    }

    suspend fun putBoolean(key: String, value: Boolean) {
        context.dataStore.edit {
            it[booleanPreferencesKey(key)] = value
        }
    }

    suspend fun getString(key: String): String? = context.dataStore.data.first()[stringPreferencesKey(key)]

    suspend fun getBoolean(key: String): Boolean? = context.dataStore.data.first()[booleanPreferencesKey(key)]

    suspend fun clearPreferences() {
        context.dataStore.edit {
            it.clear()
        }
    }

}