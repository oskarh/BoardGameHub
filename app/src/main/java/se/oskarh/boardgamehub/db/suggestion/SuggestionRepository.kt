package se.oskarh.boardgamehub.db.suggestion

import androidx.lifecycle.LiveData
import java.util.Locale

class SuggestionRepository(private val suggestionDao: SuggestionDao) {

    fun getAllSuggestions(): LiveData<List<Suggestion>> =
        suggestionDao.getAllSuggestions()

    fun getMatchingSuggestions(query: String): LiveData<List<Suggestion>> =
        suggestionDao.getMatchingSuggestions(query.trim().toLowerCase(Locale.ENGLISH))

    suspend fun insert(suggestion: Suggestion) =
        suggestionDao.insertOrUpdate(suggestion)

    suspend fun updateTimestamp(suggestion: Suggestion) =
        suggestionDao.updateTimestamp(suggestion.formattedSuggestion)

    suspend fun deleteSuggestion(suggestion: Suggestion) =
        suggestionDao.delete(suggestion.formattedSuggestion)


    suspend fun deleteAllSuggestions() =
        suggestionDao.deleteAllSuggestions()
}
