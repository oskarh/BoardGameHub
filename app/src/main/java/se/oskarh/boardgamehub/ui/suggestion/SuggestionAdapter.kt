package se.oskarh.boardgamehub.ui.suggestion

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.suggestion_item.view.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.db.suggestion.Suggestion
import se.oskarh.boardgamehub.util.extension.inflate

class SuggestionAdapter(
    private var suggestions: List<Suggestion> = emptyList(),
    private val suggestionClicked: (Suggestion) -> Unit,
    private val suggestionDeletedClicked: (Suggestion) -> Unit
) : RecyclerView.Adapter<SuggestionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.suggestion_item))
    }

    override fun getItemCount() = suggestions.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(suggestions[position])
    }

    fun updateResults(newSuggestions: List<Suggestion>) {
        suggestions = newSuggestions
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(suggestion: Suggestion) {
            itemView.suggestion_text.text = suggestion.originalSuggestion
            itemView.suggestion_root.setOnClickListener { suggestionClicked(suggestion) }
            itemView.delete_icon.setOnClickListener { suggestionDeletedClicked(suggestion) }
        }
    }
}