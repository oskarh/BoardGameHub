package se.oskarh.boardgamehub.ui.details

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.search_header.view.*
import kotlinx.android.synthetic.main.video_item.view.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.api.model.youtube.YouTubeVideo
import se.oskarh.boardgamehub.util.extension.inflate
import se.oskarh.boardgamehub.util.extension.loadImage
import se.oskarh.boardgamehub.util.extension.toIntOrZero
import timber.log.Timber

class VideoAdapter(
    private var videos: List<YouTubeVideo> = emptyList(),
    private val videoClicked: (YouTubeVideo) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val headerViewType = 0
    private val itemViewType = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            headerViewType -> HeaderHolder(parent.inflate(R.layout.search_header))
            itemViewType -> ViewHolder(parent.inflate(R.layout.video_item))
            else -> throw IllegalStateException("Unknown view type [$viewType]")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        when (holder) {
            is HeaderHolder -> holder.bind()
            is ViewHolder -> holder.bind(videos[position - 1])
            else -> throw IllegalStateException("Unknown holder")
        }

    override fun getItemViewType(position: Int) =
        when (position) {
            0 -> headerViewType
            else -> itemViewType
        }

    override fun getItemCount() = videos.size + 1

    fun updateResults(newVideos: List<YouTubeVideo>) {
        Timber.d("Updating to ${newVideos.size} [${newVideos.joinToString { it.snippet.title }}]")
        videos = newVideos.sortedByDescending { it.statistics.viewCount.toIntOrZero() }
        notifyDataSetChanged()
    }

    inner class HeaderHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            itemView.search_header.text = itemView.resources.getQuantityString(R.plurals.videos, videos.size, videos.size)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(video: YouTubeVideo) {
            Timber.d("Setting video title to ${video.snippet.title}")
            itemView.video_title.text = video.snippet.title
            itemView.channel_name.text = video.snippet.channelTitle
            itemView.total_views.text = itemView.context.getString(R.string.views, video.viewsFormatted())
//            itemView.video_info.text = itemView.context.getString(
//                R.string.youtube_list,
//                video.snippet.channelTitle,
//                video.statistics.viewCount,
//                video.publishedAtFormatted()
//            )
            itemView.video_image.loadImage(video.snippet.thumbnails.high.url)
            itemView.duration_text.text = video.durationFormatted()

            itemView.rootView.setOnClickListener {
                videoClicked(video)
            }
        }
    }
}