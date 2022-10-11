package com.specindia.ecommerce.datastore.implementation

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Base64.encodeToString
import androidx.core.content.edit
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.specindia.ecommerce.datastore.abstraction.DataStoreRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject


private const val PREFERENCE_NAME = "ecommerce_preference"
var masterKeyAliasEncrypted = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

class EncryptedDataStoreRepositoryImpl @Inject constructor(
    private val context: Context
) : DataStoreRepository {

    val encryptedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        PREFERENCE_NAME,
        masterKeyAliasEncrypted,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override suspend fun putString(key: String, value: String) {
        encryptedPreferences.edit().apply {
            putString(key, value)
        }.apply()
    }

    override suspend fun putInt(key: String, value: Int) {
        encryptedPreferences.edit().apply {
            putInt(key, value)
        }.apply()
    }

    override suspend fun putFloat(key: String, value: Float) {
        encryptedPreferences.edit().apply {
            putFloat(key, value)
        }.apply()
    }

    override suspend fun putLong(key: String, value: Long) {
        encryptedPreferences.edit().apply {
            putLong(key, value)
        }.apply()
    }

    override suspend fun putBoolean(key: String, value: Boolean) {
        encryptedPreferences.edit().apply {
            putBoolean(key, value)
        }.apply()
    }

    override suspend fun clearPreferences() {
        encryptedPreferences.edit {
            clear()
        }
    }

    override suspend fun getString(key: String): String? {
        return encryptedPreferences.getString(key, null)
    }

    override suspend fun getInt(key: String): Int? {
        return encryptedPreferences.getInt(key, 0)
    }

    override suspend fun getFloat(key: String): Float? {
        return encryptedPreferences.getFloat(key, 0f)
    }


    override suspend fun getLong(key: String): Long {
        return encryptedPreferences.getLong(key, 0L)
    }

    override suspend fun getBoolean(key: String): Boolean? {
        return encryptedPreferences.getBoolean(key, false)
    }
}
