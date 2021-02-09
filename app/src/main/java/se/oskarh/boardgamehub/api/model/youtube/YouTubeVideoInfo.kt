package se.oskarh.boardgamehub.api.model.youtube

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.Locale

// TODO: Set Locale correctly
val youTubeDateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.GERMANY)
val standardDateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY)

@Parcelize
data class YouTubeVideoInfo(
    val id: String,
    val title: String,
    val description: String,
    val channelId: String,
    val channelTitle: String,
    val publishedDate: String,
    val imageUrl: String,
    val viewCount: String = ""
): Parcelable {

    @IgnoredOnParcel
    val hasViewCount = viewCount.isNotBlank()

    // TODO: Handle this safely?
    fun publishedDateFormatted(): String {
        val youTubeDate = youTubeDateFormatter.parse(publishedDate)
        return standardDateFormatter.format(youTubeDate)
    }
}