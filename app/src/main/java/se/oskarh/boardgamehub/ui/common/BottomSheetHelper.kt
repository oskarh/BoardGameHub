package se.oskarh.boardgamehub.ui.common

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.core.graphics.drawable.toDrawable
import kotlinx.android.synthetic.main.filter_collection_dialog_bottom.*
import kotlinx.android.synthetic.main.filter_dialog_bottom.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.analytics.Analytics
import se.oskarh.boardgamehub.analytics.EVENT_FILTER_CHANGE
import se.oskarh.boardgamehub.api.CollectionFilterAction
import se.oskarh.boardgamehub.api.FilterAction
import se.oskarh.boardgamehub.util.AppPreferences
import se.oskarh.boardgamehub.util.extension.getCompatColor
import se.oskarh.boardgamehub.util.extension.visibleIf
import kotlin.math.max
import kotlin.math.min

object BottomSheetHelper {

    fun showBottomFilter(context: Context, onDismissed: (FilterAction) -> Unit = {}) {
        HideableBottomSheetDialog(context).apply {
            setContentView(layoutInflater.inflate(R.layout.filter_dialog_bottom, null))
            AppPreferences.selectedFilter.run {
                start_year_picker.value = startYear
                end_year_picker.value = endYear
                games_shown_checkbox.isChecked = areStandaloneGamesIncluded
                expansions_shown_checkbox.isChecked = areExpansionsIncluded
                only_show_published_year_checkbox.isChecked = !areAllYearsIncluded
            }
            // TODO: Refactor this into something decently nice
            val backgroundColor =
                if (only_show_published_year_checkbox.isChecked) {
                    context.getCompatColor(android.R.color.transparent)
                } else {
                    context.getCompatColor(R.color.primary0_transparent)
                }
            start_year_picker.isEnabled = only_show_published_year_checkbox.isChecked
            end_year_picker.isEnabled = only_show_published_year_checkbox.isChecked
            start_year_picker.setOnValueChangedListener { _, _, newValue ->
                end_year_picker.value = max(end_year_picker.value, newValue)
                end_year_picker.wrapSelectorWheel = false
            }
            end_year_picker.setOnValueChangedListener { _, _, newValue ->
                start_year_picker.value = min(start_year_picker.value, newValue)
                end_year_picker.wrapSelectorWheel = false
            }
            games_shown_checkbox.setOnClickListener {
                hideable = (games_shown_checkbox.isChecked || expansions_shown_checkbox.isChecked)
                validaton_failed_message.visibleIf(View.INVISIBLE) { !games_shown_checkbox.isChecked && !expansions_shown_checkbox.isChecked }
            }
            expansions_shown_checkbox.setOnClickListener {
                hideable = (games_shown_checkbox.isChecked || expansions_shown_checkbox.isChecked)
                validaton_failed_message.visibleIf(View.INVISIBLE) { !games_shown_checkbox.isChecked && !expansions_shown_checkbox.isChecked }
            }
            year_chooser_overlay.setBackgroundColor(backgroundColor)
            only_show_published_year_checkbox.setOnClickListener {
                val overlayBackgroundColor =
                    if (only_show_published_year_checkbox.isChecked) {
                        context.getCompatColor(android.R.color.transparent)
                    } else {
                        context.getCompatColor(R.color.primary0_transparent)
                    }
                start_year_picker.isEnabled = only_show_published_year_checkbox.isChecked
                end_year_picker.isEnabled = only_show_published_year_checkbox.isChecked
                year_chooser_overlay.setBackgroundColor(overlayBackgroundColor)
            }
            setOnDismissListener {
                val newFilter = FilterAction(
                    games_shown_checkbox.isChecked,
                    expansions_shown_checkbox.isChecked,
                    !only_show_published_year_checkbox.isChecked,
                    start_year_picker.value,
                    end_year_picker.value
                )
                if (newFilter != AppPreferences.selectedFilter) {
                    Analytics.logEvent(EVENT_FILTER_CHANGE)
                    AppPreferences.selectedFilter = newFilter
                }
                onDismissed(newFilter)
            }
            show()
        }
    }

    fun showCollectionBottomFilter(context: Context, onDismissed: (CollectionFilterAction) -> Unit = {}) {
        HideableBottomSheetDialog(context).apply {
            setContentView(layoutInflater.inflate(R.layout.filter_collection_dialog_bottom, null))
            AppPreferences.selectedCollectionFilter.run {
                players_picker.value = filteredNumberPlayers
                collection_games_shown_checkbox.isChecked = areStandaloneGamesIncluded
                collection_expansions_shown_checkbox.isChecked = areExpansionsIncluded
                only_show_players_checkbox.isChecked = !areAllPlayersIncluded
            }
            // TODO: Refactor this into something decently nice
            players_picker.isEnabled = only_show_players_checkbox.isChecked
            players_picker.wrapSelectorWheel = false
            collection_games_shown_checkbox.setOnClickListener {
                hideable = (collection_games_shown_checkbox.isChecked || collection_expansions_shown_checkbox.isChecked)
                collection_validaton_failed_message.visibleIf(View.INVISIBLE) { !collection_games_shown_checkbox.isChecked && !collection_expansions_shown_checkbox.isChecked }
            }
            collection_expansions_shown_checkbox.setOnClickListener {
                hideable = (collection_games_shown_checkbox.isChecked || collection_expansions_shown_checkbox.isChecked)
                collection_validaton_failed_message.visibleIf(View.INVISIBLE) { !collection_games_shown_checkbox.isChecked && !collection_expansions_shown_checkbox.isChecked }
            }
            player_chooser_overlay.updateForegroundColor(only_show_players_checkbox.isChecked)
            only_show_players_checkbox.setOnClickListener {
                player_chooser_overlay.updateForegroundColor(only_show_players_checkbox.isChecked)
                players_picker.isEnabled = only_show_players_checkbox.isChecked
            }
            setOnDismissListener {
                val newFilter = CollectionFilterAction(
                        collection_games_shown_checkbox.isChecked,
                        collection_expansions_shown_checkbox.isChecked,
                        !only_show_players_checkbox.isChecked,
                        players_picker.value
                )
                if (newFilter != AppPreferences.selectedCollectionFilter) {
                    AppPreferences.selectedCollectionFilter = newFilter
                }
                onDismissed(newFilter)
            }
            show()
        }
    }

    private fun LinearLayout.updateForegroundColor(isEnabled: Boolean) {
        val overlayBackgroundColor =
                if (isEnabled) {
                    context.getCompatColor(android.R.color.transparent)
                } else {
                    context.getCompatColor(R.color.primary0_transparent)
                }
        foreground = overlayBackgroundColor.toDrawable()
    }
}