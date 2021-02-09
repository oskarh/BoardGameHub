package se.oskarh.boardgamehub.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import se.oskarh.boardgamehub.db.boardgame.BoardGameRepository
import se.oskarh.boardgamehub.db.suggestion.SuggestionRepository
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val boardGameRepository: BoardGameRepository,
    private val suggestionRepository: SuggestionRepository
) : ViewModel() {

    fun clearSearchHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            suggestionRepository.deleteAllSuggestions()
            boardGameRepository.removeAllRecentViewed()
        }
    }

    fun clearCache() {
        viewModelScope.launch(Dispatchers.IO) {
            boardGameRepository.deleteAllBoardGames()
        }
    }
}