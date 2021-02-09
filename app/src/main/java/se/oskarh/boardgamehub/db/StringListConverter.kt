package se.oskarh.boardgamehub.db

import androidx.room.TypeConverter
import se.oskarh.boardgamehub.util.extension.deserialize
import se.oskarh.boardgamehub.util.extension.serialize

class StringListConverter {

    @TypeConverter
    fun stringListToJson(stringList: List<String>): String =
        serialize(stringList)

    @TypeConverter
    fun jsonToStringList(jsonStringList: String): List<String> =
        deserialize(jsonStringList)
}