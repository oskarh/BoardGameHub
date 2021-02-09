package se.oskarh.boardgamehub.ui.common

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.StackedValueFormatter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.recommended_players_dialog.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.R.layout
import se.oskarh.boardgamehub.analytics.Analytics
import se.oskarh.boardgamehub.analytics.EVENT_DIALOG_RECOMMENDED_AGE
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.util.PROPERTY_RECOMMENDED_PLAYERS
import se.oskarh.boardgamehub.util.RECOMMENDATION_COLORS
import se.oskarh.boardgamehub.util.extension.getCompatColor
import se.oskarh.boardgamehub.util.extension.requireArgumentParcelable

class RecommendedPlayersDialog : BottomSheetDialogFragment() {

    private lateinit var poll: BoardGame.Poll

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layout.recommended_players_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        poll = requireArgumentParcelable(PROPERTY_RECOMMENDED_PLAYERS)

        val votingData = poll.results
            .mapIndexed { index, recommendation ->
                val bestVotes = recommendation.results.firstOrNull {
                    it.value.equals("best", true)
                }?.numberOfVotes?.toFloat() ?: 0f
                val recommendedVotes = recommendation.results.firstOrNull {
                    it.value.equals("recommended", true)
                }?.numberOfVotes?.toFloat() ?: 0f
                val notRecommendedVotes = recommendation.results.firstOrNull {
                    it.value.equals("not recommended", true)
                }?.numberOfVotes?.toFloat() ?: 0f
                BarEntry(index.toFloat(), floatArrayOf(bestVotes, recommendedVotes, notRecommendedVotes))
            }
        val votingLabels = poll.results.map {
            it.numberOfPlayers.orEmpty()
        }

        val barDataSet = BarDataSet(votingData, "").apply {
            colors = RECOMMENDATION_COLORS.map { view.context.getCompatColor(it) }
            stackLabels = arrayOf(getString(R.string.best), getString(R.string.recommended), getString(R.string.not_recommended))
            valueTextColor = view.context.getCompatColor(R.color.primaryTextColor)
            valueTextSize = 18f
            valueFormatter = StackedValueFormatter(false, "", 0)
        }

        recommended_players_graph.run {
            data = BarData(barDataSet)
            legend.isEnabled = true
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
            xAxis.valueFormatter = IndexAxisValueFormatter(votingLabels)
            setTouchEnabled(false)
        }

        recommended_players_graph.legend.run {
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            orientation = Legend.LegendOrientation.HORIZONTAL
            setDrawInside(false)
            textColor = Color.WHITE
            textSize = 12f
            formSize = 12f
            formToTextSpace = 8f
            xEntrySpace = 16f
        }
    }

    companion object {
        fun newInstance(poll: BoardGame.Poll): RecommendedPlayersDialog {
            Analytics.logEvent(EVENT_DIALOG_RECOMMENDED_AGE)
            return RecommendedPlayersDialog().apply {
                arguments = bundleOf(PROPERTY_RECOMMENDED_PLAYERS to poll)
            }
        }
    }
}