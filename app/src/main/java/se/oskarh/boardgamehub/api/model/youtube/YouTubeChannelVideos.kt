package se.oskarh.boardgamehub.api.model.youtube

import com.google.gson.annotations.SerializedName

data class YouTubeChannelVideos(
    @SerializedName("items")
    val videos: List<YouTubeVideoLite>
)

data class YouTubeVideoLite(
    val id: Id,
    val snippet: SearchSnippet
) : Comparable<YouTubeVideoLite> {

    fun toYouTubeVideoInfo() =
        YouTubeVideoInfo(
            id.videoId,
            snippet.title,
            snippet.description,
            snippet.channelId,
            snippet.channelTitle,
            snippet.publishedAt,
            snippet.thumbnails.high.url)

    override fun compareTo(other: YouTubeVideoLite) =
        compareByDescending<YouTubeVideoLite> { youTubeDateFormatter.parse(it.snippet.publishedAt) }.compare(this, other)
}

data class Id(
    val videoId: String
)

data class SearchSnippet(
    val title: String,
    val description: String,
    val publishedAt: String,
    val channelId: String,
    val channelTitle: String,
    val thumbnails: Thumbnails
)

data class Thumbnails(
    val high: High
)

data class High(
    val height: Int,
    val url: String,
    val width: Int
)