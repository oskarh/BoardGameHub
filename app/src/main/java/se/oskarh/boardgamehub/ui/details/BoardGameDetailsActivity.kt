package se.oskarh.boardgamehub.ui.details

import android.os.Bundle
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.ui.base.BaseActivity
import se.oskarh.boardgamehub.util.KEY_DETAILS_ID
import se.oskarh.boardgamehub.util.KEY_DETAILS_TITLE
import se.oskarh.boardgamehub.util.extension.requireString

class BoardGameDetailsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boardgame_details)
        val title = intent.requireString(KEY_DETAILS_TITLE)
        val id = intent.getIntExtra(KEY_DETAILS_ID, -1)

        val detailsFragment = DetailsFragment.newInstance(title, id, DetailsSource.DETAILS)
        supportFragmentManager.beginTransaction()
            .add(R.id.boardgame_details_content, detailsFragment, detailsFragment.javaClass.simpleName)
            .commit()
    }
}