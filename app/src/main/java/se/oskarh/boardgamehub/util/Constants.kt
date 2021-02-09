package se.oskarh.boardgamehub.util

import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.db.boardgame.BoardGame

const val ANIMATION_DEFAULT_DURATION = 300L
const val UPDATE_MESSAGE_VISIBILITY_DURATION = 5000

const val ANIMATION_ITEM_SELECTED_SIZE = 0.97f
const val ANIMATION_ITEM_NOT_SELECTED_SIZE = 1f
const val BOARDGAME = "boardgame"
const val BOARDGAME_HUB_PRIVACY_URL = "https://boardgamehub.flycricket.io/privacy.html"
const val BOARDGAME_GEEK_URL = "https://boardgamegeek.com"
const val BOARDGAME_CATEGORY_URL = "https://boardgamegeek.com/boardgamecategory/"
const val BOARDGAME_MECHANIC_URL = "https://boardgamegeek.com/boardgamemechanic/"
const val BOARDGAME_ARTIST_URL = "https://boardgamegeek.com/boardgameartist/"
const val BOARDGAME_DESIGNER_URL = "https://boardgamegeek.com/boardgamedesigner/"
const val BOARDGAME_PUBLISHER_URL = "https://boardgamegeek.com/boardgamepublisher/"
const val BOARDGAME_TYPE_URL = "https://boardgamegeek.com/boardgamesubdomain/"
const val BOARDGAME_WEIGHT_URL = "https://boardgamegeek.com/wiki/page/Weight"

const val BOARDGAME_DETAILS_ID = "boardgame_details_id"
const val BOARDGAME_TITLE = "boardgame_title"

// TODO: Use KEY_ instead of these..?
const val PROPERTY_COLLECTION_USERNAME = "property_collection_username"
const val PROPERTY_ID = "property_id"
const val PROPERTY_LANGUAGE_DEPENDENCY = "property_language_dependency"
const val PROPERTY_RANKINGS = "property_rankings"
const val PROPERTY_RECOMMENDED_AGE = "property_recommended_age"
const val PROPERTY_RECOMMENDED_PLAYERS = "property_recommended_players"
const val PROPERTY_STATISTICS = "property_statistics"
const val PROPERTY_TITLE = "property_title"
const val PROPERTY_TYPE = "property_type"
const val PROPERTY_USERS_RATED = "property_users_rated"

const val NUMBER_OF_COMMENTS = "number_of_comments"
const val BOARDGAME_EXPANSION = "boardgameexpansion"
const val BOARDGAMEGEEK_BASE_URL = "http://www.boardgamegeek.com/"
const val BOARDGAMES_AND_EXPANSIONS = "$BOARDGAME,$BOARDGAME_EXPANSION"
const val ARTIST = "xmlapi/boardgameartist/{artistId}"
const val DESIGNER = "xmlapi/boardgamedesigner/{designerId}"
const val DEFAULT_FILTER_START_YEAR = 1960
const val DEFAULT_FILTER_END_YEAR = 2023
const val DEFAULT_COLLECTION_FILTER_NUMBER_PLAYERS = 2
const val DO_NOT_INCLUDE_DATA = "0"
const val EXACT = "exact"
const val HOT = "xmlapi2/hot"
const val ID = "id"
const val USERNAME = "username"
const val VERSION = "version"
const val STATS = "stats"
const val BRIEF = "brief"
const val DOMAIN = "domain"
const val SUBTYPE = "subtype"
const val INCLUDE_DATA = "1"
const val MAX_VISIBLE_SUGGESTIONS = 6
const val MAX_VISIBLE_FEED_GAMES = 5
const val MAX_VISIBLE_REDDIT_POSTS = 5
const val MAX_VISIBLE_FEED_VIDEOS = 5
const val SEARCH_DEBOUNCE_MILLISECONDS = 1000L
const val MAX_NUMBER_COMMENTS = 100
const val MATCH_ALL = "0"
const val MINIMUM_QUERY_LENGTH = 4
const val PAGE_SIZE = 100
const val PUBLISHER = "xmlapi/boardgamepublisher/{publisherId}"
const val QUERY = "query"
const val SEARCH = "xmlapi2/search"
const val THING = "xmlapi2/thing"
const val IMAGE_BACKGROUND_ANIMATION_DURATION = 300L
const val FAMILY = "xmlapi2/family"
const val COLLECTION = "xmlapi2/collection"
const val LIST_FORUMS = "xmlapi2/forumlist"
const val GET_FORUM = "xmlapi2/forum"
const val THREAD = "xmlapi2/thread"

const val PRIMARY = "primary"
const val TYPE = "type"
const val VIDEOS = "videos"
const val STATISTICS = "stats"
const val COMMENTS = "comments"
const val ARTIST_ID = "artistId"
const val DESIGNER_ID = "designerId"
const val PREFETCH_BUFFER_SIZE = 3
const val PREFETCH_PERIOD_MS = 1000L

const val PUBLISHER_ID = "publisherId"
const val YOUTUBE_PACKAGE_NAME = "com.google.android.youtube"
const val YOUTUBE_BASE_URL = "https://www.googleapis.com/"
const val REDDIT_BASE_URL = "https://www.reddit.com"
const val REDDIT_BOARDGAMES_SUBREDDIT = "https://www.reddit.com/r/boardgames/"
const val YOUTUBE_FIELDS = "fields"
const val YOUTUBE_ID = "id"
const val YOUTUBE_CHANNEL_ID = "channelId"
const val YOUTUBE_ORDER = "order"
const val YOUTUBE_DEFAULT_ORDER = "date"
const val YOUTUBE_DEFAULT_MAX_RESULTS = 20
const val YOUTUBE_KEY = "key"
const val LIMIT = "limit"
const val YOUTUBE_MAX_RESULTS = "maxResults"
const val YOUTUBE_TYPE = "type"
const val YOUTUBE_PART = "part"
const val YOUTUBE_VIDEO_INFO = "youtube/v3/videos"
const val SUBREDDIT_LIST = "/r/boardgames/hot.json"
const val YOUTUBE_SEARCH_CHANNEL = "youtube/v3/search"
const val YOUTUBE_DEFAULT_PART = "snippet,statistics,contentDetails"
const val YOUTUBE_SEARCH_PART = "snippet,id"
const val YOUTUBE_PACKAGE = "com.google.android.youtube"
const val YOUTUBE_FORMAT_VIEWS_TO_K_THRESHOLD = 3000
const val YOUTUBE_CHANNEL_VIDEOS_FIELDS =
    "items(id(videoId),snippet(publishedAt,title,description,thumbnails(high(url,width,height)),channelTitle,channelId))"
//    "nextPageToken,pageInfo,items(id,snippet)"
//    "pageInfo(totalResults,resultsPerPage),items(id(videoId),snippet(publishedAt,title,description,thumbnails(high(url,width,height)),channelTitle,channelId))"
const val YOUTUBE_VIDEO_DETAILS_FIELDS =
    "items(id,snippet(title,description,publishedAt,channelTitle,channelId,defaultAudioLanguage,thumbnails(high)),statistics(viewCount),contentDetails(duration))"
// TODO: Remove this
const val YOUTUBE_DEFAULT_KEY = "INSERT_YOUTUBE_API_KEY"
const val YOUTUBE_DEFAULT_TYPE = "video"
const val YOUTUBE_AUTHORITY = "www.youtube.com"
const val RANK_FORMAT_PATTERN = "0.0"
const val DETAILED_RANK_FORMAT_PATTERN = "0.00"
const val BOTTOM_SHEET_DEFAULT_PEEK_HEIGHT = 30
const val KEY_BOARDGAME_LIST = "boardgame_list"
const val KEY_IS_RANKED_LIST = "is_ranked_list"
const val KEY_PARENT_CONTAINER = "parent_container"
const val KEY_BOARDGAME_FAMILY_ID = "boardgame_family_id"
const val KEY_VIDEO_LIST = "video_list"
const val KEY_TITLE = "title"
const val KEY_DETAILS_SOURCE = "source"
const val KEY_IMAGE_URL = "image_url"
const val KEY_DESCRIPTION = "description"
const val KEY_DETAILS_TITLE = "details_title"
const val KEY_DETAILS_ID = "details_id"
const val KEY_SCREEN = "screen"
const val KEY_YOUTUBE_VIDEO = "youtube_video"
const val KEY_VIDEO_POSITION = "video_position"
const val TEXT_PLAIN = "text/plain"
const val GOOGLE_MAP_BOARDGAME_CAFES = "https://www.google.com/maps/d/viewer?mid=1FJC26z4rQAjLStvDI41kWYj0FUU&ll=32.29854645985803%2C65.75566814947112&z=2"

const val INDEX_NOT_FOUND = -1
const val COULD_NOT_PARSE_DEFAULT = "-"

const val BOARDGAME_MAXIMUM_PAGE_SIZE = 100
const val TOP_GAMES_TIMEOUT = 10_000
const val CACHE_BOARDGAME_LIFETIME = 1000 * 60 * 60 * 24 * 30L

const val ONE_DAY_MS = 1000 * 60 * 60 * 24
const val CACHE_NEW_BOARDGAMES = ONE_DAY_MS * 2L
const val CACHE_AGING_BOARDGAMES = ONE_DAY_MS * 10L
const val CACHE_OLD_BOARDGAMES = ONE_DAY_MS * 30L

const val IMPORT_COLLECTION_PERIOD_MS = 1000L
const val PROMPT_REVIEW_PERIOD = 7
const val MINIMUM_REQUIRED_APP_VERSION = "min_supported_app_version"
const val MINIMUM_REQUIRED_VOTES = 10
const val MAXIMUM_VISIBLE_EXPANSIONS = 8

val RECOMMENDATION_COLORS = intArrayOf(R.color.best, R.color.recommended, R.color.not_recommended)

//2019-01-10T03:21:06.000Z

//https://www.googleapis.com/youtube/v3/videos?id=7lCDEYXw3mM&key=YOUR_API_KEY
//&fields=items(id,snippet(channelId,title,categoryId),statistics)&part=snippet,statistics

// TODO: Move this elsewhere
val ignoredCharacters = "[:;'`´]".toRegex()

val nonDigitsRegex = Regex("[^0-9]")

val UNCATEGORIZED_BOARDGAME = listOf(BoardGame.Rank("", -1, "Uncategorized", "Uncategorized", "Uncategorized", "0"))

const val DEFAULT_VOTED_BY_PLAYER_COUNT = 4
const val MINIMUM_VOTED_BY_PLAYER_COUNT = 1
const val MAXIMUM_VOTED_BY_PLAYER_COUNT = 10
