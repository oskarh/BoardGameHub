package se.oskarh.boardgamehub.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import se.oskarh.boardgamehub.api.HotType
import se.oskarh.boardgamehub.api.TopCategory
import se.oskarh.boardgamehub.api.model.reddit.SubredditResponse
import se.oskarh.boardgamehub.api.model.youtube.YouTubeChannelVideos
import se.oskarh.boardgamehub.db.boardgame.BoardGameRepository
import se.oskarh.boardgamehub.db.boardgame.RankedBoardGame
import se.oskarh.boardgamehub.repository.ApiResponse
import se.oskarh.boardgamehub.repository.FinderRepository
import se.oskarh.boardgamehub.repository.MockedRepository
import se.oskarh.boardgamehub.repository.RedditRepository
import se.oskarh.boardgamehub.repository.SuccessResponse
import se.oskarh.boardgamehub.repository.TopGamesRepository
import se.oskarh.boardgamehub.repository.YouTubeRepository
import se.oskarh.boardgamehub.util.AppPreferences
import se.oskarh.boardgamehub.util.MAX_VISIBLE_REDDIT_POSTS
import timber.log.Timber
import javax.inject.Inject

class FeedViewModel @Inject constructor(
    private val mockedRepository: MockedRepository,
    private val finderRepository: FinderRepository,
    private val topGamesRepository: TopGamesRepository,
    private val youTubeRepository: YouTubeRepository,
    private val boardGameRepository: BoardGameRepository,
    private val redditRepository: RedditRepository
) : ViewModel() {

    private val _hotGames = MediatorLiveData<ApiResponse<List<RankedBoardGame>>>()

    val hotGames: LiveData<ApiResponse<List<RankedBoardGame>>> = _hotGames

    private var hotStream: LiveData<ApiResponse<List<RankedBoardGame>>> = MutableLiveData()

    // TODO: Make a custom LiveData / Flow implementation with the refresh logic
    fun loadHotBoardGames(hotType: HotType = HotType.BOARDGAME) {
        _hotGames.removeSource(hotStream)
        hotStream = finderRepository.hotBoardGames(hotType)
        _hotGames.addSource(hotStream) { hotResponse ->
            _hotGames.value = hotResponse
        }
    }

    suspend fun loadTopGames(topCategory: TopCategory = AppPreferences.selectedTopCategory): StateFlow<ApiResponse<List<RankedBoardGame>>> =
        withContext(Dispatchers.IO){
            topGamesRepository.findTopGames(topCategory)
        }

    private val _redditPosts = MediatorLiveData<ApiResponse<SubredditResponse>>()

    val redditPosts: LiveData<ApiResponse<SubredditResponse>> = _redditPosts

    private var redditPostsStream: LiveData<ApiResponse<SubredditResponse>> = MutableLiveData()

    // TODO: Make a custom LiveData / Flow implementation with the refresh logic
    fun loadRedditPosts() {
        _redditPosts.removeSource(redditPostsStream)
        redditPostsStream = redditRepository.getSubredditPosts(MAX_VISIBLE_REDDIT_POSTS)
        _redditPosts.addSource(redditPostsStream) { redditResponse ->
            _redditPosts.value = redditResponse
        }
    }

    fun removeRecentGame(id: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            boardGameRepository.removeLastViewed(id)
        }

    val enabledYouTubeChannelVideo: LiveData<ApiResponse<YouTubeChannelVideos>> =
        Transformations.switchMap(AppPreferences.enabledYouTubeChannelStream) { youTubeChannel ->
            Timber.d("Fetching youtube channel videos for $youTubeChannel")
            youTubeRepository.getYouTubeChannelVideos(youTubeChannel)
        }

    fun allEnabledYouTubeChannelVideos(): LiveData<ApiResponse<YouTubeChannelVideos>> {
        return MediatorLiveData<ApiResponse<YouTubeChannelVideos>>().apply {
            Timber.d("Get all videos for channels ${AppPreferences.enabledYouTubeChannels().joinToString { it.channelName }}")
            AppPreferences.enabledYouTubeChannels().forEach {
                addSource(youTubeRepository.getYouTubeChannelVideos(it)) { videoResponse: ApiResponse<YouTubeChannelVideos> ->
                    Timber.d("Got new response for videos...")
                    if (videoResponse is SuccessResponse) {
                        Timber.d("Got new response for successful videos ${videoResponse.data.videos.joinToString { it.snippet.title }}")
                        value = videoResponse
                    }
                }
            }
        }
    }

    // TODO: Fix these
    fun listForums() = finderRepository.listForums(9209)

    fun getForum() = finderRepository.getForum(154)

    fun getThread() = finderRepository.getThread(2207165)
}