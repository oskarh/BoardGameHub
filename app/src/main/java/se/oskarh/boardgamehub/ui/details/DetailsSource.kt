package se.oskarh.boardgamehub.ui.details

enum class DetailsSource(val eventName: String) {
    DETAILS("details_source_details"),
    FAMILY("details_source_family"),
    FAVORITE("details_source_favorite"),
    HOT("details_source_hot"),
    HOT_LIST("details_source_hot_list"),
    LIST("details_source_list"),
    RECENT_BOARDGAME("details_source_recent"),
    SEARCH("details_source_search"),
    TOP("details_source_top"),
    TOP_LIST("details_source_top_list"),
    UNKNOWN_LIST("details_source_list_unknown")
}