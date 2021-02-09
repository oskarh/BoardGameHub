package se.oskarh.boardgamehub.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import se.oskarh.boardgamehub.analytics.Analytics
import se.oskarh.boardgamehub.analytics.EVENT_BOARDGAME_FAVORITE
import se.oskarh.boardgamehub.analytics.EVENT_PROPERTY_IS_FAVORITE
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.db.boardgame.BoardGameRepository
import se.oskarh.boardgamehub.db.favorite.FavoriteItem
import se.oskarh.boardgamehub.db.favorite.FavoriteItemRepository
import se.oskarh.boardgamehub.db.favorite.FavoriteStatus
import se.oskarh.boardgamehub.db.favorite.FavoriteType
import se.oskarh.boardgamehub.repository.FinderRepository
import se.oskarh.boardgamehub.repository.MockedRepository
import javax.inject.Inject

class BoardGameListViewModel @Inject constructor(
    private val mockedRepository: MockedRepository,
    private val finderRepository: FinderRepository,
    private val boardGameRepository: BoardGameRepository,
    private val favoriteItemRepository: FavoriteItemRepository
) : ViewModel() {

    private val fetchedBoardGameIds = mutableSetOf<Int>()

    val boardGameDetails: LiveData<List<BoardGame>> = finderRepository.boardGameDetails

    val allFavoriteIds = favoriteItemRepository.allFavoriteIds

    // TODO: Reuse logic for prefetching between this viewmodel and MainActivityViewModel
    fun fetchDetails(ids: List<Int>) {
        val idsToFetch = ids - fetchedBoardGameIds
        finderRepository.getGames(idsToFetch)
        fetchedBoardGameIds.addAll(ids)
    }

    fun populateFromCache(ids: List<Int>) {
        viewModelScope.launch(Dispatchers.IO) {
            fetchedBoardGameIds.addAll(finderRepository.loadFromCache(ids))
        }
    }

    fun viewedGame(id: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            boardGameRepository.updateLastViewed(id)
        }

    fun toggleFavorite(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (allFavoriteIds.value.orEmpty().contains(id)) {
                Analytics.logEvent(EVENT_BOARDGAME_FAVORITE, EVENT_PROPERTY_IS_FAVORITE to false)
                favoriteItemRepository.deleteFavorite(FavoriteItem(id, FavoriteType.BOARDGAME, FavoriteStatus.OWNED))
            } else {
                Analytics.logEvent(EVENT_BOARDGAME_FAVORITE, EVENT_PROPERTY_IS_FAVORITE to true)
                favoriteItemRepository.insert(FavoriteItem(id, FavoriteType.BOARDGAME, FavoriteStatus.OWNED))
            }
        }
    }
}