package se.oskarh.boardgamehub.db

import androidx.room.TypeConverter
import se.oskarh.boardgamehub.api.GameType

class GameTypeConverter {

    @TypeConverter
    fun gameTypeToInt(gameType: GameType) = gameType.ordinal

    @TypeConverter
    fun intToGameType(ordinal: Int) = GameType.values()[ordinal]
}