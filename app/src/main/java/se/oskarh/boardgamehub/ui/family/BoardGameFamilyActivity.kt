package se.oskarh.boardgamehub.ui.family

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_boardgame_family.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.api.model.BggBoardGameFamily
import se.oskarh.boardgamehub.repository.ErrorResponse
import se.oskarh.boardgamehub.repository.LoadingResponse
import se.oskarh.boardgamehub.repository.SuccessResponse
import se.oskarh.boardgamehub.ui.base.BaseActivity
import se.oskarh.boardgamehub.ui.details.DetailsSource
import se.oskarh.boardgamehub.ui.list.BoardGameListFragment
import se.oskarh.boardgamehub.util.AppPreferences
import se.oskarh.boardgamehub.util.KEY_BOARDGAME_FAMILY_ID
import se.oskarh.boardgamehub.util.KEY_TITLE
import se.oskarh.boardgamehub.util.extension.injector
import se.oskarh.boardgamehub.util.extension.rotateBy
import se.oskarh.boardgamehub.util.extension.visibleIf
import timber.log.Timber
import javax.inject.Inject

class BoardGameFamilyActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var familyViewModel: BoardGameFamilyViewModel

    private var isDescriptionExpanded = false

    private lateinit var listModeIcon: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        setContentView(R.layout.activity_boardgame_family)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = intent.getStringExtra(KEY_TITLE)
        familyViewModel = ViewModelProvider(this, viewModelFactory).get(BoardGameFamilyViewModel::class.java)
        val familyId = intent.getIntExtra(KEY_BOARDGAME_FAMILY_ID, -1)
        familyViewModel.findFamily(familyId)
        familyViewModel.familyResponse.observe(this, Observer { response ->
            // TODO: Add error handling
            family_loading.visibleIf { response is LoadingResponse }
            family_error.visibleIf { response is ErrorResponse }
            family_content.visibleIf { response is SuccessResponse }
            if (response is SuccessResponse) {
                setupUi(response.data.boardgameFamily)
            }
        })
        description_root.setOnClickListener {
            isDescriptionExpanded = !isDescriptionExpanded
            expand_description.rotateBy(180f)
            if (isDescriptionExpanded) {
                family_description.maxLines = Int.MAX_VALUE
                show_more_text.text = getString(R.string.show_less)
            } else {
                family_description.maxLines = 2
                show_more_text.text = getString(R.string.show_more)
            }
        }
        supportFragmentManager.addOnBackStackChangedListener {
            description_root.visibleIf { supportFragmentManager.backStackEntryCount == 0 }
        }
    }

    private fun setupUi(boardGameFamily: BggBoardGameFamily) {
        family_description.text = boardGameFamily.description
        val listFragment = BoardGameListFragment.newInstance(false, R.id.boardgame_family_content, boardGameFamily.boardGames(), DetailsSource.FAMILY)
        supportFragmentManager.beginTransaction()
            .replace(R.id.boardgame_family_content, listFragment, listFragment.javaClass.simpleName)
            .commit()
    }

    override fun onCreateOptionsMenu(newMenu: Menu): Boolean {
        menuInflater.inflate(R.menu.list_options_menu, newMenu)
        listModeIcon = newMenu.findItem(R.id.toggle_list_mode_action)
        listModeIcon.setIcon(if (AppPreferences.isLargeResultsEnabled) R.drawable.ic_view_list_black_24dp else R.drawable.ic_view_module_black_24dp)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Timber.d("Selected size large ${AppPreferences.isLargeResultsEnabled}")
        when (item.itemId) {
            R.id.toggle_list_mode_action -> {
                listModeIcon.setIcon(if (AppPreferences.isLargeResultsEnabled) R.drawable.ic_view_module_black_24dp else R.drawable.ic_view_list_black_24dp)
                return false
            }
        }
        return super.onOptionsItemSelected(item)
    }
}