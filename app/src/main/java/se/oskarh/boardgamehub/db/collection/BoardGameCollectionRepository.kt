package se.oskarh.boardgamehub.db.collection

import androidx.lifecycle.LiveData

class BoardGameCollectionRepository(private val boardGameCollectionDao: BoardGameCollectionDao) {

    suspend fun insert(boardGameCollection: BoardGameCollection) =
        boardGameCollectionDao.insertOrUpdate(boardGameCollection)

    fun getAllCollections(): LiveData<List<BoardGameCollection>> =
        boardGameCollectionDao.getAllBoardGameCollections()
}
