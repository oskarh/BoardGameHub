package se.oskarh.boardgamehub.db.collection

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import se.oskarh.boardgamehub.db.BaseDao

@Dao
interface BoardGameCollectionDao : BaseDao<BoardGameCollection> {

    @Query("DELETE FROM boardgame_collection_table WHERE id = :deleteId")
    suspend fun delete(deleteId: String)

    @Query("SELECT * FROM boardgame_collection_table")
    fun getAllBoardGameCollections(): LiveData<List<BoardGameCollection>>
}