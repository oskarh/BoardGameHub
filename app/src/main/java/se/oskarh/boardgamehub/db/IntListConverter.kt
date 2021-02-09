package se.oskarh.boardgamehub.db

import androidx.room.TypeConverter
import se.oskarh.boardgamehub.util.extension.deserialize
import se.oskarh.boardgamehub.util.extension.serialize

class IntListConverter {

    @TypeConverter
    fun intListToJson(intList: List<Int>): String =
        serialize(intList)

    @TypeConverter
    fun jsonToIntList(jsonIntList: String): List<Int> =
        deserialize(jsonIntList)
}