package se.oskarh.boardgamehub.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.boardgame_list_fragment.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.analytics.Analytics
import se.oskarh.boardgamehub.analytics.EVENT_TOGGLE_LIST_MODE_LIST
import se.oskarh.boardgamehub.databinding.BoardgameListFragmentBinding
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.ui.base.BaseFragment
import se.oskarh.boardgamehub.ui.common.PrefetchTimer
import se.oskarh.boardgamehub.ui.details.DetailsFragment
import se.oskarh.boardgamehub.ui.details.DetailsSource
import se.oskarh.boardgamehub.ui.search.BoardGameAdapter
import se.oskarh.boardgamehub.util.AppPreferences
import se.oskarh.boardgamehub.util.KEY_BOARDGAME_LIST
import se.oskarh.boardgamehub.util.KEY_DETAILS_SOURCE
import se.oskarh.boardgamehub.util.KEY_IS_RANKED_LIST
import se.oskarh.boardgamehub.util.KEY_PARENT_CONTAINER
import se.oskarh.boardgamehub.util.extension.addFragment
import se.oskarh.boardgamehub.util.extension.injector
import se.oskarh.boardgamehub.util.extension.prefetchItemIndexes
import se.oskarh.boardgamehub.util.extension.requireArgumentEnum
import se.oskarh.boardgamehub.util.extension.requireArgumentInt
import se.oskarh.boardgamehub.util.extension.showPopupMenu
import timber.log.Timber
import javax.inject.Inject

class BoardGameListFragment : BaseFragment() {

    private lateinit var binding: BoardgameListFragmentBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var listViewModel: BoardGameListViewModel

    private lateinit var boardGames: List<BoardGame>

    private var parentContainer: Int = -1

    // TODO: Use a delegate for this here and in FeedFragment?
    private val prefetchTimer = PrefetchTimer()

    private lateinit var gameListAdapter: BoardGameAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = BoardgameListFragmentBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injector.inject(this)
        listViewModel = ViewModelProvider(this, viewModelFactory).get(BoardGameListViewModel::class.java)
        parentContainer = requireArgumentInt(KEY_PARENT_CONTAINER)
        boardGames = requireArguments().getParcelableArrayList<BoardGame>(KEY_BOARDGAME_LIST) as List<BoardGame>
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        boardgame_list.layoutManager = LinearLayoutManager(requireActivity())
        val isRanked = requireArguments().getBoolean(KEY_IS_RANKED_LIST)
        val source = requireArgumentEnum<DetailsSource>(KEY_DETAILS_SOURCE)
        gameListAdapter = BoardGameAdapter(isRanked, boardGames.toMutableList(), { boardGame: BoardGame ->
            val detailsFragment = DetailsFragment.newInstance(boardGame.primaryName(), boardGame.id, source)
            listViewModel.viewedGame(boardGame.id)
            requireActivity().addFragment(detailsFragment, parentContainer)
        }, { boardGame, item ->
            val isFavorite = listViewModel.allFavoriteIds.value.orEmpty().contains(boardGame.id)
            item.showPopupMenu(boardGame, isFavorite) { boardGameId ->
                listViewModel.toggleFavorite(boardGameId)
            }
        })
        boardgame_list.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        boardgame_list.adapter = gameListAdapter
        listViewModel.populateFromCache(boardGames.map { it.id })

        prefetchTimer.liveData.observe(viewLifecycleOwner, Observer {
            val visibleItems = boardgame_list.prefetchItemIndexes()
                .mapNotNull {
                    gameListAdapter.boardGames.getOrNull(it)?.id
                }.toList()

            Timber.d("Fetching details for ${visibleItems.joinToString()}")
            listViewModel.fetchDetails(visibleItems)
        })
        prefetchTimer.reset()
        listViewModel.boardGameDetails.observe(viewLifecycleOwner, Observer {
            it.map { boardGame ->
                Timber.d("New update with details $boardGame")
                gameListAdapter.updateDetails(boardGame)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toggle_list_mode_action -> {
                AppPreferences.isLargeResultsEnabled = !AppPreferences.isLargeResultsEnabled
                gameListAdapter.notifyDataSetChanged()
                Analytics.logEvent(EVENT_TOGGLE_LIST_MODE_LIST)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun newInstance(isRanked: Boolean, @IdRes parentContainer: Int, boardGames: List<BoardGame>, source: DetailsSource): BoardGameListFragment =
            BoardGameListFragment().apply {
                arguments = bundleOf(
                    KEY_IS_RANKED_LIST to isRanked,
                    KEY_PARENT_CONTAINER to parentContainer,
                    KEY_BOARDGAME_LIST to boardGames,
                    KEY_DETAILS_SOURCE to source)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        prefetchTimer.cancel()
        prefetchTimer.purge()
    }
}