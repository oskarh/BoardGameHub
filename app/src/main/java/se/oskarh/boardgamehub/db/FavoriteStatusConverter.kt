package se.oskarh.boardgamehub.db

import androidx.room.TypeConverter
import se.oskarh.boardgamehub.db.favorite.FavoriteStatus

class FavoriteStatusConverter {

    @TypeConverter
    fun favoriteStatusToInt(favoriteStatus: FavoriteStatus) = favoriteStatus.ordinal

    @TypeConverter
    fun intToFavoriteStatus(ordinal: Int) = FavoriteStatus.values()[ordinal]
}