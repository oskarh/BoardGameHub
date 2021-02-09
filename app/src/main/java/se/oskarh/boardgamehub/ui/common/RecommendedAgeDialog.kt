package se.oskarh.boardgamehub.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.recommended_age_dialog.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.R.layout
import se.oskarh.boardgamehub.R.string
import se.oskarh.boardgamehub.analytics.Analytics
import se.oskarh.boardgamehub.analytics.EVENT_DIALOG_RECOMMENDED_PLAYERS
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.util.PROPERTY_RECOMMENDED_AGE
import se.oskarh.boardgamehub.util.extension.getCompatColor
import se.oskarh.boardgamehub.util.extension.keepDigits
import se.oskarh.boardgamehub.util.extension.requireArgumentParcelable
import java.text.NumberFormat
import java.util.Locale

class RecommendedAgeDialog : BottomSheetDialogFragment() {

    private lateinit var recommendations: BoardGame.Poll

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layout.recommended_age_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recommendations = requireArgumentParcelable(PROPERTY_RECOMMENDED_AGE)
        number_votes.text =
            getString(string.user_votes, NumberFormat.getNumberInstance(Locale.getDefault()).format(recommendations.totalVotes))

        val nonEmptyRecommendations = recommendations.results
            .first()
            .results.filter { it.numberOfVotes > 0 }
        val votingData = nonEmptyRecommendations.mapIndexed { i, recommendation ->
            BarEntry(i.toFloat(), recommendation.numberOfVotes.toFloat())
        }
        val votingLabels = nonEmptyRecommendations.map { recommendation ->
            "${recommendation.value.keepDigits()}+"
        }

        val barDataSet = BarDataSet(votingData, "").apply {
            color = view.context.getCompatColor(R.color.colorAccent)
            valueTextColor = view.context.getCompatColor(R.color.primaryTextColor)
            valueTextSize = 18f
            valueFormatter = IntValueFormatter()
        }

        recommended_age_graph.run {
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
            xAxis.valueFormatter = IndexAxisValueFormatter(votingLabels)
            setTouchEnabled(false)
        }
    }

    companion object {
        fun newInstance(recommendations: BoardGame.Poll): RecommendedAgeDialog {
            Analytics.logEvent(EVENT_DIALOG_RECOMMENDED_PLAYERS)
            return RecommendedAgeDialog().apply {
                arguments = bundleOf(PROPERTY_RECOMMENDED_AGE to recommendations)
            }
        }
    }
}