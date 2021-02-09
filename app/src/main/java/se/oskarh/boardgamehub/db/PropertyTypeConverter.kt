package se.oskarh.boardgamehub.db

import androidx.room.TypeConverter
import se.oskarh.boardgamehub.db.boardgamemeta.PropertyType

class PropertyTypeConverter {

    @TypeConverter
    fun toInt(value: PropertyType?) = value?.ordinal ?: -1

    @TypeConverter
    fun toPropertyType(value: Int) = PropertyType.values().getOrNull(value)
}