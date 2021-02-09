package se.oskarh.boardgamehub.ui.search

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.favorite_footer.view.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.util.extension.inflate

class FooterBoardGameAdapter(
    isRankedList: Boolean = false,
    boardGames: MutableList<BoardGame> = mutableListOf(),
    boardGameClicked: (BoardGame) -> Unit,
    boardGameShared: (BoardGame, View) -> Unit,
    val footerClicked: () -> Unit
) : BoardGameAdapter(isRankedList, boardGames, boardGameClicked, boardGameShared) {

    private val footerViewType = 3

    override val footerSize = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            footerViewType -> FooterHolder(parent.inflate(R.layout.favorite_footer))
            else -> super.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is FooterHolder -> viewHolder.bind()
            else -> super.onBindViewHolder(viewHolder, position - headerSize)
        }
    }

    override fun getItemViewType(position: Int) =
        when (position) {
            in 0 until boardGames.size -> super.getItemViewType(position)
            else -> footerViewType
        }

    inner class FooterHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            itemView.import_collection_button.setOnClickListener {
                footerClicked()
            }
        }
    }
}