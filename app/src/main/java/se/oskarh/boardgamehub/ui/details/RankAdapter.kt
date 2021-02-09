package se.oskarh.boardgamehub.ui.details

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.ranking_item.view.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.util.extension.inflate

class RankAdapter(
    private val rankings: List<BoardGame.Rank>
) : RecyclerView.Adapter<RankAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.ranking_item))
    }

    override fun getItemCount() = rankings.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(rankings[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        @SuppressLint("SetTextI18n")
        fun bind(rank: BoardGame.Rank) {
            val ranking =
                if (rank.value.isDigitsOnly()) {
                    "#${rank.value}"
                } else {
                    rank.value
                }
            itemView.boardgame_rank.text = ranking
            itemView.type_name.text = rank.friendlyname
        }
    }
}