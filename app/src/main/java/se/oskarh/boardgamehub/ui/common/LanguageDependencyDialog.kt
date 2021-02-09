package se.oskarh.boardgamehub.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.language_dependency_dialog.*
import kotlinx.android.synthetic.main.recommended_age_dialog.number_votes
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.R.layout
import se.oskarh.boardgamehub.R.string
import se.oskarh.boardgamehub.analytics.Analytics
import se.oskarh.boardgamehub.analytics.EVENT_DIALOG_LANGUAGE_DEPENDENCY
import se.oskarh.boardgamehub.api.LanguageDependencyDescription
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.ui.details.LanguageDependencyAdapter
import se.oskarh.boardgamehub.util.PROPERTY_LANGUAGE_DEPENDENCY
import se.oskarh.boardgamehub.util.extension.getCompatColor
import se.oskarh.boardgamehub.util.extension.requireArgumentParcelable
import java.text.NumberFormat
import java.util.Locale

class LanguageDependencyDialog : BottomSheetDialogFragment() {

    private lateinit var poll: BoardGame.Poll

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layout.language_dependency_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        poll = requireArgumentParcelable(PROPERTY_LANGUAGE_DEPENDENCY)
        number_votes.text =
            getString(string.user_votes, NumberFormat.getNumberInstance(Locale.getDefault()).format(poll.totalVotes))

        val recommendations = poll.results.first()
        val votingData = recommendations.results
            .filter { it.numberOfVotes > 0 }
            .mapIndexed { i, recommendation ->
                BarEntry(i.toFloat(), recommendation.numberOfVotes.toFloat())
            }
        val votingLabels = recommendations.results.mapIndexed { i, recommendation ->
            LanguageDependencyDescription((i + 1).toString(), recommendation.value, recommendation.numberOfVotes)
        }.filter {
            it.votes > 0
        }

        val barDataSet = BarDataSet(votingData, "").apply {
            color = view.context.getCompatColor(R.color.colorAccent)
            valueTextColor = view.context.getCompatColor(R.color.primaryTextColor)
            valueTextSize = 18f
            valueFormatter = IntValueFormatter()
        }

        language_dependency_graph.run {
            data = BarData(barDataSet)
            legend.isEnabled = false
            description.isEnabled = false

            axisLeft.axisMinimum = 0f
            axisRight.axisMinimum = 0f
            axisLeft.isEnabled = false
            axisRight.isEnabled = false

            extraBottomOffset = 8f
            xAxis.setDrawGridLines(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textColor = view.context.getCompatColor(R.color.secondaryTextColor)
            xAxis.textSize = 16f
            xAxis.valueFormatter = IndexAxisValueFormatter(votingLabels.map { it.level })
            setTouchEnabled(false)
        }
        description_list.layoutManager = LinearLayoutManager(view.context)
        description_list.adapter = LanguageDependencyAdapter(votingLabels)
    }

    companion object {
        fun newInstance(poll: BoardGame.Poll): LanguageDependencyDialog {
            Analytics.logEvent(EVENT_DIALOG_LANGUAGE_DEPENDENCY)
            return LanguageDependencyDialog().apply {
                arguments = bundleOf(PROPERTY_LANGUAGE_DEPENDENCY to poll)
            }
        }
    }
}