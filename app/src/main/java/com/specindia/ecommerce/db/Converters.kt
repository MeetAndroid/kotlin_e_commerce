package com.specindia.ecommerce.db

import androidx.room.TypeConverter
import com.specindia.ecommerce.models.Source

class Converters {

    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name, name)
    }
}