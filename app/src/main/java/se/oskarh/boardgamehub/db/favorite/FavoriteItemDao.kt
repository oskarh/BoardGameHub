package se.oskarh.boardgamehub.db.favorite

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import se.oskarh.boardgamehub.db.BaseDao

@Dao
interface FavoriteItemDao : BaseDao<FavoriteItem> {

    // TODO: Keep the items of the intersection of the old and new list so we don't delete and insert them for no reason?
    @Transaction
    suspend fun createNewCollection(favorites: List<FavoriteItem>) {
        deleteAll(FavoriteType.BOARDGAME)
        insertAll(favorites)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(favorites: List<FavoriteItem>)

    @Query("SELECT * FROM favorite_table WHERE favoriteType = :favoriteType AND id = :id")
    fun getFavorite(favoriteType: FavoriteType, id: String): LiveData<FavoriteItem?>

    @Query("SELECT id FROM favorite_table WHERE favoriteType = :favoriteType")
    fun getAllFavoriteIds(favoriteType: FavoriteType): LiveData<List<Int>>

    @Query("DELETE FROM favorite_table WHERE id = :deleteId AND favoriteType = :favoriteType")
    suspend fun delete(favoriteType: FavoriteType, deleteId: Int)

    @Query("DELETE FROM favorite_table WHERE favoriteType = :favoriteType")
    suspend fun deleteAll(favoriteType: FavoriteType)
}