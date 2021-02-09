package se.oskarh.boardgamehub.ui.search

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.boardgame_large_item.view.*
import kotlinx.android.synthetic.main.boardgame_small_item.view.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.util.ANIMATION_ITEM_NOT_SELECTED_SIZE
import se.oskarh.boardgamehub.util.AppPreferences
import se.oskarh.boardgamehub.util.BoardGameDetailsUpdate
import se.oskarh.boardgamehub.util.COULD_NOT_PARSE_DEFAULT
import se.oskarh.boardgamehub.util.ChangeSizeDetector
import se.oskarh.boardgamehub.util.extension.animateToSize
import se.oskarh.boardgamehub.util.extension.getCompatColor
import se.oskarh.boardgamehub.util.extension.gone
import se.oskarh.boardgamehub.util.extension.inflate
import se.oskarh.boardgamehub.util.extension.loadImage
import se.oskarh.boardgamehub.util.extension.loadImageAnimateBackground
import se.oskarh.boardgamehub.util.extension.visible
import se.oskarh.boardgamehub.util.extension.visibleIf
import timber.log.Timber

open class BoardGameAdapter(
    val isRankedList: Boolean = false,
    var boardGames: MutableList<BoardGame> = mutableListOf(),
    private val boardGameClicked: (BoardGame) -> Unit,
    private val onLongClicked: (BoardGame, View) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val smallItemViewType = 0

    private val largeItemViewType = 1

    open val headerSize = 0

    open val footerSize = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            smallItemViewType -> SmallItemHolder(parent.inflate(R.layout.boardgame_small_item))
            largeItemViewType -> LargeItemHolder(parent.inflate(R.layout.boardgame_large_item))
            else -> throw IllegalStateException("Unknown view type [$viewType]")
        }
    }

    override fun getItemCount() = boardGames.size + headerSize + footerSize

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is SmallItemHolder -> viewHolder.bind(boardGames[position])
            is LargeItemHolder -> viewHolder.bind(boardGames[position])
            else -> throw IllegalStateException("Illegal type for ViewHolder [${viewHolder.javaClass.simpleName}]")
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>) {
        when {
            (viewHolder is LargeItemHolder && payloads.any { it is BoardGameDetailsUpdate }) ->
                viewHolder.updateDetails(boardGames[position])
            (viewHolder is SmallItemHolder && payloads.any { it is BoardGameDetailsUpdate }) ->
                viewHolder.updateDetails(boardGames[position])
            else -> onBindViewHolder(viewHolder, position)
        }
    }

    override fun getItemViewType(position: Int) =
        if (AppPreferences.isLargeResultsEnabled) {
            largeItemViewType
        } else {
            smallItemViewType
        }

    fun updateResults(newBoardGames: List<BoardGame>) {
        // TODO: Fix this, use DiffUtil
        Timber.d("Updating to [${newBoardGames.joinToString { it.primaryName() }}]")
        boardGames = newBoardGames.toMutableList()
        notifyDataSetChanged()
    }

    fun updateDetails(updatedBoardGame: BoardGame) {
        Timber.d("Updating details to ${updatedBoardGame.primaryName()}")
        boardGames.indexOfFirst { it.id == updatedBoardGame.id }
            .takeUnless { it == -1 }
            ?.let { index ->
                if (boardGames[index] != updatedBoardGame) {
                    Timber.d("Finish Updating boardGame with index $index is ${boardGames[index].yearPublished} ${boardGames[index].primaryName()}")
                    boardGames[index] = updatedBoardGame
                    notifyItemChanged(index + headerSize, BoardGameDetailsUpdate)
                }
            }
    }

    private fun setPopupMenu(view: View, boardGame: BoardGame) {
        val gestureDetector = GestureDetector(view.context, ChangeSizeDetector(view) {
            onLongClicked(boardGame, view)
        })
        view.setOnTouchListener { _, event ->
            when {
                gestureDetector.onTouchEvent(event) -> true
                event.action == MotionEvent.ACTION_CANCEL -> {
                    view.animateToSize(ANIMATION_ITEM_NOT_SELECTED_SIZE)
                    false
                }
                else -> false
            }
        }
    }

    inner class SmallItemHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(boardGame: BoardGame) {
            boardGame.thumbnailUrl?.let { image ->
                itemView.small_boardgame_image.loadImage(image)
            } ?: run {
                Timber.d("No image for ${boardGame.primaryName()}")
                itemView.small_boardgame_image.setImageDrawable(null)
            }
            itemView.small_title_text.text = formatYearPublished(boardGame)
            itemView.boardgame_type_space.visibleIf { !isRankedList }
            itemView.small_boardgame_type.visibleIf { !isRankedList }
            itemView.small_boardgame_type.setImageResource(boardGame.type.icon)
            itemView.small_rating_image.visibleIf { boardGame.hasStatistics() }
            itemView.small_rating_text.visibleIf { boardGame.hasStatistics() }
            itemView.small_rating_text.text = boardGame.formattedRating()
            itemView.small_players_image.visibleIf { boardGame.hasPlayers() }
            itemView.small_players_text.visibleIf { boardGame.hasPlayers() }
            itemView.small_players_text.text = boardGame.playersFormatted()
            itemView.small_boardgame_rank.visibleIf { isRankedList }
            itemView.small_boardgame_rank.text = (adapterPosition + 1).toString()

            itemView.setOnClickListener {
                boardGameClicked(boardGame)
            }
            setPopupMenu(itemView, boardGame)
        }

        private fun formatYearPublished(boardGame: BoardGame) =
            when {
                boardGame.yearPublished < 0 -> "${boardGame.primaryName()} (${boardGame.yearPublished} BC)"
                boardGame.yearPublished == 0 -> boardGame.primaryName()
                else -> "${boardGame.primaryName()} (${boardGame.yearPublished})"
            }

        // TODO: Call all visibility modifiers from same method for bind and updateDetails
        fun updateDetails(boardGame: BoardGame) {
            Timber.d("Updating details to ${boardGame.primaryName()}")
            // TODO: Animate this in?
            itemView.small_rating_image.visible()
            itemView.small_rating_text.visible()
            itemView.small_rating_text.text = boardGame.formattedRating()
            itemView.small_players_text.visible()
            itemView.small_players_text.text = boardGame.playersFormatted()
            itemView.small_boardgame_image.loadImage(boardGame.thumbnailUrl)
        }
    }

    inner class LargeItemHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(boardGame: BoardGame) {
            Timber.d("Binding $boardGame")
            boardGame.imageUrl?.let { image ->
                itemView.boardgame_image.loadImageAnimateBackground(image)
            } ?: run {
                Timber.d("Image missing for ${boardGame.primaryName()} setting background to black")
                itemView.boardgame_image.setBackgroundColor(itemView.context.getCompatColor(R.color.secondaryBackgroundColor))
                itemView.boardgame_image.setImageDrawable(null)
                itemView.boardgame_image.resetLoader()
            }
            itemView.title_text.text = boardGame.primaryName()
            itemView.boardgame_type.setImageResource(boardGame.type.icon)
            itemView.published_year.visibleIf { boardGame.yearPublished != 0 }
            itemView.published_year.text =
                if (boardGame.yearPublished > 0) {
                    boardGame.yearPublished.toString()
                } else {
                    itemView.context.getString(R.string.year_bc, boardGame.yearPublished)
                }
            itemView.details_progress.visibleIf { boardGame.statistics == null }
            itemView.details_properties.visibleIf { boardGame.statistics != null }
            itemView.rating_image.visibleIf { boardGame.hasStatistics() }
            itemView.rating_text.visibleIf { boardGame.hasStatistics() }
            itemView.rating_text.text = boardGame.formattedRating()
            itemView.players_image.visibleIf { boardGame.hasPlayers() }
            itemView.players_text.visibleIf { boardGame.hasPlayers() }
            itemView.players_text.text = boardGame.playersFormatted()
            itemView.time_image.visibleIf { boardGame.hasPlayingTime() }
            itemView.time_text.visibleIf { boardGame.hasPlayingTime() }
            // TODO: Fix below for i18n
            itemView.time_text.text = itemView.context.getString(R.string.playing_time_formatted, boardGame.playingTimeFormatted())
            itemView.boardgame_rank.visibleIf { isRankedList }
            itemView.boardgame_rank.text = (adapterPosition + 1).toString()

            itemView.setOnClickListener {
                boardGameClicked(boardGame)
            }
            setPopupMenu(itemView, boardGame)
        }

        // TODO: Call all visibility modifiers from same method for bind and updateDetails
        fun updateDetails(boardGame: BoardGame) {
            Timber.d("Updating details to ${boardGame.primaryName()}")
            // TODO: Animate this in?
            itemView.details_properties.visible()
            itemView.details_progress.gone()
            itemView.rating_image.visible()
            itemView.rating_text.visible()
            itemView.players_image.visible()
            itemView.players_text.visible()
            itemView.time_image.visible()
            itemView.time_text.visible()
            itemView.boardgame_image.loadImageAnimateBackground(boardGame.imageUrl)
            itemView.rating_text.text = boardGame.formattedRating()
            itemView.players_text.text = boardGame.playersFormatted()
            itemView.time_text.text = boardGame.playingTimeFormatted()
                .takeUnless { it == COULD_NOT_PARSE_DEFAULT }
                ?.let { playingTimeFormatted ->
                    itemView.context.getString(R.string.minutes, playingTimeFormatted)
                } ?: COULD_NOT_PARSE_DEFAULT
        }
    }
}