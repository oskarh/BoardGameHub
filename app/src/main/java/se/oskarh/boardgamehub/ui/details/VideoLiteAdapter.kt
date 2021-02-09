package se.oskarh.boardgamehub.ui.details

import android.view.View
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.video_item.view.channel_name
import kotlinx.android.synthetic.main.video_item.view.video_image
import kotlinx.android.synthetic.main.video_item.view.video_title
import kotlinx.android.synthetic.main.video_lite_item.view.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.api.model.youtube.YouTubeVideoInfo
import se.oskarh.boardgamehub.util.extension.inflate
import se.oskarh.boardgamehub.util.extension.loadImage
import timber.log.Timber
import kotlin.math.min

class VideoLiteAdapter(
    private val maxVisibleItems: Int = Int.MAX_VALUE,
    private val videoClicked: (YouTubeVideoInfo) -> Unit
) : ListAdapter<YouTubeVideoInfo, VideoLiteAdapter.ViewHolder>(VideoLiteDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.video_lite_item))
    }

    override fun getItemCount() = min(super.getItemCount(), maxVisibleItems)

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(video: YouTubeVideoInfo) {
            Timber.d("Setting video title to ${video.title}")
            itemView.video_title.text = video.title.parseAsHtml()
            itemView.channel_name.text = video.channelTitle
            itemView.description.text = video.description.parseAsHtml()
            itemView.video_image.loadImage(video.imageUrl)

            itemView.rootView.setOnClickListener {
                videoClicked(video)
            }
        }
    }
}