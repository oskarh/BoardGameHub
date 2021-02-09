package se.oskarh.boardgamehub.db

import androidx.room.TypeConverter
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.util.extension.deserialize
import se.oskarh.boardgamehub.util.extension.serialize

class RankListConverter {

    @TypeConverter
    fun rankListToJson(rankList: List<BoardGame.Rank>): String =
        serialize(rankList)

    @TypeConverter
    fun jsonToRankList(jsonRankList: String): List<BoardGame.Rank> =
        deserialize(jsonRankList)
}