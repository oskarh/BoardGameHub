package se.oskarh.boardgamehub.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.favorites_sheet.*
import kotlinx.android.synthetic.main.favorites_sheet.view.*
import se.oskarh.boardgamehub.BuildConfig
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.analytics.Analytics
import se.oskarh.boardgamehub.analytics.EVENT_CHANGELOG_MESSAGE
import se.oskarh.boardgamehub.analytics.EVENT_FAVORITES_OPEN
import se.oskarh.boardgamehub.analytics.EVENT_FILTER_COLLECTION_CHANGE
import se.oskarh.boardgamehub.analytics.EVENT_FORCE_UPDATE_TRIGGERED
import se.oskarh.boardgamehub.analytics.EVENT_SORT_COLLECTION_CHANGE
import se.oskarh.boardgamehub.analytics.EVENT_TOGGLE_LIST_MODE_FAVORITE
import se.oskarh.boardgamehub.analytics.EVENT_TOGGLE_LIST_MODE_MAIN
import se.oskarh.boardgamehub.api.CollectionSortAction
import se.oskarh.boardgamehub.databinding.ActivityMainBinding
import se.oskarh.boardgamehub.repository.ApiResponse
import se.oskarh.boardgamehub.repository.EmptyResponse
import se.oskarh.boardgamehub.repository.ErrorResponse
import se.oskarh.boardgamehub.repository.ImportCollectionService
import se.oskarh.boardgamehub.repository.LoadingResponse
import se.oskarh.boardgamehub.repository.SuccessResponse
import se.oskarh.boardgamehub.ui.about.AboutActivity
import se.oskarh.boardgamehub.ui.base.BaseActivity
import se.oskarh.boardgamehub.ui.common.BottomSheetHelper
import se.oskarh.boardgamehub.ui.common.showImportCollectionBottomDialog
import se.oskarh.boardgamehub.ui.details.DetailsFragment
import se.oskarh.boardgamehub.ui.details.DetailsSource
import se.oskarh.boardgamehub.ui.search.FooterBoardGameAdapter
import se.oskarh.boardgamehub.ui.settings.SettingsActivity
import se.oskarh.boardgamehub.ui.settings.SettingsFragment
import se.oskarh.boardgamehub.ui.settings.SettingsFragment.Companion.showChangelog
import se.oskarh.boardgamehub.ui.update.PromptUpdateActivity
import se.oskarh.boardgamehub.util.AppPreferences
import se.oskarh.boardgamehub.util.MAXIMUM_VOTED_BY_PLAYER_COUNT
import se.oskarh.boardgamehub.util.MINIMUM_VOTED_BY_PLAYER_COUNT
import se.oskarh.boardgamehub.util.PROPERTY_COLLECTION_USERNAME
import se.oskarh.boardgamehub.util.UPDATE_MESSAGE_VISIBILITY_DURATION
import se.oskarh.boardgamehub.util.extension.addFragment
import se.oskarh.boardgamehub.util.extension.closeKeyboardOnScrollStart
import se.oskarh.boardgamehub.util.extension.hideKeyboard
import se.oskarh.boardgamehub.util.extension.injector
import se.oskarh.boardgamehub.util.extension.lastAdapterIndex
import se.oskarh.boardgamehub.util.extension.log
import se.oskarh.boardgamehub.util.extension.setCursorEndOfLine
import se.oskarh.boardgamehub.util.extension.showActionSnackbar
import se.oskarh.boardgamehub.util.extension.showPopupMenu
import se.oskarh.boardgamehub.util.extension.showSnackbar
import se.oskarh.boardgamehub.util.extension.showTapTarget
import se.oskarh.boardgamehub.util.extension.startActivity
import se.oskarh.boardgamehub.util.extension.startMyService
import se.oskarh.boardgamehub.util.extension.textChangedObserver
import se.oskarh.boardgamehub.util.extension.toggleState
import se.oskarh.boardgamehub.util.extension.visibleIf
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel: MainActivityViewModel

    private lateinit var behavior: BottomSheetBehavior<LinearLayout>

    private val favoriteAdapter = FooterBoardGameAdapter(false, mutableListOf(), { boardGame ->
        addFragment(DetailsFragment.newInstance(boardGame.primaryName(), boardGame.id, DetailsSource.FAVORITE))
    }, { boardGame, item ->
        val isFavorite = viewModel.allFavoriteIds.value.orEmpty().contains(boardGame.id)
        item.showPopupMenu(boardGame, isFavorite) { boardGameId ->
            viewModel.toggleFavorite(boardGameId)
        }
    }, {
        onImportCollectionClicked()
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        injector.inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)
        binding.lifecycleOwner = this
        binding.activityViewModel = viewModel

        favorite_boardgames.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        favorite_boardgames.adapter = favoriteAdapter
        favorite_boardgames.layoutManager = LinearLayoutManager(this)
        favorite_boardgames.closeKeyboardOnScrollStart(this)

        viewModel.favoriteFilteredGames.observe(this, { favoriteState ->
            favorites_loading.visibleIf { favoriteState.isLoading }
            empty_favorites_message.visibleIf { favoriteState.allFavorites.isEmpty() }
            favorite_filter_clear.visibleIf(View.INVISIBLE) { favoriteState.filter.isNotEmpty() }
            Timber.d("New favorite state ${favoriteState.allFavorites.size}")
            favoriteAdapter.updateResults(favoriteState.filteredFavorites)
        })

        search_text.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewModel.hasSearchFocus(true)
            }
        }
        viewModel.searchStates.observe(this, { searchState: SearchState ->
            search_back.visibleIf(View.INVISIBLE) { searchState.isSearchBackVisible }
            search_clear.visibleIf(View.INVISIBLE) { searchState.isQueryClearVisible }
        })
        viewModel.exitSearch.observe(this, { event ->
            if (event.hasUpdate()) {
                showFeed()
            }
        })
        viewModel.suggestionClicked.observe(this, { event ->
            if (event.hasUpdate()) {
                binding.invalidateAll()
                search_text?.setCursorEndOfLine()
            }
        })
        search_clear.setOnClickListener {
            search_text.requestFocus()
            search_text.text?.clear()
        }
        search_back.setOnClickListener {
            viewModel.exitSearch()
        }

        behavior = BottomSheetBehavior.from(bottom_sheet)
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                arrow_icon.rotation = slideOffset * 180
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                hideKeyboard()
                if (newState == STATE_EXPANDED) {
                    Analytics.logEvent(EVENT_FAVORITES_OPEN)
                    if (!AppPreferences.hasShownImportOnboarding) {
                        bottom_sheet.favorite_boardgames.findViewHolderForAdapterPosition(
                                bottom_sheet.favorite_boardgames.lastAdapterIndex())?.itemView?.let {
                            showTapTarget(it, R.string.import_from_boardgamegeek, R.string.import_from_boardgamegeek_message)
                        }
                        AppPreferences.hasShownImportOnboarding = true
                    }
                }
            }
        })
        sheet_handle.setOnClickListener {
            behavior.toggleState()
        }
        favorite_filter_text.textChangedObserver().observe(this, { favoriteFilter ->
            viewModel.favoriteFilter.value = favoriteFilter.trim()
        })
        favorite_filter_clear.setOnClickListener {
            favorite_filter_text.text?.clear()
        }
        favorite_filter.setOnClickListener {
            BottomSheetHelper.showCollectionBottomFilter(this) {
                Analytics.logEvent(EVENT_FILTER_COLLECTION_CHANGE)
            }
        }
        favorite_sort.setOnClickListener {
            val collectionSortTitles = CollectionSortAction.values()
                    .map { getString(it.property) }
                    .toTypedArray()

            AlertDialog.Builder(this, R.style.AlertDialogTheme).run {
                setTitle(getString(R.string.sort_collection_title))
                setSingleChoiceItems(collectionSortTitles, AppPreferences.selectedCollectionSort.ordinal) { dialog, index ->
                    Analytics.logEvent(EVENT_SORT_COLLECTION_CHANGE)
                    val selectedSortAction = CollectionSortAction.values()[index]
                    if (selectedSortAction == CollectionSortAction.VOTED_BEST) {
                        selectBestVotedPlayerCount()
                    } else {
                        AppPreferences.selectedCollectionSort = selectedSortAction
                        main_content.showSnackbar(getString(R.string.sort_collection_message, getString(selectedSortAction.property).toLowerCase(Locale.ENGLISH)))
                    }
                    dialog.dismiss()
                }
                create()
                show()
            }
        }
        favorite_toggle_list.setImageResource(if (AppPreferences.isLargeResultsEnabled) R.drawable.ic_view_list_black_24dp else R.drawable.ic_view_module_black_24dp)
        favorite_toggle_list.setOnClickListener {
            AppPreferences.isLargeResultsEnabled = !AppPreferences.isLargeResultsEnabled
            viewModel.viewTypeToggled()
            Analytics.logEvent(EVENT_TOGGLE_LIST_MODE_FAVORITE)
            favorite_toggle_list.setImageResource(if (AppPreferences.isLargeResultsEnabled) R.drawable.ic_view_list_black_24dp else R.drawable.ic_view_module_black_24dp)
        }

        viewModel.importedCollectionResponses.observe(this, { importedGameResponse ->
            importedGameResponse.takeUnless { it.peekContent() is LoadingResponse<*> }
                    ?.getContentIfNotHandled()
                    ?.let { response: ApiResponse<Int> ->
                        Timber.d("Got collection update ${response.log()}")
                        when (response) {
                            is ErrorResponse<*> ->
                                main_content.showSnackbar(response.errorMessage, Snackbar.LENGTH_LONG)
                            is EmptyResponse<*> ->
                                main_content.showSnackbar(getString(R.string.import_board_games_empty), Snackbar.LENGTH_LONG)
                            is SuccessResponse<Int> ->
                                main_content.showSnackbar(getString(R.string.import_successful_message, response.data), Snackbar.LENGTH_LONG)
                            is LoadingResponse -> {}
                        }
                    }
        })
        viewModel.itemTypeToggled.observe(this, {
            favoriteAdapter.notifyDataSetChanged()
            val message = if (AppPreferences.isLargeResultsEnabled) R.string.large_board_games_displayed else R.string.small_board_games_displayed
            main_content.showSnackbar(message)
        })
        viewModel.allFavoriteIds.observe(this, {
            Timber.d("New list of favorite ids ${it.joinToString()}")
        })
        viewModel.addedFavorite.observe(this, { event ->
            if (event.hasUpdate()) {
                main_content.showSnackbar(R.string.added_favorite)
            }
        })
        viewModel.removedFavorite.observe(this, { event ->
            if (event.hasUpdate()) {
                main_content.showSnackbar(R.string.removed_favorite)
            }
        })

        supportFragmentManager.addOnBackStackChangedListener {
            bottom_sheet.visibleIf { supportFragmentManager.backStackEntryCount == 0 }
        }
        viewModel.isUpdateRequired.observe(this, { isUpdateRequired ->
            if (isUpdateRequired == true) {
                Analytics.logEvent(EVENT_FORCE_UPDATE_TRIGGERED)
                startActivity<PromptUpdateActivity>()
                finish()
            }
        })
        if (!AppPreferences.hasShownFavoritesOnboarding) {
            arrow_icon?.let { arrowIcon ->
                showTapTarget(arrowIcon, R.string.onboarding_favorites_title, R.string.onboarding_favorites_message)
                AppPreferences.hasShownFavoritesOnboarding = true
            }
        }
        AppPreferences.startedAppCount = AppPreferences.startedAppCount + 1

        if (isFirstOpenAfterAppUpdate()) {
            handleAppUpdate()
        }
    }

    private fun handleAppUpdate() {
        Timber.d("App was updated to ${BuildConfig.VERSION_NAME}")
        AppPreferences.currentAppVersion = BuildConfig.VERSION_NAME
        main_content.showActionSnackbar(
            getString(
                R.string.updated_to_version,
                BuildConfig.VERSION_NAME
            ), R.string.changelog, UPDATE_MESSAGE_VISIBILITY_DURATION
        ) {
            Analytics.logEvent(EVENT_CHANGELOG_MESSAGE)
            showChangelog(this)
        }
    }

    private fun isFirstOpenAfterAppUpdate() =
        AppPreferences.currentAppVersion != null && AppPreferences.currentAppVersion != BuildConfig.VERSION_NAME

    private fun showFeed() {
        hideKeyboard()
        search_text?.text?.clear()
        search_text?.clearFocus()
        viewModel.hasSearchFocus(false)
    }

    private fun showSuggestions() {
        search_text?.requestFocus()
        search_text?.text?.clear()
        viewModel.hasSearchFocus(true)
    }

    private fun selectBestVotedPlayerCount() {
        val bestVotedSortTitles = (MINIMUM_VOTED_BY_PLAYER_COUNT..MAXIMUM_VOTED_BY_PLAYER_COUNT)
                .map { it.toString() }
                .toTypedArray()

        AlertDialog.Builder(this, R.style.AlertDialogTheme).run {
            setTitle(getString(R.string.sort_best_voted_title))
            setSingleChoiceItems(bestVotedSortTitles, AppPreferences.bestVotedPlayerCount - 1) { dialog, index ->
                // TODO: Change to sealed class to better implement voted best argument..?
                AppPreferences.bestVotedPlayerCount = index + 1
                AppPreferences.selectedCollectionSort = CollectionSortAction.VOTED_BEST
                main_content.showSnackbar(getString(R.string.sort_collection_message,
                        "${getString(CollectionSortAction.VOTED_BEST.property).toLowerCase(Locale.ENGLISH)} ${getString(R.string.player_count, index + 1)}"))
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    private fun onImportCollectionClicked() {
        showImportCollectionBottomDialog(this) { username ->
            if (username.isBlank()) {
                main_content.showSnackbar(R.string.enter_username_to_import)
            } else {
                startMyService<ImportCollectionService>(PROPERTY_COLLECTION_USERNAME to username)
            }
        }
    }

    override fun onCreateOptionsMenu(newMenu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_options_menu, newMenu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                R.id.filter_action -> {
                    BottomSheetHelper.showBottomFilter(this)
                    true
                }
                R.id.sort_action -> {
                    SettingsFragment.changeSort(this)
                    true
                }
                R.id.toggle_list_mode_action -> {
                    AppPreferences.isLargeResultsEnabled = !AppPreferences.isLargeResultsEnabled
                    viewModel.viewTypeToggled()
                    Analytics.logEvent(EVENT_TOGGLE_LIST_MODE_MAIN)
                    true
                }
                R.id.settings_action -> {
                    startActivity<SettingsActivity>()
                    true
                }
                R.id.about_action -> {
                    startActivity<AboutActivity>()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            when {
                behavior.state == STATE_EXPANDED -> behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                viewModel.screenState.value == ShowSearch -> showSuggestions()
                viewModel.screenState.value is ShowSuggestions -> showFeed()
                else -> super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }
}
