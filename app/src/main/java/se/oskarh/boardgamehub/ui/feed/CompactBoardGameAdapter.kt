package se.oskarh.boardgamehub.ui.feed

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.boardgame_compact_item.view.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.db.boardgame.RankedBoardGame
import se.oskarh.boardgamehub.util.BoardGameDetailsUpdate
import se.oskarh.boardgamehub.util.MAX_VISIBLE_FEED_GAMES
import se.oskarh.boardgamehub.util.extension.inflate
import se.oskarh.boardgamehub.util.extension.loadImage
import se.oskarh.boardgamehub.util.extension.visible
import se.oskarh.boardgamehub.util.extension.visibleIf
import timber.log.Timber
import kotlin.math.min

class CompactBoardGameAdapter(
    private var boardGames: MutableList<RankedBoardGame> = mutableListOf(),
    private val boardGameClicked: (RankedBoardGame) -> Unit) : RecyclerView.Adapter<CompactBoardGameAdapter.ViewHolder>() {

    // TODO: Rename
    var boardGameList: List<RankedBoardGame> = boardGames
        get() = boardGames
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.boardgame_compact_item))
    }

    override fun getItemCount() = min(boardGames.size, MAX_VISIBLE_FEED_GAMES)

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(boardGames[position])
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.any { it is BoardGameDetailsUpdate }) {
            viewHolder.updateDetails(boardGames[position].boardGame)
        } else {
            onBindViewHolder(viewHolder, position)
        }
    }

    fun updateResults(newBoardGames: List<RankedBoardGame>) {
        boardGames = newBoardGames.toMutableList()
        notifyDataSetChanged()
    }

    fun updateDetails(updatedBoardGame: BoardGame) {
        Timber.d("Updating details to ${updatedBoardGame.primaryName()}")
        boardGames.take(MAX_VISIBLE_FEED_GAMES)
            .indexOfFirst { it.boardGame.id == updatedBoardGame.id }
            .takeUnless { it == -1 }
            ?.let { index ->
                Timber.d("Finish Updating boardGame with index $index is ${boardGames[index].boardGame.yearPublished} ${boardGames[index].boardGame.primaryName()}")
                boardGames[index] = boardGames[index].copy(boardGame = updatedBoardGame)
                notifyItemChanged(index, BoardGameDetailsUpdate)
            }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(rankedBoardGame: RankedBoardGame) {
            itemView.title_text.text = rankedBoardGame.boardGame.primaryName()
            itemView.rating_text.text = rankedBoardGame.boardGame.formattedRating()
            itemView.rating_text.visibleIf(View.INVISIBLE) { rankedBoardGame.boardGame.hasStatistics() }
            itemView.players_text.text = rankedBoardGame.boardGame.playersFormatted()
            itemView.players_text.visibleIf(View.INVISIBLE) { rankedBoardGame.boardGame.hasStatistics() }
            itemView.boardgame_rank.text = rankedBoardGame.rank.toString()
            itemView.boardgame_image.loadImage(rankedBoardGame.boardGame.thumbnailUrl)
            itemView.boardgame_root.setOnClickListener {
                boardGameClicked(rankedBoardGame)
            }
        }

        fun updateDetails(boardGame: BoardGame) {
            itemView.boardgame_image.loadImage(boardGame.thumbnailUrl)
            itemView.rating_text.text = boardGame.formattedRating()
            itemView.rating_text.visible()
            itemView.players_text.text = boardGame.playersFormatted()
            itemView.players_text.visible()
        }
    }
}