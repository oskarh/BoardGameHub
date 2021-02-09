package se.oskarh.boardgamehub.ui.main

import se.oskarh.boardgamehub.api.CollectionFilterAction
import se.oskarh.boardgamehub.api.CollectionSortAction
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.util.AppPreferences

data class FavoriteState(
    val filter: String = "",
    val allFavorites: List<BoardGame> = emptyList(),
    val favoriteIds: List<Int> = emptyList(),
    val collectionFilter: CollectionFilterAction = AppPreferences.selectedCollectionFilter,
    val collectionSort: CollectionSortAction = AppPreferences.selectedCollectionSort,
    val votedBestPlayerCount: Int = AppPreferences.bestVotedPlayerCount) {

    val isLoading = allFavorites.size != favoriteIds.size

    val filteredFavorites =
            collectionSort.sort(votedBestPlayerCount, allFavorites.filter { boardGame: BoardGame ->
                boardGame.normalizedName.contains(filter, true) && collectionFilter.isMatching(boardGame)
            })
}