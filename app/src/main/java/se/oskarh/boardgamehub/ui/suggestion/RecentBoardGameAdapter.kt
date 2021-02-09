package se.oskarh.boardgamehub.ui.suggestion

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recent_boardgame_item.view.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.util.extension.inflate
import se.oskarh.boardgamehub.util.extension.loadImage

class RecentBoardGameAdapter(private var recentBoardGames: List<BoardGame> = emptyList(),
                             private val boardGameClicked: (BoardGame) -> Unit,
                             private val boardGameDeletedClicked: (BoardGame) -> Unit)
    : RecyclerView.Adapter<RecentBoardGameAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.recent_boardgame_item))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(recentBoardGames[position])

    override fun getItemCount() = recentBoardGames.size

    fun updateResults(newBoardGames: List<BoardGame>) {
        recentBoardGames = newBoardGames
        notifyDataSetChanged()

//        val diffResult = DiffUtil.calculateDiff(BoardGameDiffUtil(newBoardGames, recentBoardGames))
//        recentBoardGames = newBoardGames
//        diffResult.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(recentBoardGame: BoardGame) {
            itemView.title.text = recentBoardGame.primaryName()
            itemView.boardgame_image.loadImage(recentBoardGame.thumbnailUrl)
            itemView.recent_boardgame_root.setOnClickListener { boardGameClicked(recentBoardGame) }
            itemView.delete_icon.setOnClickListener { boardGameDeletedClicked(recentBoardGame) }
        }
    }
}