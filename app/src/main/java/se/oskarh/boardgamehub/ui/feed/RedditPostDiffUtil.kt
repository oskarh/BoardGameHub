package se.oskarh.boardgamehub.ui.feed

import androidx.recyclerview.widget.DiffUtil
import se.oskarh.boardgamehub.api.model.reddit.DataX

object RedditPostDiffUtil : DiffUtil.ItemCallback<DataX>() {
    override fun areItemsTheSame(oldItem: DataX, newItem: DataX) =
            oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: DataX, newItem: DataX) =
            oldItem == newItem
}