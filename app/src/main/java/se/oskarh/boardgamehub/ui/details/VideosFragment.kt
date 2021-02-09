package se.oskarh.boardgamehub.ui.details

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.videos_page.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.analytics.Analytics
import se.oskarh.boardgamehub.analytics.EVENT_YOUTUBE_SEARCH
import se.oskarh.boardgamehub.analytics.EVENT_YOUTUBE_VIDEO_OPEN
import se.oskarh.boardgamehub.api.model.youtube.YouTubeVideoDetails
import se.oskarh.boardgamehub.databinding.VideosPageBinding
import se.oskarh.boardgamehub.repository.ApiResponse
import se.oskarh.boardgamehub.repository.EmptyResponse
import se.oskarh.boardgamehub.repository.ErrorResponse
import se.oskarh.boardgamehub.repository.LoadingResponse
import se.oskarh.boardgamehub.repository.SuccessResponse
import se.oskarh.boardgamehub.ui.common.LazyLoadableFragment
import se.oskarh.boardgamehub.util.AppPreferences
import se.oskarh.boardgamehub.util.extension.canResolveYouTubeSearch
import se.oskarh.boardgamehub.util.extension.injector
import se.oskarh.boardgamehub.util.extension.searchYouTube
import se.oskarh.boardgamehub.util.extension.showTapTarget
import se.oskarh.boardgamehub.util.extension.startVideoActivity
import se.oskarh.boardgamehub.util.extension.visibleIf
import timber.log.Timber
import javax.inject.Inject

class VideosFragment : LazyLoadableFragment() {

    private lateinit var binding: VideosPageBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var detailsViewModel: DetailsViewModel

    private var isLoadInitiated = false

    private val youTubeVideos: LiveData<ApiResponse<YouTubeVideoDetails>> by lazy {
        detailsViewModel.youTubeVideoDetails()
    }

    private val videoAdapter by lazy {
        VideoAdapter(emptyList()) { youTubeVideo ->
            Analytics.logEvent(EVENT_YOUTUBE_VIDEO_OPEN)
            activity?.startVideoActivity(youTubeVideo.toYouTubeVideoInfo())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = VideosPageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injector.inject(this)
        search_youtube_button.visibleIf { view.context.canResolveYouTubeSearch() }
        video_list.run {
            layoutManager = LinearLayoutManager(view.context)
            adapter = videoAdapter
        }
        videos_try_again_button.setOnClickListener {
            Timber.d("Clicked try again button")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        detailsViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(DetailsViewModel::class.java)
        detailsViewModel.boardGameName.observe(viewLifecycleOwner, Observer { title ->
            if(title != null) {
                search_youtube_button.setOnClickListener {
                    Analytics.logEvent(EVENT_YOUTUBE_SEARCH)
                    activity?.searchYouTube(title)
                }
            }
        })
        if (isLoadInitiated) {
            loadData()
        }
    }

    override fun onLoad() {
        if (!AppPreferences.hasShownYouTubeSearchOnboarding) {
            Handler().postDelayed({
                activity?.showTapTarget(search_youtube_button, R.string.onboarding_youtube_search_title, R.string.onboarding_youtube_search_message)
            }, 250)
            AppPreferences.hasShownYouTubeSearchOnboarding = true
        }
        isLoadInitiated = true
        if(::detailsViewModel.isInitialized) {
            loadData()
        }
    }

    private fun loadData() {
        youTubeVideos.observe(viewLifecycleOwner, Observer { response ->
            Timber.d("Got youtube response $response")
            when (response) {
                is SuccessResponse -> {
                    Timber.d("YouTubeResponse [${response.data.videos.joinToString()}]")
                    videoAdapter.updateResults(response.data.videos)
                }
                is ErrorResponse -> Timber.d("Error message loading videos ${response.errorMessage}")
            }
            videos_empty_message.visibleIf { response is EmptyResponse }
            videos_error_message.visibleIf { response is ErrorResponse }
            videos_try_again_button.visibleIf { response is ErrorResponse }
            content_root.visibleIf { response is SuccessResponse }
            videos_loading.visibleIf { response is LoadingResponse }
        })
    }
}