package se.oskarh.boardgamehub.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.ranking_dialog.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.analytics.Analytics
import se.oskarh.boardgamehub.analytics.EVENT_DIALOG_RATING
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.ui.details.RankAdapter
import se.oskarh.boardgamehub.util.PROPERTY_STATISTICS
import se.oskarh.boardgamehub.util.extension.formatRating
import se.oskarh.boardgamehub.util.extension.requireArgumentParcelable
import java.text.NumberFormat
import java.util.Locale

class RankingDialog : BottomSheetDialogFragment() {

    private lateinit var statistics: BoardGame.Statistics

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.ranking_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        statistics = requireArgumentParcelable(PROPERTY_STATISTICS)
        user_average_rating.text = statistics.userAverage.formatRating(true)
        bayes_average_rating.text = statistics.bayesAverage.formatRating(true)
        number_ratings.text =
            getString(R.string.user_ratings, NumberFormat.getNumberInstance(Locale.getDefault()).format(statistics.usersRated))
        ranking_list.layoutManager = LinearLayoutManager(view.context)
        ranking_list.adapter = RankAdapter(statistics.ranks)
    }

    companion object {
        fun newInstance(statistics: BoardGame.Statistics): RankingDialog {
            Analytics.logEvent(EVENT_DIALOG_RATING)
            return RankingDialog().apply {
                arguments = bundleOf(PROPERTY_STATISTICS to statistics)
            }
        }
    }
}