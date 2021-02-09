package se.oskarh.boardgamehub.ui.update

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.prompt_update_activity.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.databinding.PromptUpdateActivityBinding
import se.oskarh.boardgamehub.ui.base.BaseActivity
import se.oskarh.boardgamehub.util.extension.redirectToPlayStore

class PromptUpdateActivity : BaseActivity() {

    private lateinit var binding: PromptUpdateActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.prompt_update_activity)
        update_button.setOnClickListener {
            redirectToPlayStore()
            finish()
        }
    }
}