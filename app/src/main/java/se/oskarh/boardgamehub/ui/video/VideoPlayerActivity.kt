package se.oskarh.boardgamehub.ui.video

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.core.text.parseAsHtml
import androidx.databinding.DataBindingUtil
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import kotlinx.android.synthetic.main.video_player_activity.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.analytics.Analytics
import se.oskarh.boardgamehub.analytics.EVENT_YOUTUBE_CHANNEL_OPEN
import se.oskarh.boardgamehub.analytics.EVENT_YOUTUBE_SKIP_BACK
import se.oskarh.boardgamehub.analytics.EVENT_YOUTUBE_SKIP_FORWARD
import se.oskarh.boardgamehub.analytics.EVENT_YOUTUBE_VIDEO_SHARE
import se.oskarh.boardgamehub.api.model.youtube.YouTubeVideoInfo
import se.oskarh.boardgamehub.databinding.VideoPlayerActivityBinding
import se.oskarh.boardgamehub.ui.base.BaseActivity
import se.oskarh.boardgamehub.util.AppPreferences
import se.oskarh.boardgamehub.util.KEY_VIDEO_POSITION
import se.oskarh.boardgamehub.util.KEY_YOUTUBE_VIDEO
import se.oskarh.boardgamehub.util.TEXT_PLAIN
import se.oskarh.boardgamehub.util.YOUTUBE_PACKAGE
import se.oskarh.boardgamehub.util.extension.enableFullScreen
import se.oskarh.boardgamehub.util.extension.getCompatDrawable
import se.oskarh.boardgamehub.util.extension.injector
import se.oskarh.boardgamehub.util.extension.isYouTubeInstalled
import se.oskarh.boardgamehub.util.extension.isLandscapeOrientation
import se.oskarh.boardgamehub.util.extension.toIntOrZero
import se.oskarh.boardgamehub.util.extension.visibleIf
import timber.log.Timber
import java.text.NumberFormat
import kotlin.math.max
import kotlin.math.min

// TODO: Add language to the video and overview?
class VideoPlayerActivity : BaseActivity() {

    private lateinit var binding: VideoPlayerActivityBinding

    private lateinit var video: YouTubeVideoInfo

    private val tracker = YouTubePlayerTracker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isLandscapeOrientation()) {
            enableFullScreen()
        }
        binding = DataBindingUtil.setContentView(this, R.layout.video_player_activity)
        injector.inject(this)
        binding.lifecycleOwner = this
        video = intent.getParcelableExtra(KEY_YOUTUBE_VIDEO)!!
        lifecycle.addObserver(youtube_player)
        youtube_player.getPlayerUiController().run {
            showFullscreenButton(!isLandscapeOrientation())
        }
        youtube_player.initialize(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.addListener(tracker)

                val startSecond = savedInstanceState?.getFloat(KEY_VIDEO_POSITION) ?: 0f
                Timber.d("Loading video $video...")
                if (AppPreferences.shouldAutoPlayVideos) {
                    youTubePlayer.loadVideo(video.id, startSecond)
                } else {
                    youTubePlayer.cueVideo(video.id, startSecond)
                }

                youtube_player.getPlayerUiController().run {
                    setCustomAction1(getCompatDrawable(R.drawable.ic_back_10_black_24dp), View.OnClickListener {
                        Analytics.logEvent(EVENT_YOUTUBE_SKIP_BACK)
                        val newPlayTime = max(tracker.currentSecond - 10, 0f)
                        youTubePlayer.seekTo(newPlayTime)
                    })
                    setCustomAction2(getCompatDrawable(R.drawable.ic_skip_forward_black_24dp), View.OnClickListener {
                        Analytics.logEvent(EVENT_YOUTUBE_SKIP_FORWARD)
                        val newPlayTime = min(tracker.currentSecond + 30, tracker.videoDuration)
                        youTubePlayer.seekTo(newPlayTime)
                    })
                }
            }
        })
        video_playing_title?.text = video.title.parseAsHtml()
        // TODO: Support formatting by Locale
        published_date?.text = video.publishedDateFormatted()
        channel_root?.setOnClickListener {
            Analytics.logEvent(EVENT_YOUTUBE_CHANNEL_OPEN)
            openYouTubeChannel(video.channelId)
        }
        channel_name?.text = video.channelTitle
        video_views?.visibleIf { video.hasViewCount }
        video_views?.text = getString(R.string.views, NumberFormat.getInstance().format(video.viewCount.toIntOrZero()))
        description?.text = video.description.parseAsHtml()
        share_icon?.setOnClickListener {
            Analytics.logEvent(EVENT_YOUTUBE_VIDEO_SHARE)
            shareVideo(video.id)
        }
    }

    private fun shareVideo(videoId: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = TEXT_PLAIN
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_youtube_subject, video.title))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_youtube_text, toYouTubeVideoLink(videoId)))
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)))
    }

    private fun openYouTubeChannel(channelId: String) {
        val youTubeChannelIntent = Intent(Intent.ACTION_VIEW).apply {
            setPackage(YOUTUBE_PACKAGE)
            data = toYouTubeChannelLink(channelId).toUri()
        }
        if (isYouTubeInstalled()) {
            startActivity(youTubeChannelIntent)
        } else {
            startActivity(Intent(Intent.ACTION_VIEW, toYouTubeChannelLink(channelId).toUri()))
        }
    }

    private fun toYouTubeVideoLink(videoId: String) = getString(R.string.youtube_link_builder, videoId)

    private fun toYouTubeChannelLink(channelId: String) = getString(R.string.youtube_channel_link_builder, channelId)

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putFloat(KEY_VIDEO_POSITION, tracker.currentSecond)
    }
}