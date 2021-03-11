package se.oskarh.boardgamehub.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.Transformations.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import se.oskarh.boardgamehub.analytics.Analytics
import se.oskarh.boardgamehub.analytics.EVENT_DETAILS_LOADED
import se.oskarh.boardgamehub.analytics.EVENT_PROPERTY_DETAILS_CACHE_HIT
import se.oskarh.boardgamehub.analytics.USER_PROPERTY_FAVORITE_COUNT
import se.oskarh.boardgamehub.api.GameType
import se.oskarh.boardgamehub.api.HotType
import se.oskarh.boardgamehub.api.model.boardgamegeek.BggBoardGame
import se.oskarh.boardgamehub.api.model.boardgamegeek.BoardGameDetailsResponse
import se.oskarh.boardgamehub.api.model.boardgamegeek.CollectionSuccessfulResponse
import se.oskarh.boardgamehub.api.model.boardgamegeek.FamilyResponse
import se.oskarh.boardgamehub.api.model.boardgamegeek.GetForumResponse
import se.oskarh.boardgamehub.api.model.boardgamegeek.HotBoardGames
import se.oskarh.boardgamehub.api.model.boardgamegeek.ListForumsResponse
import se.oskarh.boardgamehub.api.model.boardgamegeek.SearchResponse
import se.oskarh.boardgamehub.api.model.boardgamegeek.ThreadDetailsResponse
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.db.boardgame.BoardGameRepository
import se.oskarh.boardgamehub.db.boardgame.RankedBoardGame
import se.oskarh.boardgamehub.db.favorite.FavoriteItemRepository
import se.oskarh.boardgamehub.util.AppPreferences
import se.oskarh.boardgamehub.util.BOARDGAMES_AND_EXPANSIONS
import se.oskarh.boardgamehub.util.BOARDGAME_MAXIMUM_PAGE_SIZE
import se.oskarh.boardgamehub.util.DO_NOT_INCLUDE_DATA
import se.oskarh.boardgamehub.util.INCLUDE_DATA
import se.oskarh.boardgamehub.util.MATCH_ALL
import se.oskarh.boardgamehub.util.MAX_VISIBLE_FEED_GAMES
import se.oskarh.boardgamehub.util.extension.log
import se.oskarh.boardgamehub.util.extension.mapResponse
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinderRepository @Inject constructor(
    private val boardGameGeekService: BoardGameGeekService,
    private val boardGameRepository: BoardGameRepository,
    private val favoriteItemRepository: FavoriteItemRepository) {

    private val _boardgameDetails = MediatorLiveData<List<BoardGame>>()

    val boardGameDetails: LiveData<List<BoardGame>> = _boardgameDetails

    fun favoriteBoardGames(): LiveData<List<BoardGame>> =
        // TODO: Not really a switchMap operation as I always return same livedata. Observe it instead?
        Transformations.switchMap(favoriteItemRepository.allFavoriteIds) { favorites ->
            Timber.d("Username Got new list of favorite ids [${favorites.size}] [${favorites.joinToString()}]")
            Analytics.setUserProperty(USER_PROPERTY_FAVORITE_COUNT, favorites.size)
            getGames(favorites)
            boardGameRepository.getGamesWithIdsLive(favorites)
        }

    // TODO: Either start using or remove inFlightRequests, currently not in use
    private val inFlightRequests = mutableSetOf<Int>()

    // TODO: List<BoardGame>
    fun searchGames(query: String,
                    matching: String = MATCH_ALL,
                    type: String = BOARDGAMES_AND_EXPANSIONS): LiveData<ApiResponse<SearchResponse>> =
        map(boardGameGeekService.searchGames(query, matching, type)) { response ->
            if (response is SuccessResponse) {
                Timber.d("Successful response got ${response.data.games.size} games")
                if (response.data.games.isEmpty()) {
                    EmptyResponse()
                } else {
                    // TODO: Make this prettier?
                    val filtedBoardGames = AppPreferences.selectedFilter.filter(mergeIdenticalItems(response.data.games))
                    val sortedResponse = AppPreferences.selectedSort.sort(query, filtedBoardGames)
                    SuccessResponse(SearchResponse(sortedResponse, response.data.total))
                }
            } else {
                response
            }
        }

    private fun mergeIdenticalItems(boardGames: List<BggBoardGame>): List<BggBoardGame> =
        boardGames.groupBy { it.id }
            .map { (_, gameList) ->
                gameList.first().apply {
                    itemType = parseGameType(gameList)
                }
            }.toList()

    private fun parseGameType(games: List<BggBoardGame>): String {
        val gameTypes: Set<String> = games.map { it.itemType }.toSet()
        return if (gameTypes.contains(GameType.BOARDGAME_EXPANSION.property)) {
            (gameTypes - GameType.BOARDGAME.property).joinToString()
        } else {
            gameTypes.joinToString()
        }
    }

    fun getGame(id: Int): LiveData<ApiResponse<BoardGame>> {
        return MediatorLiveData<ApiResponse<BoardGame>>().apply {
            value = LoadingResponse()
            GlobalScope.launch(Dispatchers.IO) {
                val cachedGame = boardGameRepository.getGame(id)
                if (cachedGame != null) {
                    Analytics.logEvent(EVENT_DETAILS_LOADED, EVENT_PROPERTY_DETAILS_CACHE_HIT to 1)
                    Timber.d("Could find details in cache, full info ${cachedGame.primaryName()} ${cachedGame.videos.joinToString { it.link }} #comments ${cachedGame.comments.size} Links = ${cachedGame.links.joinToString()}")
                    postValue(SuccessResponse(cachedGame))
                    if (cachedGame.isCacheStale()) {
                        // TODO: Only done this way because I have to have a LiveData. Create call adapter that allows me to just single shot it and remove of Dispatchers.Main
                        launch(Dispatchers.Main) {
                            addSource(getBoardGame(id)) { response ->
                                if (response is SuccessResponse) {
                                    postValue(SuccessResponse(response.data))
                                }
                            }
                        }
                    }
                } else {
                    Analytics.logEvent(EVENT_DETAILS_LOADED, EVENT_PROPERTY_DETAILS_CACHE_HIT to 0)
                    inFlightRequests.add(id)
                    Timber.d("Could find NOT details in cache $id, in flight requests now contain ${inFlightRequests.joinToString()}")
                    launch(Dispatchers.Main) {
                        addSource(getBoardGame(id)) {
                            Timber.d("Got returned details ${it.log()}")
                            value = it
                        }
                    }
                }
            }
        }
    }

    fun getGameComments(id: Int): LiveData<ApiResponse<List<BoardGame.Comment>>> =
        // TODO: If new fix goes through don't get statistics here?
        map(boardGameGeekService.getGame(id, INCLUDE_DATA, DO_NOT_INCLUDE_DATA, INCLUDE_DATA)) { response ->
            response.mapResponse({
                val boardGameDetails = it.gameDetails
                GlobalScope.launch(Dispatchers.IO) {
                    val boardGame = boardGameRepository.getGame(id)
                    val allComments = (boardGameDetails.boardGameComments() + boardGame?.comments.orEmpty()).distinct()
                    Timber.d("All comments had size ${allComments.size}")
                    boardGameRepository.updateStatisticsAndComments(boardGameDetails.id, boardGameDetails.statistics!!.toStatistics(), allComments)
                }
                boardGameDetails.boardGameComments()
            }, {
                it.gameDetails.comments?.boardGameComments?.isEmpty() == true
            })
        }

    private fun getBoardGame(id: Int) =
        map<ApiResponse<BoardGameDetailsResponse>, ApiResponse<BoardGame>>(
            boardGameGeekService.getGame(id, INCLUDE_DATA, INCLUDE_DATA, DO_NOT_INCLUDE_DATA)) { boardGameResponse: ApiResponse<BoardGameDetailsResponse> ->
            when (boardGameResponse) {
                is SuccessResponse<BoardGameDetailsResponse> -> {
                    val boardGame = boardGameResponse.data.gameDetails.toBoardGame()
                    GlobalScope.launch(Dispatchers.IO) {
                        boardGameRepository.updateOrInsertBoardGame(boardGame)
                    }
                    SuccessResponse(boardGame)
                }
                is LoadingResponse<BoardGameDetailsResponse> -> LoadingResponse()
                is EmptyResponse<BoardGameDetailsResponse> -> EmptyResponse()
                is ErrorResponse<BoardGameDetailsResponse> -> ErrorResponse(boardGameResponse.errorMessage)
            }
        }

    suspend fun loadFromCache(ids: List<Int>): List<Int> {
        val cachedGames = boardGameRepository.getCachedGamesWithIds(ids)
        if (cachedGames.isNotEmpty()) {
            _boardgameDetails.postValue(cachedGames)
        }
        return cachedGames.map { it.id }
    }

    // TODO: Bad name for this function, rename it
    fun getGames(ids: List<Int>) {
        // TODO: Clean up code below
        GlobalScope.launch(Dispatchers.IO) {
            val notCachedIds = ids - loadFromCache(ids)
            Timber.d("In flight requests now contain x ${inFlightRequests.joinToString()} requested ${ids.size} not cached ${notCachedIds.size}")
            if (notCachedIds.isNotEmpty()) {
                Timber.d("Not cached games ids $notCachedIds")
                inFlightRequests.addAll(notCachedIds)

                val chunkedIds = notCachedIds.chunked(BOARDGAME_MAXIMUM_PAGE_SIZE)
                chunkedIds.forEachIndexed { index, idChunk ->
                    Timber.d("List for page $index is ${idChunk.joinToString()}")
                    val boardGameDetails = boardGameGeekService.getGames(idChunk.joinToString(","))
                    launch(Dispatchers.Main) {
                        _boardgameDetails.addSource(boardGameDetails) { response ->
                            if (response !is LoadingResponse) {
                                _boardgameDetails.removeSource(boardGameDetails)
                            }
                            if (response is SuccessResponse) {
                                Timber.d("Received ${response.data.gameDetails.size} game details ${response.data.gameDetails.firstOrNull()?.description}")
                                val foundBoardGames = response.data.gameDetails.map { it.toBoardGame() }
                                inFlightRequests.removeAll(foundBoardGames.map { it.id })
                                GlobalScope.launch(Dispatchers.IO) {
                                    boardGameRepository.insert(*foundBoardGames.toTypedArray())
                                }
                                _boardgameDetails.value = foundBoardGames
                            }
                        }
                    }
                    if (index != chunkedIds.lastIndex) {
                        Timber.d("Iteration $index / ${chunkedIds.lastIndex}")
                        delay(3000)
                    }
                }
            } else {
                Timber.d("cached games EMPTY after cache")
            }
        }
    }

    // TODO: Refactor to map?
    fun hotBoardGames(hotType: HotType): LiveData<ApiResponse<List<RankedBoardGame>>> {
        return MediatorLiveData<ApiResponse<List<RankedBoardGame>>>().apply {
            addSource(boardGameGeekService.hotGames(hotType.property)) { hotResponse ->
                if (hotResponse is SuccessResponse<HotBoardGames>) {
                    GlobalScope.launch(Dispatchers.IO) {
                        // TODO: Remove prefetchTopListGames and only use the getGames which populates cache and use
                        val cachedGames = prefetchTopListGames(hotResponse.data.games.map { it.id })
                        val hotGames = hotResponse.data.games.map { rankedBoardGame ->
                            cachedGames.firstOrNull { cachedGame ->
                                cachedGame.id == rankedBoardGame.id
                            }?.let { cachedGame ->
                                Timber.d("Found cached game ${cachedGame.primaryName()}")
                                RankedBoardGame(cachedGame, rankedBoardGame.rank)
                            } ?: rankedBoardGame.toRankedBoardGame()
                        }
                        postValue(SuccessResponse(hotGames))
                    }
                } else {
                    value = hotResponse.mapResponse({ response ->
                        response.games.map { rankedBoardGame ->
                            rankedBoardGame.toRankedBoardGame()
                        }
                    })
                }
            }
        }
    }

    suspend fun prefetchTopListGames(topList: List<Int>): List<BoardGame> {
        val visibleTopGames = topList.take(MAX_VISIBLE_FEED_GAMES)
        val cachedGames = boardGameRepository.getCachedGamesWithIds(visibleTopGames)
        val notCachedTopGames = visibleTopGames - cachedGames.map { it.id }
        getGames(notCachedTopGames)
        return cachedGames
    }

    fun findFamily(boardGameId: Int): LiveData<ApiResponse<FamilyResponse>> {
        return boardGameGeekService.getFamily(boardGameId)
    }

    fun getCollection(username: String): Call<CollectionSuccessfulResponse> {
        return boardGameGeekService.getCollection(username)
    }

    fun listForums(boardGameId: Int): LiveData<ApiResponse<ListForumsResponse>> {
        return boardGameGeekService.listForums(boardGameId)
    }

    fun getForum(forumId: Int): LiveData<ApiResponse<GetForumResponse>> {
        return boardGameGeekService.getForum(forumId)
    }

    fun getThread(threadId: Int): LiveData<ApiResponse<ThreadDetailsResponse>> {
        return boardGameGeekService.getThread(threadId)
    }
}
