package se.oskarh.boardgamehub.api

import se.oskarh.boardgamehub.db.boardgame.BoardGame

data class CollectionFilterAction(
    val areStandaloneGamesIncluded: Boolean,
    val areExpansionsIncluded: Boolean,
    val areAllPlayersIncluded: Boolean,
    val filteredNumberPlayers: Int) {

    private val gameTypesIncluded = mutableSetOf<GameType>().apply {
        if (areStandaloneGamesIncluded) add(GameType.BOARDGAME)
        if (areExpansionsIncluded) add(GameType.BOARDGAME_EXPANSION)
    }

    fun isMatching(boardGame: BoardGame) =
            (areAllPlayersIncluded || boardGame.playerRange().contains(filteredNumberPlayers)) &&
                    gameTypesIncluded.contains(boardGame.type)
}