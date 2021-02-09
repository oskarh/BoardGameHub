package se.oskarh.boardgamehub.db

import androidx.room.TypeConverter
import se.oskarh.boardgamehub.db.favorite.FavoriteType

class FavoriteTypeConverter {

    @TypeConverter
    fun favoriteTypeToInt(favoriteType: FavoriteType) = favoriteType.ordinal

    @TypeConverter
    fun intToFavoriteType(ordinal: Int) = FavoriteType.values()[ordinal]
}