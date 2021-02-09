package se.oskarh.boardgamehub.api

import androidx.annotation.StringRes
import se.oskarh.boardgamehub.R

enum class RatingSource(@StringRes val description: Int) {
    USER_AVERAGE_0_VOTES(R.string.user_average_0_votes),
    USER_AVERAGE_10_VOTES(R.string.user_average_10_votes),
    USER_AVERAGE_100_VOTES(R.string.user_average_100_votes),
    GEEK_RATING(R.string.geek_rating)
}
