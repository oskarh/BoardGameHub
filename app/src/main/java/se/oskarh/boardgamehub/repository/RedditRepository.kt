package se.oskarh.boardgamehub.repository

import androidx.lifecycle.LiveData
import dagger.Reusable
import se.oskarh.boardgamehub.api.model.reddit.SubredditResponse
import javax.inject.Inject

@Reusable
class RedditRepository @Inject constructor(
    private val redditService: RedditService) {

    fun getSubredditPosts(limit: Int): LiveData<ApiResponse<SubredditResponse>> =
            redditService.getSubredditPosts(limit)
}