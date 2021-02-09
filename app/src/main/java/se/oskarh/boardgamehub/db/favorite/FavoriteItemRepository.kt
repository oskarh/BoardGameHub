package se.oskarh.boardgamehub.db.favorite

import androidx.lifecycle.LiveData

class FavoriteItemRepository(private val favoriteItemDao: FavoriteItemDao) {

    suspend fun insert(favoriteItem: FavoriteItem) =
        favoriteItemDao.insertOrUpdate(favoriteItem)

    suspend fun insert(favoriteItems: List<FavoriteItem>) =
        favoriteItemDao.insertAll(favoriteItems)

    suspend fun importCollection(isOldCollectionKept: Boolean, favoriteItems: List<FavoriteItem>) {
        if (isOldCollectionKept) {
            favoriteItemDao.insertAll(favoriteItems)
        } else {
            favoriteItemDao.createNewCollection(favoriteItems)
        }
    }

    suspend fun deleteFavorite(favoriteItem: FavoriteItem) =
        favoriteItemDao.delete(favoriteItem.favoriteType, favoriteItem.id)

    fun getFavorite(favoriteType: FavoriteType = FavoriteType.BOARDGAME, id: Int): LiveData<FavoriteItem?> =
        favoriteItemDao.getFavorite(favoriteType, id.toString())

    val allFavoriteIds: LiveData<List<Int>> = favoriteItemDao.getAllFavoriteIds(FavoriteType.BOARDGAME)
}
