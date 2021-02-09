package se.oskarh.boardgamehub.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import dagger.Reusable
import se.oskarh.boardgamehub.api.YouTubeChannel
import se.oskarh.boardgamehub.api.model.youtube.YouTubeChannelVideos
import se.oskarh.boardgamehub.api.model.youtube.YouTubeVideoDetails
import se.oskarh.boardgamehub.util.YOUTUBE_CHANNEL_VIDEOS_FIELDS
import se.oskarh.boardgamehub.util.YOUTUBE_DEFAULT_KEY
import se.oskarh.boardgamehub.util.YOUTUBE_DEFAULT_MAX_RESULTS
import se.oskarh.boardgamehub.util.YOUTUBE_DEFAULT_ORDER
import se.oskarh.boardgamehub.util.YOUTUBE_DEFAULT_PART
import se.oskarh.boardgamehub.util.YOUTUBE_DEFAULT_TYPE
import se.oskarh.boardgamehub.util.YOUTUBE_SEARCH_PART
import se.oskarh.boardgamehub.util.YOUTUBE_VIDEO_DETAILS_FIELDS
import javax.inject.Inject

@Reusable
class YouTubeRepository @Inject constructor(
    private val youTubeService: YouTubeService) {

    // TODO: Fix keys
    fun getYouTubeVideoDetails(ids: List<String>): LiveData<ApiResponse<YouTubeVideoDetails>> =
        youTubeService.getYouTubeVideoDetails(
            ids.joinToString(),
            YOUTUBE_DEFAULT_KEY,
            YOUTUBE_DEFAULT_PART,
            YOUTUBE_VIDEO_DETAILS_FIELDS
        )

    fun getYouTubeChannelVideos(youTubeChannel: YouTubeChannel): LiveData<ApiResponse<YouTubeChannelVideos>> {
        return MediatorLiveData<ApiResponse<YouTubeChannelVideos>>().apply {
            value = LoadingResponse()
            addSource(youTubeService.getYouTubeChannelVideos(
                youTubeChannel.channelId,
                YOUTUBE_DEFAULT_KEY,
                YOUTUBE_SEARCH_PART,
                YOUTUBE_CHANNEL_VIDEOS_FIELDS,
                YOUTUBE_DEFAULT_MAX_RESULTS,
                YOUTUBE_DEFAULT_TYPE,
                YOUTUBE_DEFAULT_ORDER)) { videosResponse ->

                value = videosResponse
            }
        }
    }
}