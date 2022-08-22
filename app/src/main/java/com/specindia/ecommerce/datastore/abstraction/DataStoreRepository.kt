package com.specindia.ecommerce.datastore.abstraction

interface DataStoreRepository {
    suspend fun putString(key: String, value: String)
    suspend fun putInt(key: String, value: Int)
    suspend fun putFloat(key: String, value: Float)
    suspend fun putDouble(key: String, value: Double)
    suspend fun putLong(key: String, value: Long)
    suspend fun putBoolean(key: String, value: Boolean)
    suspend fun clearPreferences()

    suspend fun getString(key: String): String?
    suspend fun getInt(key: String): Int?
    suspend fun getFloat(key: String): Float?
    suspend fun getDouble(key: String): Double?
    suspend fun getLong(key: String): Long?
    suspend fun getBoolean(key: String): Boolean?
}
