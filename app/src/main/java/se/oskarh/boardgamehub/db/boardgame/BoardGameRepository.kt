package se.oskarh.boardgamehub.db.boardgame

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

class BoardGameRepository(private val boardGameDao: BoardGameDao) {

    suspend fun insert(vararg boardGame: BoardGame) =
        boardGameDao.insertOrUpdate(*boardGame)

    suspend fun updateVideos(id: Int, videos: List<BoardGame.Video>) =
        boardGameDao.updateVideos(id, videos)

    suspend fun updateOrInsertBoardGame(boardGame: BoardGame) {
        if (updateBoardGame(boardGame) == 0) {
            insert(boardGame)
        }
    }

    private suspend fun updateBoardGame(boardGame: BoardGame) =
        boardGameDao.updateBoardGame(
            boardGame.id,
            boardGame.names,
            boardGame.type,
            boardGame.yearPublished,
            boardGame.description,
            boardGame.imageUrl,
            boardGame.thumbnailUrl,
            boardGame.minPlayers,
            boardGame.maxPlayers,
            boardGame.minAge,
            boardGame.playingTime,
            boardGame.minPlayTime,
            boardGame.maxPlayTime,
            boardGame.statistics,
            boardGame.videos,
            boardGame.links,
            boardGame.polls,
            boardGame.normalizedName)

    suspend fun updateStatisticsAndComments(id: Int, statistics: BoardGame.Statistics, comments: List<BoardGame.Comment>) =
        boardGameDao.updateStatisticsAndComments(id, statistics, comments)

    suspend fun updateLastViewed(id: Int) =
        boardGameDao.updateLastViewed(id)

    suspend fun removeLastViewed(id: Int) =
        boardGameDao.updateLastViewed(id, 0)

    suspend fun removeAllRecentViewed() =
        boardGameDao.removeAllRecentBoardGames()

    suspend fun getGame(id: Int) =
        boardGameDao.getGame(id)

    suspend fun getCachedGamesWithIds(ids: List<Int>): List<BoardGame> =
        // SQLite crashes if this list is too big so chunk it for db access and then merge results
        ids.chunked(500).map { subList ->
            boardGameDao.getGamesWithIds(subList)
        }.flatten()

    fun getAllRecentGames(): LiveData<List<BoardGame>> =
        boardGameDao.getAllRecentGames()

    fun getMatchingRecentGames(query: String): LiveData<List<BoardGame>> =
        boardGameDao.getMatchingRecentGames("$query*")

    fun getGamesWithIdsLive(ids: List<Int>): LiveData<List<BoardGame>> {
        Timber.d("Asking favorite games total size ${ids.size}")
        return liveData(Dispatchers.IO) {
            if (ids.size > 990) {
                // TODO: Need to do this for now as otherwise we don't get the new list of 0 elements. Fix so the chunk part is run and
                //  new LiveData returned even with an empty list
                ids.chunked(990).forEach { subList ->
                    Timber.d("Asking favorite games of size ${subList.size} ${subList.take(10).joinToString()}")
                    emitSource(boardGameDao.getGamesWithIdsLive(subList))
                }
            } else {
                emitSource(boardGameDao.getGamesWithIdsLive(ids))
            }
        }
    }

    suspend fun deleteStaleBoardGames() =
        boardGameDao.deleteStaleBoardGames()

    suspend fun deleteAllBoardGames() =
        boardGameDao.deleteAllBoardGames()
}