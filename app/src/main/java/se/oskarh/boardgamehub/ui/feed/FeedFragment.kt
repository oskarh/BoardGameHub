package se.oskarh.boardgamehub.ui.feed

import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.ask_for_review_card.*
import kotlinx.android.synthetic.main.feed_fragment.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.analytics.Analytics
import se.oskarh.boardgamehub.analytics.EVENT_ALL_DEFAULT_CHANNEL_VIDEOS
import se.oskarh.boardgamehub.analytics.EVENT_ALL_HOT_BOARDGAMES
import se.oskarh.boardgamehub.analytics.EVENT_ALL_TOP_BOARDGAMES
import se.oskarh.boardgamehub.analytics.EVENT_BOARDGAMEGEEK_LINK_OPEN
import se.oskarh.boardgamehub.analytics.EVENT_BOARDGAME_MAP
import se.oskarh.boardgamehub.analytics.EVENT_PROPERTY_REDDIT_POST
import se.oskarh.boardgamehub.analytics.EVENT_RECENT_BOARDGAME_DELETE
import se.oskarh.boardgamehub.analytics.EVENT_RECENT_BOARDGAME_OPEN
import se.oskarh.boardgamehub.analytics.EVENT_RECENT_SEARCH_DELETE
import se.oskarh.boardgamehub.analytics.EVENT_RECENT_SEARCH_OPEN
import se.oskarh.boardgamehub.analytics.EVENT_REDDIT_BOARDGAMES_OPEN
import se.oskarh.boardgamehub.analytics.EVENT_REDDIT_POST_OPEN
import se.oskarh.boardgamehub.analytics.EVENT_REVIEW_MAYBE_LATER
import se.oskarh.boardgamehub.analytics.EVENT_REVIEW_NEVER_ASK
import se.oskarh.boardgamehub.analytics.EVENT_REVIEW_SHOWN
import se.oskarh.boardgamehub.analytics.EVENT_REVIEW_YES
import se.oskarh.boardgamehub.analytics.EVENT_SEARCH_ERROR
import se.oskarh.boardgamehub.analytics.EVENT_SEARCH_RESULTS_EMPTY
import se.oskarh.boardgamehub.analytics.EVENT_SEARCH_RESULTS_FOUND
import se.oskarh.boardgamehub.analytics.EVENT_YOUTUBE_SEARCH_ERROR
import se.oskarh.boardgamehub.analytics.EVENT_YOUTUBE_SEARCH_SUCCESS
import se.oskarh.boardgamehub.analytics.ScreenType
import se.oskarh.boardgamehub.analytics.USER_PROPERTY_REDDIT_POST_COUNT
import se.oskarh.boardgamehub.api.model.youtube.YouTubeChannelVideos
import se.oskarh.boardgamehub.api.model.youtube.YouTubeVideoInfo
import se.oskarh.boardgamehub.databinding.FeedFragmentBinding
import se.oskarh.boardgamehub.db.boardgame.RankedBoardGame
import se.oskarh.boardgamehub.repository.EmptyResponse
import se.oskarh.boardgamehub.repository.ErrorResponse
import se.oskarh.boardgamehub.repository.LoadingResponse
import se.oskarh.boardgamehub.repository.SuccessResponse
import se.oskarh.boardgamehub.ui.base.BaseFragment
import se.oskarh.boardgamehub.ui.common.PrefetchTimer
import se.oskarh.boardgamehub.ui.details.DetailsFragment
import se.oskarh.boardgamehub.ui.details.DetailsSource
import se.oskarh.boardgamehub.ui.details.VideoLiteAdapter
import se.oskarh.boardgamehub.ui.list.BoardGameListActivity
import se.oskarh.boardgamehub.ui.main.MainActivityViewModel
import se.oskarh.boardgamehub.ui.main.ShowFeed
import se.oskarh.boardgamehub.ui.main.ShowSearch
import se.oskarh.boardgamehub.ui.main.ShowSuggestions
import se.oskarh.boardgamehub.ui.search.HeaderBoardGameAdapter
import se.oskarh.boardgamehub.ui.settings.SettingsFragment
import se.oskarh.boardgamehub.ui.suggestion.RecentBoardGameAdapter
import se.oskarh.boardgamehub.ui.suggestion.SuggestionAdapter
import se.oskarh.boardgamehub.ui.videolist.VideoListActivity
import se.oskarh.boardgamehub.util.AppPreferences
import se.oskarh.boardgamehub.util.BOARDGAME_GEEK_URL
import se.oskarh.boardgamehub.util.GOOGLE_MAP_BOARDGAME_CAFES
import se.oskarh.boardgamehub.util.KEY_BOARDGAME_LIST
import se.oskarh.boardgamehub.util.KEY_DETAILS_SOURCE
import se.oskarh.boardgamehub.util.KEY_SCREEN
import se.oskarh.boardgamehub.util.KEY_TITLE
import se.oskarh.boardgamehub.util.KEY_VIDEO_LIST
import se.oskarh.boardgamehub.util.MAX_VISIBLE_FEED_VIDEOS
import se.oskarh.boardgamehub.util.PROMPT_REVIEW_PERIOD
import se.oskarh.boardgamehub.util.REDDIT_BOARDGAMES_SUBREDDIT
import se.oskarh.boardgamehub.util.extension.addFragment
import se.oskarh.boardgamehub.util.extension.animateToGone
import se.oskarh.boardgamehub.util.extension.canHandleGoogleMapsIntent
import se.oskarh.boardgamehub.util.extension.canHandleRatingIntent
import se.oskarh.boardgamehub.util.extension.closeKeyboardOnScroll
import se.oskarh.boardgamehub.util.extension.closeKeyboardOnScrollStart
import se.oskarh.boardgamehub.util.extension.gone
import se.oskarh.boardgamehub.util.extension.hideKeyboard
import se.oskarh.boardgamehub.util.extension.injector
import se.oskarh.boardgamehub.util.extension.isEmpty
import se.oskarh.boardgamehub.util.extension.prefetchItemIndexes
import se.oskarh.boardgamehub.util.extension.redirectToPlayStore
import se.oskarh.boardgamehub.util.extension.removeScrollChangeListener
import se.oskarh.boardgamehub.util.extension.requireValue
import se.oskarh.boardgamehub.util.extension.scrollToTop
import se.oskarh.boardgamehub.util.extension.showPopupMenu
import se.oskarh.boardgamehub.util.extension.showTapTarget
import se.oskarh.boardgamehub.util.extension.startActivity
import se.oskarh.boardgamehub.util.extension.startVideoActivity
import se.oskarh.boardgamehub.util.extension.visibleIf
import timber.log.Timber
import javax.inject.Inject

class FeedFragment : BaseFragment() {

    private lateinit var binding: FeedFragmentBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var feedViewModel: FeedViewModel

    private lateinit var mainActivityViewModel: MainActivityViewModel

    private var boardGameGeekVideos = emptyList<YouTubeVideoInfo>()

    private val prefetchTimer = PrefetchTimer()

    private val hotBoardGameAdapter = CompactBoardGameAdapter {
        activity?.addFragment(DetailsFragment.newInstance(it.boardGame.primaryName(), it.boardGame.id, DetailsSource.HOT))
    }

    private val topBoardGameAdapter = CompactBoardGameAdapter {
        activity?.addFragment(DetailsFragment.newInstance(it.boardGame.primaryName(), it.boardGame.id, DetailsSource.TOP))
    }

    private val redditAdapter = RedditAdapter { post ->
        Analytics.logEvent(EVENT_REDDIT_POST_OPEN, EVENT_PROPERTY_REDDIT_POST to post.permalink)
        Analytics.setUserProperty(USER_PROPERTY_REDDIT_POST_COUNT, AppPreferences.visitedRedditPosts.size + 1)
        AppPreferences.visitedRedditPosts.add(post.id)
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(post.postLink())))
    }

    private val videoAdapter = VideoLiteAdapter(MAX_VISIBLE_FEED_VIDEOS) {
        activity?.startVideoActivity(it)
    }

    private val suggestionAdapter = SuggestionAdapter(emptyList(), { suggestion ->
        Analytics.logEvent(EVENT_RECENT_SEARCH_OPEN)
        hideKeyboard()
        mainActivityViewModel.suggestionClicked(suggestion)
    }, {
        Analytics.logEvent(EVENT_RECENT_SEARCH_DELETE)
        mainActivityViewModel.deleteSuggestion(it)
    })

    private val recentBoardGameAdapter = RecentBoardGameAdapter(emptyList(), { boardGame ->
        Analytics.logEvent(EVENT_RECENT_BOARDGAME_OPEN)
        activity?.addFragment(DetailsFragment.newInstance(boardGame.primaryName(), boardGame.id, DetailsSource.RECENT_BOARDGAME))
    }, { boardGame ->
        Analytics.logEvent(EVENT_RECENT_BOARDGAME_DELETE)
        feedViewModel.removeRecentGame(boardGame.id)
    })

    private val searchResultsAdapter = HeaderBoardGameAdapter(false, mutableListOf(), {
        mainActivityViewModel.insert(mainActivityViewModel.currentQuery.requireValue())
        activity?.addFragment(DetailsFragment.newInstance(it.primaryName(), it.id, DetailsSource.SEARCH))
    }, { boardGame, item ->
        val isFavorite = mainActivityViewModel.allFavoriteIds.value.orEmpty().contains(boardGame.id)
        item.showPopupMenu(boardGame, isFavorite) { boardGameId ->
            mainActivityViewModel.toggleFavorite(boardGameId)
        }
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FeedFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injector.inject(this)
        setupReviewCard()
        show_all_hot_button.setOnClickListener {
            Analytics.logEvent(EVENT_ALL_HOT_BOARDGAMES)
            startActivity<BoardGameListActivity>(
                KEY_BOARDGAME_LIST to hotBoardGameAdapter.boardGameList.map { it.boardGame.toLiteBoardGame() },
                KEY_TITLE to getString(R.string.hot_boardgames),
                KEY_SCREEN to ScreenType.HOT_BOARDGAMES.ordinal,
                KEY_DETAILS_SOURCE to DetailsSource.HOT_LIST.ordinal)
        }
        hot_try_again_button.setOnClickListener {
            feedViewModel.loadHotGames()
        }
        top_category_title.text = getString(R.string.best_top_category, getString(AppPreferences.selectedTopCategory.categoryName))
        configure_top_category_button.setOnClickListener {
            SettingsFragment.changeTopCategory(activity) { topCategory->
                top_category_title.text = getString(R.string.best_top_category, getString(topCategory.categoryName))
                feedViewModel.loadTopGames()
            }
        }
        show_all_top_button.setOnClickListener {
            Analytics.logEvent(EVENT_ALL_TOP_BOARDGAMES)
            startActivity<BoardGameListActivity>(
                KEY_BOARDGAME_LIST to topBoardGameAdapter.boardGameList.map { it.boardGame.toLiteBoardGame() },
                KEY_TITLE to getString(R.string.best_top_category, getString(AppPreferences.selectedTopCategory.categoryName)),
                KEY_SCREEN to ScreenType.TOP_BOARDGAMES.ordinal,
                KEY_DETAILS_SOURCE to DetailsSource.TOP_LIST.ordinal)
        }
        top_try_again_button.setOnClickListener {
            feedViewModel.loadTopGames()
        }

        channel_title.text = getString(R.string.channel_videos, AppPreferences.enabledYouTubeChannel.channelName)
        configure_videos_button.setOnClickListener {
            SettingsFragment.changeYouTubeChannel(activity) { youTubeChannel ->
                channel_title.text = getString(R.string.channel_videos, youTubeChannel.channelName)
            }
        }
        show_all_videos_button.setOnClickListener {
            Analytics.logEvent(EVENT_ALL_DEFAULT_CHANNEL_VIDEOS)
            startActivity<VideoListActivity>(KEY_VIDEO_LIST to boardGameGeekVideos,
                KEY_TITLE to getString(R.string.boardgamegeek_videos))
        }
        boardgame_cafe_layout.visibleIf { canHandleGoogleMapsIntent() }
        boardgame_cafe_webview.setOnClickListener {
            Analytics.logEvent(EVENT_BOARDGAME_MAP)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_MAP_BOARDGAME_CAFES))
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        }
        boardgame_geek_link.setOnClickListener {
            Analytics.logEvent(EVENT_BOARDGAMEGEEK_LINK_OPEN)
            startActivity(Intent(Intent.ACTION_VIEW, BOARDGAME_GEEK_URL.toUri()))
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Clean up this in a better way and reuse code
        suggestion_list.adapter = suggestionAdapter
        suggestion_list.layoutManager = LinearLayoutManager(requireActivity())
        suggestion_list.closeKeyboardOnScrollStart(requireActivity())
        recent_boardgames_list.adapter = recentBoardGameAdapter
        recent_boardgames_list.layoutManager = LinearLayoutManager(requireActivity())
        recent_boardgames_list.closeKeyboardOnScrollStart(requireActivity())
        search_list.layoutManager = LinearLayoutManager(requireActivity())
        search_list.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        search_list.adapter = searchResultsAdapter
        search_list.closeKeyboardOnScrollStart(requireActivity())
        hot_list.layoutManager = LinearLayoutManager(requireActivity())
        hot_list.adapter = hotBoardGameAdapter
        top_list.layoutManager = LinearLayoutManager(requireActivity())
        top_list.adapter = topBoardGameAdapter
        reddit_list.layoutManager = LinearLayoutManager(requireActivity())
        reddit_list.adapter = redditAdapter
        reddit_list.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        youtube_list.layoutManager = LinearLayoutManager(requireActivity())
        youtube_list.adapter = videoAdapter
        feed_root.closeKeyboardOnScroll(requireActivity())
        suggestion_scroll.closeKeyboardOnScroll(requireActivity())
        suggestion_overlay.setOnClickListener {
            mainActivityViewModel.exitSearch()
        }
        search_overlay.setOnClickListener {
            mainActivityViewModel.exitSearch()
        }
        // TODO: Refactor this to extension method or similar?
        lazyLoadVideos()
        mainActivityViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(MainActivityViewModel::class.java)
        feedViewModel = ViewModelProvider(this, viewModelFactory).get(FeedViewModel::class.java)
        feedViewModel.hotGames.observe(viewLifecycleOwner, Observer { response ->
            hot_games_loading.visibleIf { response is LoadingResponse }
            show_all_hot_button.visibleIf(View.INVISIBLE) { response is SuccessResponse }
            hot_list.visibleIf(View.INVISIBLE) { response is SuccessResponse }
            hot_error_root.visibleIf { response is ErrorResponse }
            if (response is SuccessResponse) {
                updateHotGames(response.data)
            }
        })
        feedViewModel.loadHotGames()

        fetchRedditPosts()
        feedViewModel.topGames.observe(viewLifecycleOwner, Observer { response ->
            top_games_loading.visibleIf { response is LoadingResponse }
            show_all_top_button.visibleIf(View.INVISIBLE) { response is SuccessResponse }
            top_list.visibleIf(View.INVISIBLE) { response is SuccessResponse }
            top_error_root.visibleIf { response is ErrorResponse }
            if (response is SuccessResponse) {
                updateTopGames(response.data)
            }
        })
        feedViewModel.loadTopGames()
        mainActivityViewModel.screenState.observe(viewLifecycleOwner, Observer { screenState ->
            suggestion_overlay.visibleIf { screenState is ShowSuggestions }
            empty_suggestions_message.visibleIf { screenState is ShowSuggestions && !screenState.hasSuggestions }
            recent_searches_title.visibleIf { screenState is ShowSuggestions && screenState.suggestions.isNotEmpty() }
            recent_boardgames_title.visibleIf { screenState is ShowSuggestions && screenState.recentBoardGames.isNotEmpty() }
            search_overlay.visibleIf { screenState is ShowSearch }
            when (screenState) {
                is ShowFeed -> suggestion_scroll.scrollTo(0, 0)
                is ShowSuggestions -> {
                    suggestionAdapter.updateResults(screenState.suggestions)
                    recentBoardGameAdapter.updateResults(screenState.recentBoardGames)
                }
            }
        })

        mainActivityViewModel.sortedSearch.observe(viewLifecycleOwner, Observer { response ->
            search_list.visibleIf { response is SuccessResponse }
            empty_search_root.visibleIf { response is EmptyResponse }
            empty_search_message.text = getString(R.string.empty_search_message)
            error_search_root.visibleIf { response is ErrorResponse }
            when (response) {
                is SuccessResponse -> {
                    Analytics.logEvent(EVENT_SEARCH_RESULTS_FOUND)
                    // TODO: Display isShown in a better way
                    val searchedBoardGames = response.data.games
                        .map { it.toBoardGame() }
                        .filter { it.isShown }
                        .toList()

                    search_list.scrollToTop()
                    mainActivityViewModel.resetCache()
                    searchResultsAdapter.updateResults(searchedBoardGames)
                    prefetchTimer.reset()
                    mainActivityViewModel.populateFromCache(searchedBoardGames.map { it.id })
                    if(!AppPreferences.hasShownFilterOnboarding && search_list.isVisible) {
                        activity?.findViewById<View>(R.id.filter_action)?.let { filterItem ->
                            activity?.showTapTarget(filterItem, R.string.onboarding_filter_title, R.string.onboarding_filter_message)
                            AppPreferences.hasShownFilterOnboarding = true
                        }
                    }
                }
                is EmptyResponse -> Analytics.logEvent(EVENT_SEARCH_RESULTS_EMPTY)
                is ErrorResponse -> Analytics.logEvent(EVENT_SEARCH_ERROR)
            }
            search_loading.visibleIf { response is LoadingResponse }
        })

        prefetchTimer.liveData.observe(viewLifecycleOwner, Observer {
            val visibleItemIds: List<Int> = search_list.prefetchItemIndexes(searchResultsAdapter.headerSize)
                .mapNotNull { index ->
                    searchResultsAdapter.boardGames.getOrNull(index)?.id
                }
                .toList()

            Timber.d("Now requesting update with details for ids ${visibleItemIds.joinToString()}")
            mainActivityViewModel.fetchDetails(visibleItemIds)
        })

        mainActivityViewModel.boardGameDetails.observe(viewLifecycleOwner, Observer { boardGameDetailsList ->
            boardGameDetailsList.onEach { boardGame ->
                Timber.d("New update with details for board game ${boardGame.primaryName()}")
                // TODO: Find out where the updates should go?
                searchResultsAdapter.updateDetails(boardGame)
                hotBoardGameAdapter.updateDetails(boardGame)
                topBoardGameAdapter.updateDetails(boardGame)
            }
        })

        mainActivityViewModel.itemTypeToggled.observe(viewLifecycleOwner, Observer {
            searchResultsAdapter.notifyDataSetChanged()
        })

        // TODO: Podcast stuff
//        val intent = Intent(Intent.ACTION_VIEW)
//        intent.setDataAndType(Uri.parse("https://www.listennotes.com/c/r/29eaebf141384c2a9ba3aa78fca92632"), "application/rss+xml")
//        requireActivity().startActivity(intent)

        // TODO: Forum stuff
//        feedViewModel.getThread().observe(viewLifecycleOwner, Observer {
//            Timber.d("Got new thread... ${it.log()}")
//            if (it is SuccessResponse) {
//                Timber.d("Thread was ${it.data.articles.articles.firstOrNull()?.body}")
//            }
//        })
    }

    override fun onResume() {
        super.onResume()
        feedViewModel.loadRedditPosts()
    }

    private fun lazyLoadVideos() {
        val scrollBounds = Rect()
        feed_root.getHitRect(scrollBounds)
        feed_root.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { feedScroll, _, _, _, _ ->
            (feedScroll.getChildAt(0) as? ViewGroup)?.let { feedGroup ->
                if (feedGroup.children.any { it.id == R.id.videos_title_root && it.getLocalVisibleRect(scrollBounds) }) {
                    fetchYouTubeVideos()
                    feedScroll.removeScrollChangeListener()
                }
            }
        })
    }

    private fun updateHotGames(hotBoardGames: List<RankedBoardGame>) {
        hotBoardGameAdapter.updateResults(hotBoardGames)
    }

    private fun updateTopGames(topBoardGames: List<RankedBoardGame>) {
        topBoardGameAdapter.updateResults(topBoardGames)
    }

    private fun fetchRedditPosts() {
        reddit_all_button.setOnClickListener {
            Analytics.logEvent(EVENT_REDDIT_BOARDGAMES_OPEN)
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(REDDIT_BOARDGAMES_SUBREDDIT)))
        }
        reddit_try_again_button.setOnClickListener {
            feedViewModel.loadRedditPosts()
        }
        feedViewModel.redditPosts.observe(viewLifecycleOwner, Observer { redditResponse ->
            Timber.d("Got new Reddit response [${redditResponse}]")
            reddit_loading.visibleIf { redditResponse is LoadingResponse && redditAdapter.isEmpty() }
            reddit_all_button.visibleIf(View.INVISIBLE) { redditResponse is SuccessResponse || !redditAdapter.isEmpty() }
            reddit_error_root.visibleIf { redditResponse is ErrorResponse && redditAdapter.isEmpty() }
            if (redditResponse is SuccessResponse) {
                redditAdapter.submitList(redditResponse.data.data.children.map { it.data })
            }
        })
    }

    private fun fetchYouTubeVideos() {
        feedViewModel.enabledYouTubeChannelVideo.observe(viewLifecycleOwner, Observer { channelVideos ->
            // TODO: Implement error and empty views for videos?
            youtube_videos_loading.visibleIf { channelVideos is LoadingResponse }
            show_all_videos_button.visibleIf { channelVideos is SuccessResponse }
            when(channelVideos) {
                is SuccessResponse -> {
                    Analytics.logEvent(EVENT_YOUTUBE_SEARCH_SUCCESS)
                    updateVideos(channelVideos.data)
                }
                is ErrorResponse -> Analytics.logEvent(EVENT_YOUTUBE_SEARCH_ERROR)
            }
        })
    }

    private fun updateVideos(videos: YouTubeChannelVideos) {
        boardGameGeekVideos = videos.videos.map { it.toYouTubeVideoInfo() }
//        videoAdapter.updateResults(boardGameGeekVideos)
        videoAdapter.submitList(boardGameGeekVideos)
    }

    private fun setupReviewCard() {
        review_card.visibleIf { shouldPromptForReview() }
        if (review_card.isVisible) {
            Analytics.logEvent(EVENT_REVIEW_SHOWN)
        }
        never_review_button.setOnClickListener {
            Analytics.logEvent(EVENT_REVIEW_NEVER_ASK)
            AppPreferences.hasOptedOutReview = true
            review_card.animateToGone()
        }
        later_review_button.setOnClickListener {
            Analytics.logEvent(EVENT_REVIEW_MAYBE_LATER)
            review_card.animateToGone()
        }
        yes_review_button.setOnClickListener {
            Analytics.logEvent(EVENT_REVIEW_YES)
            review_card.gone()
            AppPreferences.hasReviewed = true
            activity?.redirectToPlayStore()
        }
    }

    private fun shouldPromptForReview() =
            canHandleRatingIntent() &&
                AppPreferences.startedAppCount % PROMPT_REVIEW_PERIOD == 0 &&
                !AppPreferences.hasOptedOutReview &&
                !AppPreferences.hasReviewed

    override fun onDestroy() {
        super.onDestroy()
        prefetchTimer.cancel()
        prefetchTimer.purge()
    }
}