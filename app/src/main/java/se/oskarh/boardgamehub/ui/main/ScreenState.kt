package se.oskarh.boardgamehub.ui.main

import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.db.suggestion.Suggestion

sealed class ScreenState

data class ShowSuggestions(val suggestions: List<Suggestion>, val recentBoardGames: List<BoardGame>) : ScreenState() {
    val hasSuggestions = suggestions.isNotEmpty() || recentBoardGames.isNotEmpty()
}

object ShowFeed : ScreenState()

object ShowSearch : ScreenState()
