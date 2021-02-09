package se.oskarh.boardgamehub.db.suggestion

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import se.oskarh.boardgamehub.db.BaseDao
import se.oskarh.boardgamehub.util.MAX_VISIBLE_SUGGESTIONS

@Dao
interface SuggestionDao : BaseDao<Suggestion> {

    @Query("DELETE FROM suggestion_table WHERE formattedSuggestion = :suggestion")
    suspend fun delete(suggestion: String)

    @Query("SELECT * FROM suggestion_table ORDER BY created DESC LIMIT :maxNumberSuggestions")
    fun getAllSuggestions(maxNumberSuggestions: Int = MAX_VISIBLE_SUGGESTIONS): LiveData<List<Suggestion>>

    @Query("SELECT * FROM suggestion_table WHERE formattedSuggestion LIKE '%' || :query || '%' ORDER BY created DESC LIMIT :maxNumberSuggestions")
    fun getMatchingSuggestions(query: String, maxNumberSuggestions: Int = MAX_VISIBLE_SUGGESTIONS): LiveData<List<Suggestion>>

    @Query("UPDATE suggestion_table SET created = :timestamp WHERE formattedSuggestion = :formattedQuery")
    suspend fun updateTimestamp(formattedQuery: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM suggestion_table")
    suspend fun deleteAllSuggestions()
}