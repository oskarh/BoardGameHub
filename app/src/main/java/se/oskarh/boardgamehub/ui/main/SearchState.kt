package se.oskarh.boardgamehub.ui.main

import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.db.suggestion.Suggestion
import se.oskarh.boardgamehub.util.MINIMUM_QUERY_LENGTH

data class SearchState(
    val hasFocus: Boolean = false,
    val searchQueryCount: Int = 0,
    val suggestions : List<Suggestion> = emptyList(),
    val recentBoardGames : List<BoardGame> = emptyList()) {

    val visibilityType = when {
        searchQueryCount >= MINIMUM_QUERY_LENGTH -> ShowSearch
        hasFocus && searchQueryCount < MINIMUM_QUERY_LENGTH -> ShowSuggestions(suggestions, recentBoardGames)
        else -> ShowFeed
    }

    val isShowingSuggestions = searchQueryCount < MINIMUM_QUERY_LENGTH

    val isQueryClearVisible = searchQueryCount > 0

    val isSearchBackVisible = hasFocus
}