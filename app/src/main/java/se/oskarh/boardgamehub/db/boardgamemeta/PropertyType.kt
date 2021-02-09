package se.oskarh.boardgamehub.db.boardgamemeta

import se.oskarh.boardgamehub.analytics.EVENT_DIALOG_ARTIST
import se.oskarh.boardgamehub.analytics.EVENT_DIALOG_CATEGORY
import se.oskarh.boardgamehub.analytics.EVENT_DIALOG_DESIGNER
import se.oskarh.boardgamehub.analytics.EVENT_DIALOG_MECHANIC
import se.oskarh.boardgamehub.analytics.EVENT_DIALOG_PUBLISHER
import se.oskarh.boardgamehub.analytics.EVENT_DIALOG_TYPE
import se.oskarh.boardgamehub.util.BOARDGAME_ARTIST_URL
import se.oskarh.boardgamehub.util.BOARDGAME_CATEGORY_URL
import se.oskarh.boardgamehub.util.BOARDGAME_DESIGNER_URL
import se.oskarh.boardgamehub.util.BOARDGAME_MECHANIC_URL
import se.oskarh.boardgamehub.util.BOARDGAME_PUBLISHER_URL
import se.oskarh.boardgamehub.util.BOARDGAME_TYPE_URL

enum class PropertyType(val eventName: String) {
    ARTIST(EVENT_DIALOG_ARTIST),
    BOARD_GAME_CATEGORY(EVENT_DIALOG_CATEGORY),
    BOARD_GAME_MECHANIC(EVENT_DIALOG_MECHANIC),
    DESIGNER(EVENT_DIALOG_DESIGNER),
    PUBLISHER(EVENT_DIALOG_PUBLISHER),
    TYPE(EVENT_DIALOG_TYPE);

    fun toBoardGameGeekUrl(id: Int): String {
        return when(this) {
            ARTIST -> "$BOARDGAME_ARTIST_URL$id"
            BOARD_GAME_CATEGORY -> "$BOARDGAME_CATEGORY_URL$id"
            BOARD_GAME_MECHANIC -> "$BOARDGAME_MECHANIC_URL$id"
            DESIGNER -> "$BOARDGAME_DESIGNER_URL$id"
            PUBLISHER -> "$BOARDGAME_PUBLISHER_URL$id"
            TYPE -> "$BOARDGAME_TYPE_URL$id"
        }
    }
}