package se.oskarh.boardgamehub.ui.details

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.comment_footer.view.*
import kotlinx.android.synthetic.main.comment_item.view.*
import kotlinx.android.synthetic.main.search_header.view.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.util.extension.inflate
import se.oskarh.boardgamehub.util.extension.visibleIf
import timber.log.Timber

class CommentsAdapter(private var comments: List<BoardGame.Comment> = emptyList()) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val headerViewType = 0
    private val itemViewType = 1
    private val footerViewType = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            headerViewType -> HeaderHolder(parent.inflate(R.layout.search_header))
            itemViewType -> ViewHolder(parent.inflate(R.layout.comment_item))
            footerViewType -> FooterHolder(parent.inflate(R.layout.comment_footer))
            else -> throw IllegalStateException("Unknown view type [$viewType]")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        when (holder) {
            is HeaderHolder -> holder.bind()
            is ViewHolder -> holder.bind(comments[position - 1])
            is FooterHolder -> holder.bind()
            else -> throw IllegalStateException("Unknown holder")
        }

    override fun getItemViewType(position: Int) =
        when (position) {
            0 -> headerViewType
            itemCount - 1 -> footerViewType
            else -> itemViewType
        }

    override fun getItemCount() = comments.size + 2

    fun updateResults(newComments: List<BoardGame.Comment>) {
        comments = newComments
        notifyDataSetChanged()
    }

    inner class HeaderHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            itemView.search_header.text = itemView.resources.getQuantityString(R.plurals.comments, comments.size, comments.size)
        }
    }

    inner class FooterHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            // TODO: Make this gone if more comments don't exist. Disable when loading
            Timber.d("Has all comments ${comments.joinToString()}")
            // TODO: Fix in next release!!!
            // TODO: Fix comments properly
            itemView.load_more_button.visibleIf { false }
//            itemView.load_more_button.visibleIf { !comments.hasAllComments() }
            itemView.load_more_button.setOnClickListener {
                // TODO: Set disabled etc?
                Timber.d("Clicked load more comments...")
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(comment: BoardGame.Comment) {
            itemView.username.text = comment.username
            itemView.rating.text = comment.rating
            itemView.comment.text = comment.text
        }
    }
}