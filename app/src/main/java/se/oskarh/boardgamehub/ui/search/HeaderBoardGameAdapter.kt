package se.oskarh.boardgamehub.ui.search

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.search_header.view.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.util.BoardGameDetailsUpdate
import se.oskarh.boardgamehub.util.extension.inflate

class HeaderBoardGameAdapter(
    isRankedList: Boolean = false,
    boardGames: MutableList<BoardGame> = mutableListOf(),
    boardGameClicked: (BoardGame) -> Unit,
    boardGameShared: (BoardGame, View) -> Unit
) : BoardGameAdapter(isRankedList, boardGames, boardGameClicked, boardGameShared) {

    private val headerViewType = 2

    override val headerSize = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            headerViewType -> HeaderHolder(parent.inflate(R.layout.search_header))
            else -> super.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is HeaderHolder -> viewHolder.bind(boardGames.size)
            else -> super.onBindViewHolder(viewHolder, position - headerSize)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>) {
        if (viewHolder is SmallItemHolder && payloads.any { it is BoardGameDetailsUpdate }) {
            viewHolder.updateDetails(boardGames[position - headerSize])
        } else if (viewHolder is LargeItemHolder && payloads.any { it is BoardGameDetailsUpdate }) {
            viewHolder.updateDetails(boardGames[position - headerSize])
        } else {
            onBindViewHolder(viewHolder, position)
        }
    }

    override fun getItemViewType(position: Int) =
        when (position) {
            in 0 until headerSize -> headerViewType
            else -> super.getItemViewType(position)
        }

    inner class HeaderHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(boardGameCount: Int) {
            itemView.search_header.text = itemView.resources.getQuantityString(R.plurals.search_header_text, boardGameCount, boardGameCount)
        }
    }
}