package se.oskarh.boardgamehub.repository

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import se.oskarh.boardgamehub.analytics.Analytics
import se.oskarh.boardgamehub.analytics.EVENT_IMPORT_ERROR
import se.oskarh.boardgamehub.analytics.EVENT_IMPORT_SUCCESSFUL
import se.oskarh.boardgamehub.analytics.EVENT_IMPORT_WRONG_USERNAME
import se.oskarh.boardgamehub.analytics.EVENT_PROPERTY_IMPORT_SIZE
import se.oskarh.boardgamehub.api.model.CollectionSuccessfulResponse
import se.oskarh.boardgamehub.db.collection.BoardGameCollection
import se.oskarh.boardgamehub.db.collection.BoardGameCollectionRepository
import se.oskarh.boardgamehub.db.favorite.FavoriteItem
import se.oskarh.boardgamehub.db.favorite.FavoriteItemRepository
import se.oskarh.boardgamehub.db.favorite.FavoriteType
import se.oskarh.boardgamehub.util.AppPreferences
import se.oskarh.boardgamehub.util.Event
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImportCollectionRepository @Inject constructor(
    private val finderRepository: FinderRepository,
    private val favoriteItemRepository: FavoriteItemRepository,
    private val boardGameCollectionRepository: BoardGameCollectionRepository) {

    private val _importCollectionResponses = MutableLiveData<Event<ApiResponse<Int>>>()

    val importCollectionResponses: MutableLiveData<Event<ApiResponse<Int>>> = _importCollectionResponses

    fun requestUserCollection(username: String) {
        finderRepository.getCollection(username).enqueue(object : retrofit2.Callback<CollectionSuccessfulResponse> {

            override fun onFailure(call: Call<CollectionSuccessfulResponse>, throwable: Throwable) {
                Analytics.logEvent(EVENT_IMPORT_ERROR)
                _importCollectionResponses.value = Event(ErrorResponse("Import failed, please try again later"))
            }

            override fun onResponse(call: Call<CollectionSuccessfulResponse>, successfulResponse: Response<CollectionSuccessfulResponse>) {
                when {
                    successfulResponse.code() == 202 -> _importCollectionResponses.value = Event(LoadingResponse())
                    successfulResponse.code() == 200 -> {
                        if (successfulResponse.body()?.totalitems != null) {
                            val importedCollection = successfulResponse.body()!!
                            val userFilter = AppPreferences.userCollectionFilter()
                            val favorites = importedCollection.boardGames.orEmpty()
                                .filter { it.isUserIncluded(userFilter) }
                                .map { FavoriteItem(it.id, FavoriteType.BOARDGAME, it.favoriteStatus()) }
                            GlobalScope.launch(Dispatchers.IO) {
                                favoriteItemRepository.importCollection(AppPreferences.isOldCollectionKept, favorites)
                                boardGameCollectionRepository.insert(
                                    BoardGameCollection(username, importedCollection.pubdate.orEmpty(), importedCollection.collectionBoardGames()))
                            }
                            Analytics.logEvent(EVENT_IMPORT_SUCCESSFUL, EVENT_PROPERTY_IMPORT_SIZE to favorites.size)
                            if (favorites.isEmpty()) {
                                _importCollectionResponses.value = Event(EmptyResponse())
                            } else {
                                _importCollectionResponses.value = Event(SuccessResponse(favorites.size))
                            }
                        } else {
                            Analytics.logEvent(EVENT_IMPORT_WRONG_USERNAME)
                            _importCollectionResponses.value = Event(ErrorResponse("No collection found for user $username"))
                        }
                    }
                }
            }
        })
    }
}
