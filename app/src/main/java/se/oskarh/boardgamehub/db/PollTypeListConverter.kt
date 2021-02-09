package se.oskarh.boardgamehub.db

import androidx.room.TypeConverter
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.util.extension.deserialize
import se.oskarh.boardgamehub.util.extension.serialize

class PollTypeListConverter {

    @TypeConverter
    fun pollTypeListToJson(pollTypes: List<BoardGame.Poll>): String =
        serialize(pollTypes)

    @TypeConverter
    fun jsonToPollTypeList(jsonPollTypes: String): List<BoardGame.Poll> =
        deserialize(jsonPollTypes)
}
