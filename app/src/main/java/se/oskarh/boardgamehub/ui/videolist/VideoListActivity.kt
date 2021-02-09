package se.oskarh.boardgamehub.ui.videolist

import android.os.Bundle
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.api.model.youtube.YouTubeVideoInfo
import se.oskarh.boardgamehub.ui.base.BaseActivity
import se.oskarh.boardgamehub.util.KEY_TITLE
import se.oskarh.boardgamehub.util.KEY_VIDEO_LIST
import java.util.ArrayList

class VideoListActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = intent.getStringExtra(KEY_TITLE)
        val videos = intent.getParcelableArrayListExtra<YouTubeVideoInfo>(KEY_VIDEO_LIST) as ArrayList<YouTubeVideoInfo>
        val listFragment = VideoListFragment.newInstance(videos)
        supportFragmentManager.beginTransaction()
            .add(R.id.video_list_content, listFragment, listFragment.javaClass.simpleName)
            .commit()
    }
}
