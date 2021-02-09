package se.oskarh.boardgamehub.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.chibatching.kotpref.KotprefModel
import com.chibatching.kotpref.enumpref.enumValuePref
import se.oskarh.boardgamehub.api.CollectionFilterAction
import se.oskarh.boardgamehub.api.CollectionFilterOptions
import se.oskarh.boardgamehub.api.CollectionSortAction
import se.oskarh.boardgamehub.api.FilterAction
import se.oskarh.boardgamehub.api.RatingSource
import se.oskarh.boardgamehub.api.SortAction
import se.oskarh.boardgamehub.api.TopCategory
import se.oskarh.boardgamehub.api.YouTubeChannel
import se.oskarh.boardgamehub.util.extension.asDistinctLiveData
import se.oskarh.boardgamehub.util.extension.requireValue
import timber.log.Timber

object AppPreferences : KotprefModel() {

    var currentAppVersion by nullableStringPref(null)

    var isLargeResultsEnabled by booleanPref(true)

    var selectedSort by enumValuePref(SortAction.RELEVANCE)

    var selectedRating by enumValuePref(RatingSource.USER_AVERAGE_0_VOTES)

    var selectedCollectionSort by enumValuePref(CollectionSortAction.TITLE)

    var bestVotedPlayerCount by intPref(DEFAULT_VOTED_BY_PLAYER_COUNT)

    var userSort: LiveData<SortAction> = AppPreferences.asDistinctLiveData(AppPreferences::selectedSort)

    var latestUsernameCollection by stringPref()

    private var filterMinYear by intPref(DEFAULT_FILTER_START_YEAR)

    private var filterMaxYear by intPref(DEFAULT_FILTER_END_YEAR)

    private var isFilterShowingGames by booleanPref(true)

    private var isFilterShowingExpansions by booleanPref(true)

    private var areAllYearsIncluded by booleanPref(true)

    private var isCollectionFilterShowingGames by booleanPref(true)

    private var isCollectionFilterShowingExpansions by booleanPref(true)

    private var areAllPlayersIncluded by booleanPref(true)

    private var collectionFilterNumberPlayers by intPref(DEFAULT_COLLECTION_FILTER_NUMBER_PLAYERS)

    var shouldAutoPlayVideos by booleanPref(true)

    var shouldOpenVideosInYouTube by booleanPref(false)

    var isDescriptionVisible by booleanPref(true)

    var isMechanicsVisible by booleanPref(true)

    var isTypesVisible by booleanPref(true)

    var isCategoriesVisible by booleanPref(true)

    var isExpansionsVisible by booleanPref(true)

    var isCompilationsVisible by booleanPref(true)

    var isImplementationVisible by booleanPref(true)

    // TODO: Implement integration
    var isIntegrationVisible by booleanPref(true)

    var isFamilyVisible by booleanPref(true)

    var isDesignerVisible by booleanPref(true)

    var isArtistVisible by booleanPref(true)

    var isPublisherVisible by booleanPref(true)

    val userFilter = MediatorLiveData<FilterAction>().apply {
        value = FilterAction(isFilterShowingGames, isFilterShowingExpansions, areAllYearsIncluded, filterMinYear, filterMaxYear)

        addSource(AppPreferences.asDistinctLiveData(AppPreferences::isFilterShowingGames)) { isStandaloneIncluded ->
            value = requireValue().copy(areStandaloneGamesIncluded = isStandaloneIncluded)
        }
        addSource(AppPreferences.asDistinctLiveData(AppPreferences::isFilterShowingExpansions)) { isExpansionsIncluded ->
            value = requireValue().copy(areExpansionsIncluded = isExpansionsIncluded)
        }
        addSource(AppPreferences.asDistinctLiveData(AppPreferences::areAllYearsIncluded)) { areAllYearsIncluded ->
            value = requireValue().copy(areAllYearsIncluded = areAllYearsIncluded)
        }
        addSource(AppPreferences.asDistinctLiveData(AppPreferences::filterMinYear)) { startYear ->
            value = requireValue().copy(startYear = startYear)
        }
        addSource(AppPreferences.asDistinctLiveData(AppPreferences::filterMaxYear)) { endYear ->
            value = requireValue().copy(endYear = endYear)
        }
    }

    val collectionFilter = MediatorLiveData<CollectionFilterAction>().apply {
        value = CollectionFilterAction(isCollectionFilterShowingGames, isCollectionFilterShowingExpansions, areAllPlayersIncluded, collectionFilterNumberPlayers)

        addSource(AppPreferences.asDistinctLiveData(AppPreferences::isCollectionFilterShowingGames)) { isCollectionFilterShowingGames ->
            value = requireValue().copy(areStandaloneGamesIncluded = isCollectionFilterShowingGames)
        }
        addSource(AppPreferences.asDistinctLiveData(AppPreferences::isCollectionFilterShowingExpansions)) { isCollectionFilterShowingExpansions ->
            value = requireValue().copy(areExpansionsIncluded = isCollectionFilterShowingExpansions)
        }
        addSource(AppPreferences.asDistinctLiveData(AppPreferences::areAllPlayersIncluded)) { areAllPlayersIncluded ->
            value = requireValue().copy(areAllPlayersIncluded = areAllPlayersIncluded)
        }
        addSource(AppPreferences.asDistinctLiveData(AppPreferences::collectionFilterNumberPlayers)) { collectionFilterNumberPlayers ->
            value = requireValue().copy(filteredNumberPlayers = collectionFilterNumberPlayers)
        }
    }

    var selectedFilter: FilterAction
        get() = FilterAction(isFilterShowingGames, isFilterShowingExpansions, areAllYearsIncluded, filterMinYear, filterMaxYear)
        set(value) {
            isFilterShowingGames = value.areStandaloneGamesIncluded
            isFilterShowingExpansions = value.areExpansionsIncluded
            areAllYearsIncluded = value.areAllYearsIncluded
            filterMinYear = value.startYear
            filterMaxYear = value.endYear
        }

    var selectedCollectionFilter: CollectionFilterAction
        get() = CollectionFilterAction(isCollectionFilterShowingGames, isCollectionFilterShowingExpansions, areAllPlayersIncluded, collectionFilterNumberPlayers)
        set(value) {
            isCollectionFilterShowingGames = value.areStandaloneGamesIncluded
            isCollectionFilterShowingExpansions = value.areExpansionsIncluded
            areAllPlayersIncluded = value.areAllPlayersIncluded
            collectionFilterNumberPlayers = value.filteredNumberPlayers
        }

    var enabledYouTubeChannel: YouTubeChannel by enumValuePref(YouTubeChannel.BOARD_GAME_GEEK)

    val enabledYouTubeChannelStream = AppPreferences.asDistinctLiveData(AppPreferences::enabledYouTubeChannel)

    var selectedTopCategory: TopCategory by enumValuePref(TopCategory.ALL)

    private val enabledYouTubeChannelSet by stringSetPref {
        YouTubeChannel.values()
            .map { it.name }
            .toSet()
    }

    var hasOptedOutReview by booleanPref(false)

    var hasReviewed by booleanPref(false)

    var startedAppCount by intPref(1)

    var hasShownFavoritesOnboarding by booleanPref(false)

    var hasShownImportOnboarding by booleanPref(false)

    var hasShownPropertyOnboarding by booleanPref(false)

    var hasShownYouTubeSearchOnboarding by booleanPref(false)

    var hasShownFilterOnboarding by booleanPref(false)

    var hasShownMinimumAgeOnboarding by booleanPref(false)

    var isOldCollectionKept by booleanPref(true)

    var areOwnedBoardGamesImported by booleanPref(true)

    var arePreviouslyOwnedBoardGamesImported by booleanPref(false)

    var areForTradeBoardGamesImported by booleanPref(false)

    var areWantBoardGamesImported by booleanPref(false)

    var areWantToPlayBoardGamesImported by booleanPref(false)

    var areWantToBuyBoardGamesImported by booleanPref(false)

    var areWishListBoardGamesImported by booleanPref(false)

    var arePreOrderedBoardGamesImported by booleanPref(false)

    val visitedRedditPosts by stringSetPref {
        return@stringSetPref mutableSetOf<String>()
    }

    val visitedRedditLinks by stringSetPref {
        return@stringSetPref mutableSetOf<String>()
    }

    fun collectionSort(): LiveData<CollectionSortAction> = AppPreferences.asDistinctLiveData(AppPreferences::selectedCollectionSort)

    fun votedBestPlayer(): LiveData<Int> = AppPreferences.asDistinctLiveData(AppPreferences::bestVotedPlayerCount)

    fun userCollectionFilter() =
        CollectionFilterOptions(
            areOwnedBoardGamesImported,
            arePreviouslyOwnedBoardGamesImported,
            areForTradeBoardGamesImported,
            areWantBoardGamesImported,
            areWantToPlayBoardGamesImported,
            areWantToBuyBoardGamesImported,
            areWishListBoardGamesImported,
            arePreOrderedBoardGamesImported)

    fun enabledYouTubeChannels(): Set<YouTubeChannel> {
        Timber.d("Now have enabled channels ${enabledYouTubeChannelSet.joinToString()}")
        // TODO: Re-enable when I can afford it!
        return setOf(YouTubeChannel.BOARD_GAME_GEEK)
//        return enumSetFrom(enabledYouTubeChannelSet)
    }

    fun updateChannelStatus(channel: YouTubeChannel, isEnabled: Boolean) {
        if (isEnabled) {
            enabledYouTubeChannelSet.add(channel.name)
        } else {
            enabledYouTubeChannelSet.remove(channel.name)
        }
    }
}
