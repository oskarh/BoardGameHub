package se.oskarh.boardgamehub.ui.coverdetails

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_cover_details.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.ui.base.BaseActivity
import se.oskarh.boardgamehub.util.KEY_IMAGE_URL
import se.oskarh.boardgamehub.util.extension.loadImage
import se.oskarh.boardgamehub.util.extension.requireString

class CoverDetailsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cover_details)
        cover_image.loadImage(intent.requireString(KEY_IMAGE_URL))
    }
}