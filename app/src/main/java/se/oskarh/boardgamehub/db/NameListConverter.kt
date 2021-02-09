package se.oskarh.boardgamehub.db

import androidx.room.TypeConverter
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.util.extension.deserialize
import se.oskarh.boardgamehub.util.extension.serialize

class NameListConverter {

    @TypeConverter
    fun nameListToJson(nameList: List<BoardGame.Name>): String =
        serialize(nameList)

    @TypeConverter
    fun jsonToNameList(jsonNameList: String): List<BoardGame.Name> =
        deserialize(jsonNameList)
}