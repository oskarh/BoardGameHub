package se.oskarh.boardgamehub.api.model.reddit

import androidx.core.net.toUri
import se.oskarh.boardgamehub.util.REDDIT_BASE_URL

data class DataX(
    val author: String,
    val author_flair_text: Any,
    val author_fullname: String,
    val post_hint: String?,
//    val created: Double,
    val created_utc: Double,
    val domain: String,
//    val downs: Int,
//    val gilded: Int,
//    val hidden: Boolean,
    val id: String,
    val is_original_content: Boolean,
    val is_self: Boolean,
    val is_video: Boolean,
    val link_flair_background_color: String?,
    val link_flair_text: String?,
    val name: String?,
    val num_comments: Int,
    val permalink: String,
    val pinned: Boolean,
    val score: Int,
    val selftext: String,
//    val send_replies: Boolean,
    val spoiler: Boolean,
    val stickied: Boolean,
    val thumbnail: String,
    val title: String,
//    val total_awards_received: Int,
    val ups: Int,
    val url: String) {

    fun postLink() = "${REDDIT_BASE_URL}${permalink}"

    fun hasExternalLink() = post_hint.equals("link", true) && url.toUri().host != null && !url.toUri().host!!.contains("reddit.com", true)
}