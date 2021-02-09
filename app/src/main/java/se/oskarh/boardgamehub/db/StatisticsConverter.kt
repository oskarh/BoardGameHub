package se.oskarh.boardgamehub.db

import androidx.room.TypeConverter
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.util.extension.deserialize
import se.oskarh.boardgamehub.util.extension.serialize

class StatisticsConverter {

    @TypeConverter
    fun statisticsToJson(statistics: BoardGame.Statistics): String =
        serialize(statistics)

    @TypeConverter
    fun jsonToStatistics(statistics: String): BoardGame.Statistics =
        deserialize(statistics)
}