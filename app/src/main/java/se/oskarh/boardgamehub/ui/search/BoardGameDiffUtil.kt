package se.oskarh.boardgamehub.ui.search

import androidx.recyclerview.widget.DiffUtil
import se.oskarh.boardgamehub.db.boardgame.BoardGame

object BoardGameDiffUtil : DiffUtil.ItemCallback<BoardGame>() {
    override fun areItemsTheSame(oldItem: BoardGame, newItem: BoardGame) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: BoardGame, newItem: BoardGame) =
        oldItem.imageUrl == newItem.imageUrl &&
                oldItem.playersFormatted() == newItem.playersFormatted() &&
                oldItem.playingTimeFormatted() == newItem.playingTimeFormatted() &&
                oldItem.formattedRating() == newItem.formattedRating() &&
                oldItem.yearPublished == newItem.yearPublished
}
