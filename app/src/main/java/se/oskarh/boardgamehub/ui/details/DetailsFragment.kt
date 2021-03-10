package se.oskarh.boardgamehub.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.details_fragment.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.analytics.Analytics
import se.oskarh.boardgamehub.analytics.EVENT_OPEN_COVER_IMAGE
import se.oskarh.boardgamehub.analytics.EVENT_OPEN_DETAILS
import se.oskarh.boardgamehub.analytics.EVENT_PROPERTY_BOARDGAME_ID
import se.oskarh.boardgamehub.analytics.EVENT_PROPERTY_DETAILS_SOURCE
import se.oskarh.boardgamehub.analytics.EVENT_TAB_COMMENTS
import se.oskarh.boardgamehub.analytics.EVENT_TAB_DETAILS
import se.oskarh.boardgamehub.api.PollType
import se.oskarh.boardgamehub.databinding.DetailsFragmentBinding
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.repository.ErrorResponse
import se.oskarh.boardgamehub.repository.LoadingResponse
import se.oskarh.boardgamehub.repository.SuccessResponse
import se.oskarh.boardgamehub.ui.base.BaseFragment
import se.oskarh.boardgamehub.ui.common.LazyLoadableFragment
import se.oskarh.boardgamehub.ui.common.RankingDialog
import se.oskarh.boardgamehub.ui.common.RecommendedPlayersDialog
import se.oskarh.boardgamehub.ui.coverdetails.CoverDetailsActivity
import se.oskarh.boardgamehub.util.BOARDGAME_DETAILS_ID
import se.oskarh.boardgamehub.util.BOARDGAME_TITLE
import se.oskarh.boardgamehub.util.COULD_NOT_PARSE_DEFAULT
import se.oskarh.boardgamehub.util.KEY_IMAGE_URL
import se.oskarh.boardgamehub.util.MINIMUM_REQUIRED_VOTES
import se.oskarh.boardgamehub.util.extension.addOnPageSelectedListener
import se.oskarh.boardgamehub.util.extension.injector
import se.oskarh.boardgamehub.util.extension.loadImageChangeBackground
import se.oskarh.boardgamehub.util.extension.requireArgumentInt
import se.oskarh.boardgamehub.util.extension.requireArgumentString
import se.oskarh.boardgamehub.util.extension.showSnackbar
import se.oskarh.boardgamehub.util.extension.startActivity
import se.oskarh.boardgamehub.util.extension.visible
import se.oskarh.boardgamehub.util.extension.visibleIf
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.abs

class DetailsFragment : BaseFragment() {

    private lateinit var binding: DetailsFragmentBinding

    private lateinit var gameTitle: String

    private var boardGameId: Int = -1

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var detailsViewModel: DetailsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DetailsFragmentBinding.inflate(inflater)
        boardGameId = requireArgumentInt(BOARDGAME_DETAILS_ID)
        gameTitle = requireArgumentString(BOARDGAME_TITLE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injector.inject(this)
        details_app_bar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            setToolbarAlpha(abs(verticalOffset) / appBarLayout.totalScrollRange.toFloat())
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        detailsViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(DetailsViewModel::class.java)
        // TODO: Have a function or similar for this instead of a variable for currentBoardGame?
        // TODO: Also needed to implement refresh in a better way
        detailsViewModel.currentBoardGame.value = boardGameId
        // TODO: Fix
        detailsViewModel.detailsResponse.observe(viewLifecycleOwner, { response ->
            details_progress.visibleIf { response is LoadingResponse }
            error_root.visibleIf { response is ErrorResponse }
            if (response is SuccessResponse) {
                setupUi(response.data)
                detailsViewModel.viewedGame(response.data.id)
            }
        })
        view_pager.offscreenPageLimit = 2
        view_pager.adapter = DetailsAdapter(childFragmentManager).apply {
            view_pager.addOnPageSelectedListener { activePage ->
                onPageSelected(activePage)
            }
        }
        details_tabs.setupWithViewPager(view_pager)
        requireActivity().findViewById<Toolbar>(R.id.details_toolbar).run {
            navigationIcon = ContextCompat.getDrawable(context, R.drawable.ic_arrow_back)
            setNavigationOnClickListener { activity?.onBackPressed() }
            title = gameTitle
        }
        details_try_again_button.setOnClickListener {
            detailsViewModel.currentBoardGame.value = boardGameId
        }
    }

    private fun setToolbarAlpha(alpha: Float) {
        activity?.findViewById<Toolbar>(R.id.details_toolbar)?.alpha = alpha
    }

    // TODO: Need to do this in a better way so it doesn't glitch?
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    // TODO: Need to do this in a better way so it doesn't glitch?
    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    private fun setupUi(gameDetails: BoardGame) {
        Timber.d("Setting UI to $gameDetails")
        boardgame_image.loadImageChangeBackground(gameDetails.imageUrl)
        boardgame_image.setOnClickListener {
            gameDetails.imageUrl?.let { image ->
                Analytics.logEvent(EVENT_OPEN_COVER_IMAGE)
                startActivity<CoverDetailsActivity>(KEY_IMAGE_URL to image)
            }
        }
        published_year.visibleIf { gameDetails.yearPublished != 0 }
        published_year.text = gameDetails.yearPublished.toString()

        players.text = gameDetails.playersFormatted()
        players.setOnClickListener { showRecommendedPlayersDialog() }
        players_image.setOnClickListener { showRecommendedPlayersDialog() }
        // TODO: Move out
        playing_time.text =
            if (gameDetails.playingTimeFormatted() != "0") getString(
                R.string.playing_time_formatted,
                gameDetails.playingTimeFormatted()
            ) else COULD_NOT_PARSE_DEFAULT
        rating_image.setOnClickListener { showRankInformation() }
        rating_text.setOnClickListener { showRankInformation() }
        rating_text.text = gameDetails.formattedRating()
        details_properties.visible()
    }

    private fun showRecommendedPlayersDialog() {
        (detailsViewModel.detailsResponse.value as? SuccessResponse)?.data
            ?.getPoll(PollType.SUGGESTED_NUMBER_OF_PLAYERS)
            ?.takeIf { it.totalVotes > MINIMUM_REQUIRED_VOTES }
            ?.let { poll: BoardGame.Poll ->
                RecommendedPlayersDialog.newInstance(poll).show(parentFragmentManager, null)
            } ?: players_image.showSnackbar(R.string.recommended_number_players_missing)
    }

    private fun showRankInformation() {
        (detailsViewModel.detailsResponse.value as? SuccessResponse)?.data?.statistics?.let { statistics ->
            RankingDialog.newInstance(statistics).show(parentFragmentManager, null)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(BOARDGAME_DETAILS_ID, boardGameId)
    }

    companion object {
        fun newInstance(gameTitle: String, id: Int, source: DetailsSource): DetailsFragment {
            // TODO: Change to only use one of these
            Analytics.logEvent(source.eventName)
            Analytics.logEvent(EVENT_OPEN_DETAILS, EVENT_PROPERTY_BOARDGAME_ID to id, EVENT_PROPERTY_DETAILS_SOURCE to source.eventName)
            return DetailsFragment().apply {
                arguments = bundleOf(BOARDGAME_TITLE to gameTitle, BOARDGAME_DETAILS_ID to id)
            }
        }
    }

    private inner class DetailsAdapter(fragmentManager: FragmentManager) :
        FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val detailsPages: List<LazyLoadableFragment> = listOf(
            InformationFragment(),
//            VideosFragment(),
            CommentsFragment()
        )

        fun onPageSelected(position: Int) {
            val eventName = when (position) {
                0 -> EVENT_TAB_DETAILS
//                1 -> EVENT_TAB_VIDEOS
                else -> EVENT_TAB_COMMENTS
            }
            Analytics.logEvent(eventName)
            detailsPages[position].load()
        }

        override fun getCount(): Int = detailsPages.size

        override fun getItem(position: Int) = detailsPages[position]

        override fun getPageTitle(position: Int) =
            when (position) {
                0 -> getString(R.string.tab_details)
//                1 -> getString(R.string.tab_videos)
                else -> getString(R.string.tab_comments)
            }
    }
}