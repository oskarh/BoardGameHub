package se.oskarh.boardgamehub.ui.details

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.language_dependency_description_item.view.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.api.LanguageDependencyDescription
import se.oskarh.boardgamehub.util.extension.inflate

class LanguageDependencyAdapter(
    private val descriptions: List<LanguageDependencyDescription>
) : RecyclerView.Adapter<LanguageDependencyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.language_dependency_description_item))
    }

    override fun getItemCount() = descriptions.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(descriptions[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(dependencyDescription: LanguageDependencyDescription) {
            itemView.dependency_level.text = dependencyDescription.level
            itemView.dependency_description.text = dependencyDescription.description
        }
    }
}