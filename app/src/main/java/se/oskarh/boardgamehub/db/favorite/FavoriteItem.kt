package se.oskarh.boardgamehub.db.favorite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_table")
data class FavoriteItem(
    @PrimaryKey
    var id: Int,
    var favoriteType: FavoriteType,
    var favoriteStatus: FavoriteStatus,
    val created: Long = System.currentTimeMillis())