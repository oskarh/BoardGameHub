package se.oskarh.boardgamehub.repository

import androidx.lifecycle.LiveData
import retrofit2.http.GET
import retrofit2.http.Query
import se.oskarh.boardgamehub.api.model.youtube.YouTubeChannelVideos
import se.oskarh.boardgamehub.api.model.youtube.YouTubeVideoDetails
import se.oskarh.boardgamehub.util.YOUTUBE_CHANNEL_ID
import se.oskarh.boardgamehub.util.YOUTUBE_FIELDS
import se.oskarh.boardgamehub.util.YOUTUBE_ID
import se.oskarh.boardgamehub.util.YOUTUBE_KEY
import se.oskarh.boardgamehub.util.YOUTUBE_MAX_RESULTS
import se.oskarh.boardgamehub.util.YOUTUBE_ORDER
import se.oskarh.boardgamehub.util.YOUTUBE_PART
import se.oskarh.boardgamehub.util.YOUTUBE_SEARCH_CHANNEL
import se.oskarh.boardgamehub.util.YOUTUBE_TYPE
import se.oskarh.boardgamehub.util.YOUTUBE_VIDEO_INFO

interface YouTubeService {

    @GET(YOUTUBE_VIDEO_INFO)
    fun getYouTubeVideoDetails(
        @Query(YOUTUBE_ID) ids: String,
        @Query(YOUTUBE_KEY) key: String,
        @Query(YOUTUBE_PART) part: String,
        @Query(YOUTUBE_FIELDS) fields: String): LiveData<ApiResponse<YouTubeVideoDetails>>

    @GET(YOUTUBE_SEARCH_CHANNEL)
    fun getYouTubeChannelVideos(
        @Query(YOUTUBE_CHANNEL_ID) channelId: String,
        @Query(YOUTUBE_KEY) key: String,
        @Query(YOUTUBE_PART) part: String,
        @Query(YOUTUBE_FIELDS) fields: String,
        @Query(YOUTUBE_MAX_RESULTS) maxResults: Int,
        @Query(YOUTUBE_TYPE) type: String,
        @Query(YOUTUBE_ORDER) order: String): LiveData<ApiResponse<YouTubeChannelVideos>>
}
