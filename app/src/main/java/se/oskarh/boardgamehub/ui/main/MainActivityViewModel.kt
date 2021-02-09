package se.oskarh.boardgamehub.ui.main

import androidx.core.text.trimmedLength
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import se.oskarh.boardgamehub.BuildConfig
import se.oskarh.boardgamehub.analytics.Analytics
import se.oskarh.boardgamehub.analytics.EVENT_BOARDGAME_FAVORITE
import se.oskarh.boardgamehub.analytics.EVENT_PROPERTY_IS_FAVORITE
import se.oskarh.boardgamehub.api.model.SearchResponse
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.db.boardgame.BoardGameRepository
import se.oskarh.boardgamehub.db.favorite.FavoriteItem
import se.oskarh.boardgamehub.db.favorite.FavoriteItemRepository
import se.oskarh.boardgamehub.db.favorite.FavoriteStatus
import se.oskarh.boardgamehub.db.favorite.FavoriteType
import se.oskarh.boardgamehub.db.suggestion.Suggestion
import se.oskarh.boardgamehub.db.suggestion.SuggestionRepository
import se.oskarh.boardgamehub.repository.ApiResponse
import se.oskarh.boardgamehub.repository.FinderRepository
import se.oskarh.boardgamehub.repository.ImportCollectionRepository
import se.oskarh.boardgamehub.repository.LoadingResponse
import se.oskarh.boardgamehub.repository.SuccessResponse
import se.oskarh.boardgamehub.util.AppPreferences
import se.oskarh.boardgamehub.util.CustomTransformations
import se.oskarh.boardgamehub.util.MINIMUM_QUERY_LENGTH
import se.oskarh.boardgamehub.util.MINIMUM_REQUIRED_APP_VERSION
import se.oskarh.boardgamehub.util.OneTimeEvent
import se.oskarh.boardgamehub.util.SEARCH_DEBOUNCE_MILLISECONDS
import se.oskarh.boardgamehub.util.VetoableLiveData
import se.oskarh.boardgamehub.util.extension.normalize
import se.oskarh.boardgamehub.util.extension.requireValue
import se.oskarh.boardgamehub.util.extension.trimmedCount
import timber.log.Timber
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    private val finderRepository: FinderRepository,
    private val boardGameRepository: BoardGameRepository,
    private val suggestionRepository: SuggestionRepository,
    private val favoriteItemRepository: FavoriteItemRepository,
    private val importCollectionRepository: ImportCollectionRepository) : ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            boardGameRepository.deleteStaleBoardGames()
        }
        fetchRemoteConfig()
    }

    private val fetchedBoardGameIds = mutableSetOf<Int>()

    val boardGameDetails: LiveData<List<BoardGame>> = finderRepository.boardGameDetails

    private val _isUpdateRequired = MutableLiveData<Boolean>()

    val isUpdateRequired: LiveData<Boolean> = _isUpdateRequired

    private val _suggestionClicked = MutableLiveData<OneTimeEvent>()

    val suggestionClicked: LiveData<OneTimeEvent> = _suggestionClicked

    private val _itemTypeToggled = MutableLiveData<OneTimeEvent>()

    val itemTypeToggled: LiveData<OneTimeEvent> = _itemTypeToggled

    val allFavoriteIds = favoriteItemRepository.allFavoriteIds

    // TODO: Reuse logic for prefetching between this viewmodel and BoardGameListViewModel
    fun fetchDetails(ids: List<Int>) {
        val idsToFetch = ids - fetchedBoardGameIds
        finderRepository.getGames(idsToFetch)
        fetchedBoardGameIds.addAll(idsToFetch)
    }

    fun populateFromCache(ids: List<Int>) {
        viewModelScope.launch(Dispatchers.IO) {
            fetchedBoardGameIds.addAll(finderRepository.loadFromCache(ids))
        }
    }

    fun resetCache() = fetchedBoardGameIds.clear()

    // TODO: Get rid of VetoableLiveData?
    val currentQuery: MutableLiveData<String> = VetoableLiveData()

    private val distinctNormalizedQuery = Transformations.distinctUntilChanged(currentQuery.map { it.normalize() })

    val favoriteFilter = MutableLiveData<String>()

    private val _addedFavorite = MutableLiveData<OneTimeEvent>()

    val addedFavorite: LiveData<OneTimeEvent> = _addedFavorite

    private val _removedFavorite = MutableLiveData<OneTimeEvent>()

    val removedFavorite: LiveData<OneTimeEvent> = _removedFavorite

    val favoriteFilteredGames = MediatorLiveData<FavoriteState>().apply {
        value = FavoriteState()
        addSource(finderRepository.favoriteBoardGames()) { newFavorites ->
            val allNewFavorites = (requireValue().allFavorites + newFavorites).distinctBy { it.id }
                .filter { requireValue().favoriteIds.contains(it.id) }
            // TODO: Fix so finderRepository.favoriteBoardGames returns all board games from our favorite ids, not only the last batch (because of switchmap on favoriteIds?)
            // Works for everything except when favorite size is bigger than SQLite page size. Currentlhy 990 items
            Timber.d("New favorites reported ${newFavorites.size} | ${newFavorites.take(10).joinToString { it.primaryName() }} favorite size is ${allNewFavorites.size}")
            value = requireValue().copy(allFavorites = allNewFavorites)
        }
        addSource(favoriteFilter) { newFilter ->
            Timber.d("New favorites filter reported $newFilter")
            value = requireValue().copy(filter = newFilter)
        }
        addSource(favoriteItemRepository.allFavoriteIds) { newFavoriteIds ->
            Timber.d("New favorites ids reported ${newFavoriteIds.size}")
            value = requireValue().copy(favoriteIds = newFavoriteIds)
        }
        addSource(AppPreferences.collectionSort()) { newSort ->
            Timber.d("New favorite sort ${newSort.name}")
            value = requireValue().copy(collectionSort = newSort)
        }
        addSource(AppPreferences.votedBestPlayer()) { newPlayerCount ->
            Timber.d("New voted best player count $newPlayerCount")
            value = requireValue().copy(votedBestPlayerCount = newPlayerCount)
        }
        addSource(AppPreferences.collectionFilter) { newFilter ->
            Timber.d("New favorite filter $newFilter")
            value = requireValue().copy(collectionFilter = newFilter)
        }
    }

    val importedCollectionResponses = importCollectionRepository.importCollectionResponses

    private val _searchResponse: LiveData<ApiResponse<SearchResponse>> =
        Transformations.switchMap(CustomTransformations.debounce((distinctNormalizedQuery), SEARCH_DEBOUNCE_MILLISECONDS)) {
            Timber.d("Searching for $it")
            if (it.trimmedLength() >= MINIMUM_QUERY_LENGTH) {
                finderRepository.searchGames(it)
            } else {
                // TODO: Absent live data
                MutableLiveData()
            }
        }

    private val _sortedSearch = MediatorLiveData<ApiResponse<SearchResponse>>().apply {
        addSource(AppPreferences.userFilter) { newFilter ->
            value?.let { currentValue ->
                if (currentValue is SuccessResponse) {
                    value = currentValue.copy(currentValue.data.filterBy(newFilter))
                }
            }
        }

        addSource(AppPreferences.userSort) { newSort ->
            value?.let { currentValue ->
                if (currentValue is SuccessResponse) {
                    value = currentValue.copy(currentValue.data.sortBy(currentQuery.requireValue(), newSort))
                }
            }
        }

        addSource(distinctNormalizedQuery) {
            Timber.d("Query changed to [$it]")
            value = LoadingResponse()
        }

        addSource(_searchResponse) { response ->
            // TODO: Make this prettier if I don't want to sort and filter here?
            if (response is SuccessResponse) {
                value = response
//                value = response.copy(response.data.sortBy(AppPreferences.selectedSort))
            } else {
                value = response
            }
        }
    }

    val sortedSearch: LiveData<ApiResponse<SearchResponse>> = _sortedSearch

    private val searchFocus = MutableLiveData<Boolean>()

    private val allSuggestions: LiveData<List<Suggestion>> = suggestionRepository.getAllSuggestions()

    // TODO: Switchmap on SearchState instead and only return suggestions when suggestion is visible?
    private val matchingSuggestions: LiveData<List<Suggestion>> =
        Transformations.switchMap(distinctNormalizedQuery) {
            Timber.d("Switching to search for [$it]")
            suggestionRepository.getMatchingSuggestions(it)
        }

    fun suggestionClicked(suggestion: Suggestion) {
        currentQuery.value = suggestion.originalSuggestion
        _suggestionClicked.value = OneTimeEvent()
        updateTimestamp(suggestion)
    }

    fun viewTypeToggled() {
        _itemTypeToggled.value = OneTimeEvent()
    }

    private val allRecentBoardGames: LiveData<List<BoardGame>> = boardGameRepository.getAllRecentGames()

    private val matchingRecentGames: LiveData<List<BoardGame>> =
        Transformations.switchMap(distinctNormalizedQuery) { query ->
            boardGameRepository.getMatchingRecentGames(query)
        }

    fun hasSearchFocus(hasFocus: Boolean) {
        searchFocus.value = hasFocus
    }

    // TODO: Maybe clean this up?
    val searchStates = MediatorLiveData<SearchState>().apply {
        value = SearchState()
        // Always keep recent board games in memory for quick access
        val allRecentBoardGames = allRecentBoardGames
        val allSuggestions = allSuggestions
        addSource(Transformations.distinctUntilChanged(searchFocus)) { isFocused ->
            Timber.d("Changing to focus $value")
            value = if (isFocused && distinctNormalizedQuery.value.isNullOrBlank()) {
                requireValue().copy(
                    hasFocus = isFocused,
                    suggestions = allSuggestions.value ?: emptyList(),
                    recentBoardGames = allRecentBoardGames.value ?: emptyList())
            } else {
                requireValue().copy(hasFocus = isFocused)
            }
        }
        addSource(matchingSuggestions) { newSuggestions ->
            Timber.d("Changing to matching suggestions $value, will update ${requireValue().isShowingSuggestions}")
            if (requireValue().isShowingSuggestions && !distinctNormalizedQuery.value.isNullOrBlank()) {
                value = requireValue().copy(suggestions = newSuggestions)
            }
        }
        addSource(matchingRecentGames) { newRecentGames ->
            Timber.d("Changing to matching games ${newRecentGames.joinToString { it.primaryName() }}")
            if (requireValue().isShowingSuggestions && !distinctNormalizedQuery.value.isNullOrBlank()) {
                value = requireValue().copy(recentBoardGames = newRecentGames)
            }
        }
        addSource(allRecentBoardGames) { newRecentGames ->
            if (requireValue().isShowingSuggestions && requireValue().searchQueryCount == 0) {
                value = requireValue().copy(recentBoardGames = newRecentGames)
            }
        }
        addSource(allSuggestions) { suggestions ->
            Timber.d("All suggestions now ${suggestions.joinToString { it.originalSuggestion }}")
            if (requireValue().isShowingSuggestions) {
                value = requireValue().copy(suggestions = suggestions)
            }
        }
        addSource(distinctNormalizedQuery) { query ->
            Timber.d("Changing to query $value")
            value = if (query.isNullOrBlank()) {
                requireValue().copy(
                    searchQueryCount = query.trimmedCount(),
                    suggestions = allSuggestions.value ?: emptyList(),
                    recentBoardGames = allRecentBoardGames.value ?: emptyList())
            } else {
                requireValue().copy(searchQueryCount = query.trimmedCount())
            }
        }
    }

    val screenState: LiveData<ScreenState> = Transformations.map(searchStates) { searchState ->
        Timber.d("Changing, checking state for $searchState ${searchState.searchQueryCount < MINIMUM_QUERY_LENGTH && searchState.hasFocus && searchState.suggestions.isNotEmpty()}")
        searchState.visibilityType
    }

    fun toggleFavorite(id: Int) {
        Timber.d("Toggling favorite $id")
        viewModelScope.launch(Dispatchers.IO) {
            if (allFavoriteIds.value.orEmpty().contains(id)) {
                Analytics.logEvent(EVENT_BOARDGAME_FAVORITE, EVENT_PROPERTY_IS_FAVORITE to false)
                _removedFavorite.postValue(OneTimeEvent())
                favoriteItemRepository.deleteFavorite(FavoriteItem(id, FavoriteType.BOARDGAME, FavoriteStatus.OWNED))
            } else {
                Analytics.logEvent(EVENT_BOARDGAME_FAVORITE, EVENT_PROPERTY_IS_FAVORITE to true)
                _addedFavorite.postValue(OneTimeEvent())
                favoriteItemRepository.insert(FavoriteItem(id, FavoriteType.BOARDGAME, FavoriteStatus.OWNED))
            }
        }
    }

    private val _exitSearch = MutableLiveData<OneTimeEvent>()

    val exitSearch: LiveData<OneTimeEvent> = _exitSearch

    fun exitSearch() {
        _exitSearch.value = OneTimeEvent()
    }

    fun insert(suggestion: String) =
        suggestion.takeIf { it.trim().length >= MINIMUM_QUERY_LENGTH }?.let { newSuggestion ->
            insert(Suggestion(newSuggestion))
        }

    fun deleteSuggestion(suggestion: Suggestion) =
        viewModelScope.launch(Dispatchers.IO) {
            suggestionRepository.deleteSuggestion(suggestion)
        }

//    val enabledYouTubeChannels = Transformations.map(AppPreferences.asLiveData(AppPreferences::enabledYouTubeChannelSet)) {
//        enumSetFrom<YouTubeChannel>(it)
//    }

    private fun insert(suggestion: Suggestion) =
        viewModelScope.launch(Dispatchers.IO) {
            suggestionRepository.insert(suggestion)
        }

    private fun updateTimestamp(suggestion: Suggestion) =
        viewModelScope.launch(Dispatchers.IO) {
            suggestionRepository.updateTimestamp(suggestion)
        }

    private fun fetchRemoteConfig() {
        FirebaseRemoteConfig.getInstance()
            .fetchAndActivate()
            .addOnSuccessListener {
                val minimumVersion = FirebaseRemoteConfig.getInstance().getDouble(MINIMUM_REQUIRED_APP_VERSION)
                val currentVersion = BuildConfig.VERSION_NAME.toDoubleOrNull() ?: 0.0
                Timber.d("Got remote config min version [$minimumVersion] current version [$currentVersion] update requied: ${minimumVersion > currentVersion}")
                _isUpdateRequired.value = minimumVersion > currentVersion
            }
    }
}