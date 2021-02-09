package se.oskarh.boardgamehub.ui.list

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.analytics.ScreenType
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.ui.base.BaseActivity
import se.oskarh.boardgamehub.ui.details.DetailsSource
import se.oskarh.boardgamehub.util.AppPreferences
import se.oskarh.boardgamehub.util.KEY_BOARDGAME_LIST
import se.oskarh.boardgamehub.util.KEY_DETAILS_SOURCE
import se.oskarh.boardgamehub.util.KEY_IS_RANKED_LIST
import se.oskarh.boardgamehub.util.KEY_SCREEN
import se.oskarh.boardgamehub.util.KEY_TITLE
import se.oskarh.boardgamehub.util.extension.argumentEnum
import se.oskarh.boardgamehub.util.extension.setCurrentScreen
import java.util.ArrayList

class BoardGameListActivity : BaseActivity() {

    private lateinit var listModeIcon: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boardgame_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = intent.getStringExtra(KEY_TITLE)
        setCurrentScreen(argumentEnum<ScreenType>(KEY_SCREEN) ?: ScreenType.GAME_LIST)
        val isRankedList = intent.getBooleanExtra(KEY_IS_RANKED_LIST, true)
        val boardGames = intent.getParcelableArrayListExtra<BoardGame>(KEY_BOARDGAME_LIST) as ArrayList<BoardGame>

        val detailsSource = argumentEnum<DetailsSource>(KEY_DETAILS_SOURCE) ?: DetailsSource.UNKNOWN_LIST
        val listFragment = BoardGameListFragment.newInstance(isRankedList, R.id.boardgame_list_content, boardGames, detailsSource)
        supportFragmentManager.beginTransaction()
            .add(R.id.boardgame_list_content, listFragment, listFragment.javaClass.simpleName)
            .commit()
    }

    override fun onCreateOptionsMenu(newMenu: Menu): Boolean {
        menuInflater.inflate(R.menu.list_options_menu, newMenu)
        listModeIcon = newMenu.findItem(R.id.toggle_list_mode_action)
        listModeIcon.setIcon(if (AppPreferences.isLargeResultsEnabled) R.drawable.ic_view_list_black_24dp else R.drawable.ic_view_module_black_24dp)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toggle_list_mode_action -> {
                listModeIcon.setIcon(if (AppPreferences.isLargeResultsEnabled) R.drawable.ic_view_module_black_24dp else R.drawable.ic_view_list_black_24dp)
                return false
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
