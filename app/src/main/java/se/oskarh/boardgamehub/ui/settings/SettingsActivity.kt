package se.oskarh.boardgamehub.ui.settings

import android.os.Bundle
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.ui.base.BaseActivity

class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.settings)

        val settingsFragment = SettingsFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.settings_content, settingsFragment, settingsFragment.javaClass.simpleName)
            .commit()
    }
}
