package se.oskarh.boardgamehub.ui.feed

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.reddit_post_item.view.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.analytics.Analytics
import se.oskarh.boardgamehub.analytics.EVENT_PROPERTY_REDDIT_LINK
import se.oskarh.boardgamehub.analytics.EVENT_REDDIT_LINK_OPEN
import se.oskarh.boardgamehub.analytics.USER_PROPERTY_REDDIT_LINK_COUNT
import se.oskarh.boardgamehub.api.model.reddit.DataX
import se.oskarh.boardgamehub.util.AppPreferences
import se.oskarh.boardgamehub.util.extension.getCompatColor
import se.oskarh.boardgamehub.util.extension.inflate
import se.oskarh.boardgamehub.util.extension.visibleIf
import java.util.Calendar
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class RedditAdapter(private val postClicked: (DataX) -> Unit) : ListAdapter<DataX, RedditAdapter.ViewHolder>(RedditPostDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.reddit_post_item))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(post: DataX) {
            val postColor = if (AppPreferences.visitedRedditPosts.contains(post.id)) {
                itemView.context.getCompatColor(R.color.secondaryTextColor)
            } else {
                itemView.context.getCompatColor(R.color.primaryTextColor)
            }
            val linkColor = if (AppPreferences.visitedRedditLinks.contains(post.url)) {
                itemView.context.getCompatColor(R.color.secondaryTextColor)
            } else {
                itemView.context.getCompatColor(R.color.primaryTextColor)
            }
            itemView.post_title.text = post.title.parseAsHtml()
            itemView.post_title.setTextColor(postColor)
            itemView.post_link.visibleIf { post.hasExternalLink() }
            ImageViewCompat.setImageTintList(itemView.post_link, ColorStateList.valueOf(linkColor))
            itemView.post_link.setOnClickListener {
                Analytics.logEvent(EVENT_REDDIT_LINK_OPEN, EVENT_PROPERTY_REDDIT_LINK to post.url)
                Analytics.setUserProperty(USER_PROPERTY_REDDIT_LINK_COUNT, AppPreferences.visitedRedditLinks.size + 1)
                AppPreferences.visitedRedditLinks.add(post.url)
                itemView.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(post.url)))
            }
            itemView.post_flair.visibleIf { !post.link_flair_text.isNullOrBlank() }
            itemView.post_flair.text = post.link_flair_text?.parseAsHtml()
            itemView.post_flair.setTextColor(postColor)
            itemView.upvotes.text = post.ups.toString()
            itemView.comments.text = post.num_comments.toString()
            itemView.post_info.text = "${getElapsedTimeString(post.created_utc.toLong())} • ${post.author}"
            itemView.post_info.setTextColor(postColor)
            itemView.rootView.setOnClickListener {
                postClicked(post)
            }
        }
    }

    private fun getElapsedTimeString(postUtcTime: Long): String {
        val currentUtcTime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis / 1000
        val elapsedSeconds = currentUtcTime - postUtcTime
        return when {
            TimeUnit.SECONDS.toDays(elapsedSeconds) > 0 -> "${TimeUnit.SECONDS.toDays(elapsedSeconds)}d"
            TimeUnit.SECONDS.toHours(elapsedSeconds) > 0 -> "${TimeUnit.SECONDS.toHours(elapsedSeconds)}h"
            else -> "${TimeUnit.SECONDS.toMinutes(elapsedSeconds)}m"
        }
    }
}