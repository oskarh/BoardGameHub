package se.oskarh.boardgamehub.repository

import androidx.lifecycle.LiveData
import retrofit2.http.GET
import retrofit2.http.Query
import se.oskarh.boardgamehub.api.model.reddit.SubredditResponse
import se.oskarh.boardgamehub.util.LIMIT
import se.oskarh.boardgamehub.util.SUBREDDIT_LIST

interface RedditService {

    @GET(SUBREDDIT_LIST)
    fun getSubredditPosts(@Query(LIMIT) limit: Int): LiveData<ApiResponse<SubredditResponse>>
}
