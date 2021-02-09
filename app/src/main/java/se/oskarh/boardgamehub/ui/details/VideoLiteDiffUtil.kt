package se.oskarh.boardgamehub.ui.details

import androidx.recyclerview.widget.DiffUtil
import se.oskarh.boardgamehub.api.model.youtube.YouTubeVideoInfo

object VideoLiteDiffUtil : DiffUtil.ItemCallback<YouTubeVideoInfo>() {
    override fun areItemsTheSame(oldItem: YouTubeVideoInfo, newItem: YouTubeVideoInfo) =
            oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: YouTubeVideoInfo, newItem: YouTubeVideoInfo) =
            oldItem == newItem
}
