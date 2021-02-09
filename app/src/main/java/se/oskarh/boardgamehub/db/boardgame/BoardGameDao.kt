package se.oskarh.boardgamehub.db.boardgame

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import se.oskarh.boardgamehub.api.GameType
import se.oskarh.boardgamehub.db.BaseDao
import se.oskarh.boardgamehub.util.CACHE_BOARDGAME_LIFETIME

@Dao
interface BoardGameDao : BaseDao<BoardGame> {

    @Query("SELECT * FROM boardgame_table ORDER BY id DESC")
    suspend fun getAllGames(): List<BoardGame>

    @Query("SELECT * FROM boardgame_table WHERE id = :id")
    suspend fun getGame(id: Int): BoardGame?

    @Query("SELECT * FROM boardgame_table WHERE id IN (:ids)")
    suspend fun getGamesWithIds(ids: List<Int>): List<BoardGame>

    @Query("SELECT * FROM boardgame_table WHERE id IN (:ids)")
    fun getGamesWithIdsLive(ids: List<Int>): LiveData<List<BoardGame>>

    @Query("SELECT * FROM boardgame_table WHERE lastViewedTimestamp > 0 ORDER BY lastViewedTimestamp DESC LIMIT 20")
    fun getAllRecentGames(): LiveData<List<BoardGame>>

    @Query("SELECT boardgame_table.* FROM boardgame_table JOIN boardgame_table_fts ON boardgame_table.id = boardgame_table_fts.docid WHERE boardgame_table.lastViewedTimestamp > 0 AND boardgame_table_fts.normalizedName MATCH :query ORDER BY lastViewedTimestamp DESC LIMIT 50")
    fun getMatchingRecentGames(query: String): LiveData<List<BoardGame>>

    @Query("""
        UPDATE boardgame_table SET 
            names = :names, 
            type = :type, 
            yearPublished = :yearPublished, 
            description = :description, 
            imageUrl= :imageUrl, 
            thumbnailUrl= :thumbnailUrl, 
            minPlayers = :minPlayers, 
            maxPlayers = :maxPlayers, 
            minAge = :minAge, 
            playingTime = :playingTime, 
            minPlayTime = :minPlayTime, 
            maxPlayTime = :maxPlayTime,
            statistics = :statistics,
            videos = :videos,
            links = :links,
            polls = :polls,
            normalizedName = :normalizedName,
            lastSync = :lastSync
        WHERE id = :boardGameId
    """)
    suspend fun updateBoardGame(boardGameId: Int,
                                names: List<BoardGame.Name>,
                                type: GameType,
                                yearPublished: Int,
                                description: String?,
                                imageUrl: String?,
                                thumbnailUrl: String?,
                                minPlayers: Int?,
                                maxPlayers: Int?,
                                minAge: Int?,
                                playingTime: Int?,
                                minPlayTime: Int?,
                                maxPlayTime: Int?,
                                statistics: BoardGame.Statistics?,
                                videos: List<BoardGame.Video>,
                                links: List<BoardGame.Link>,
                                polls: List<BoardGame.Poll>,
                                normalizedName: String,
                                lastSync: Long = System.currentTimeMillis()): Int

    @Query("UPDATE boardgame_table SET statistics = :statistics, comments = :newComments WHERE id = :boardGameId")
    suspend fun updateStatisticsAndComments(boardGameId: Int, statistics: BoardGame.Statistics, newComments: List<BoardGame.Comment>)

    @Query("UPDATE boardgame_table SET videos = :newVideos WHERE id = :boardGameId")
    suspend fun updateVideos(boardGameId: Int, newVideos: List<BoardGame.Video>)

    @Query("UPDATE boardgame_table SET lastViewedTimestamp = :timestamp WHERE id = :boardGameId")
    suspend fun updateLastViewed(boardGameId: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE boardgame_table SET lastViewedTimestamp = 0 WHERE lastViewedTimestamp > 0")
    suspend fun removeAllRecentBoardGames()

    @Query("DELETE FROM boardgame_table WHERE created < :createdThreshold AND lastViewedTimestamp < :lastViewedThreshold")
    suspend fun deleteStaleBoardGames(createdThreshold: Long = (System.currentTimeMillis() - CACHE_BOARDGAME_LIFETIME),
                                      lastViewedThreshold: Long = (System.currentTimeMillis() - CACHE_BOARDGAME_LIFETIME))

    @Query("DELETE FROM boardgame_table")
    suspend fun deleteAllBoardGames()
}