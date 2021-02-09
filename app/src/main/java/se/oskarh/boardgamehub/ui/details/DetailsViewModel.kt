package se.oskarh.boardgamehub.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import se.oskarh.boardgamehub.analytics.Analytics
import se.oskarh.boardgamehub.analytics.EVENT_BOARDGAME_FAVORITE
import se.oskarh.boardgamehub.analytics.EVENT_PROPERTY_IS_FAVORITE
import se.oskarh.boardgamehub.api.model.youtube.YouTubeVideoDetails
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.db.boardgame.BoardGameRepository
import se.oskarh.boardgamehub.db.favorite.FavoriteItem
import se.oskarh.boardgamehub.db.favorite.FavoriteItemRepository
import se.oskarh.boardgamehub.db.favorite.FavoriteStatus
import se.oskarh.boardgamehub.db.favorite.FavoriteType
import se.oskarh.boardgamehub.repository.ApiResponse
import se.oskarh.boardgamehub.repository.EmptyResponse
import se.oskarh.boardgamehub.repository.ErrorResponse
import se.oskarh.boardgamehub.repository.FinderRepository
import se.oskarh.boardgamehub.repository.LoadingResponse
import se.oskarh.boardgamehub.repository.MockedRepository
import se.oskarh.boardgamehub.repository.SuccessResponse
import se.oskarh.boardgamehub.repository.YouTubeRepository
import se.oskarh.boardgamehub.util.OneTimeEvent
import se.oskarh.boardgamehub.util.extension.requireValue
import timber.log.Timber
import javax.inject.Inject

class DetailsViewModel @Inject constructor(
    private val mockedRepository: MockedRepository,
    private val finderRepository: FinderRepository,
    private val boardGameRepository: BoardGameRepository,
    private val youTubeRepository: YouTubeRepository,
    private val favoriteItemRepository: FavoriteItemRepository
) : ViewModel() {

    val currentBoardGame = MutableLiveData<Int>()

    val detailsResponse = Transformations.switchMap(currentBoardGame) { boardGameId ->
        finderRepository.getGame(boardGameId)
    }

    val boardGameName = Transformations.map(detailsResponse) { boardGameResponse ->
        (boardGameResponse as? SuccessResponse)?.data?.primaryName()
    }

    val favoriteBoardGame = Transformations.switchMap(currentBoardGame) { boardGameId ->
        favoriteItemRepository.getFavorite(FavoriteType.BOARDGAME, boardGameId)
    }

    private val _addedFavorite = MutableLiveData<OneTimeEvent>()

    val addedFavorite: LiveData<OneTimeEvent> = _addedFavorite

    private val _removedFavorite = MutableLiveData<OneTimeEvent>()

    val removedFavorite: LiveData<OneTimeEvent> = _removedFavorite

    fun toggleFavorite() {
        viewModelScope.launch(Dispatchers.IO) {
            favoriteBoardGame.value?.let { favoriteItem ->
                Analytics.logEvent(EVENT_BOARDGAME_FAVORITE, EVENT_PROPERTY_IS_FAVORITE to false)
                _removedFavorite.postValue(OneTimeEvent())
                favoriteItemRepository.deleteFavorite(favoriteItem)
            } ?: run {
                Analytics.logEvent(EVENT_BOARDGAME_FAVORITE, EVENT_PROPERTY_IS_FAVORITE to true)
                _addedFavorite.postValue(OneTimeEvent())
                favoriteItemRepository.insert(FavoriteItem(currentBoardGame.requireValue(), FavoriteType.BOARDGAME, FavoriteStatus.OWNED))
            }
        }
    }

    val boardGameComments: LiveData<ApiResponse<List<BoardGame.Comment>>> =
        Transformations.switchMap(detailsResponse) { response ->
            liveData(Dispatchers.IO) {
                when (response) {
                    is SuccessResponse -> {
                        Timber.d("Has comments ${response.data.hasComments()} No Comments ${response.data.hasNoComments()}")
                        when {
                            response.data.hasComments() -> emit(SuccessResponse(response.data.comments))
                            response.data.hasNoComments() -> emit(EmptyResponse<List<BoardGame.Comment>>())
                            else -> emitSource(finderRepository.getGameComments(response.data.id))
                        }
                    }
                    is LoadingResponse -> emit(LoadingResponse<List<BoardGame.Comment>>())
                    is EmptyResponse -> emit(EmptyResponse<List<BoardGame.Comment>>())
                    is ErrorResponse -> emit(ErrorResponse<List<BoardGame.Comment>>(response.errorMessage))
                }
            }
        }

    fun youTubeVideoDetails(): LiveData<ApiResponse<YouTubeVideoDetails>> =
        Transformations.switchMap(detailsResponse) { response ->
            liveData(Dispatchers.IO) {
                when (response) {
                    is SuccessResponse -> {
                        response.data.videos.mapNotNull { video ->
                            video.youTubeId()
                        }.takeIf { youTubeIds ->
                            youTubeIds.isNotEmpty()
                        }?.let { youTubeIds ->
                            emitSource(youTubeRepository.getYouTubeVideoDetails(youTubeIds))
                        } ?: emit(EmptyResponse<YouTubeVideoDetails>())
                    }
                    is LoadingResponse -> emit(LoadingResponse<YouTubeVideoDetails>())
                    is EmptyResponse -> emit(EmptyResponse<YouTubeVideoDetails>())
                    is ErrorResponse -> emit(ErrorResponse<YouTubeVideoDetails>(response.errorMessage))
                }
            }
        }

    fun viewedGame(id: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            boardGameRepository.updateLastViewed(id)
        }
}