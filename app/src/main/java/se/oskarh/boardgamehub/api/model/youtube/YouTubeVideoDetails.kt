package se.oskarh.boardgamehub.api.model.youtube

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import se.oskarh.boardgamehub.util.INDEX_NOT_FOUND
import se.oskarh.boardgamehub.util.YOUTUBE_FORMAT_VIEWS_TO_K_THRESHOLD
import se.oskarh.boardgamehub.util.extension.toIntOrZero

data class YouTubeVideoDetails(
    @SerializedName("items")
    val videos: List<YouTubeVideo>
)

@Parcelize
@SuppressLint("ParcelCreator")
data class YouTubeVideo(
    val contentDetails: ContentDetails,
    val id: String,
    val snippet: VideosSnippet,
    val statistics: YouTubeStatistics
): Parcelable {

    fun toYouTubeVideoInfo() =
        YouTubeVideoInfo(id,
            snippet.title,
            snippet.description,
            snippet.channelId,
            snippet.channelTitle,
            snippet.publishedAt,
            snippet.thumbnails.high.url,
            statistics.viewCount)

    fun durationFormatted(durationString: String = contentDetails.duration): String {
        val duration = getDuration(durationString)
        val hours = duration / 3600
        val minutes = (duration % 3600) / 60
        val seconds = duration % 60

        val minutesSeconds = "${String.format("%02d", minutes)}:${String.format("%02d", seconds)}"
        return if (hours > 0) {
            "${String.format("%02d", hours)}:$minutesSeconds"
        } else {
            minutesSeconds
        }
    }

    fun viewsFormatted() =
        if (statistics.viewCount.toIntOrZero() >= YOUTUBE_FORMAT_VIEWS_TO_K_THRESHOLD) {
            "${((statistics.viewCount.toIntOrZero() + 500) / 1000)}K"
        } else {
            statistics.viewCount
        }

    private fun getDuration(durationString: String): Long {
        var time = durationString.substring(2)
        var duration = 0L
        val indexes = arrayOf(arrayOf("H", 3600), arrayOf("M", 60), arrayOf("S", 1))
        for (i in indexes.indices) {
            val index = time.indexOf(indexes[i].first() as String)
            if (index != INDEX_NOT_FOUND) {
                val value = time.substring(0, index)
                duration += (Integer.parseInt(value) * indexes[i][1] as Int).toLong()
                time = time.substring(value.length + 1)
            }
        }
        return duration
    }
}

@Parcelize
@SuppressLint("ParcelCreator")
data class VideosSnippet(
    val channelId: String,
    val channelTitle: String,
    val description: String,
    val publishedAt: String,
    val title: String,
    val thumbnails: Thumbnail
): Parcelable

@Parcelize
@SuppressLint("ParcelCreator")
data class YouTubeStatistics(
    val viewCount: String
): Parcelable

@Parcelize
@SuppressLint("ParcelCreator")
data class ContentDetails(
    val duration: String
): Parcelable

@Parcelize
@SuppressLint("ParcelCreator")
data class Thumbnail(
    val high: ThumbnailProperties
): Parcelable

@Parcelize
@SuppressLint("ParcelCreator")
data class ThumbnailProperties(
    val url: String,
    val width: Int,
    val height: Int
): Parcelable