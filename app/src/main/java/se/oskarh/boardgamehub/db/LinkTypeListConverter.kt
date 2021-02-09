package se.oskarh.boardgamehub.db

import androidx.room.TypeConverter
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.util.extension.deserialize
import se.oskarh.boardgamehub.util.extension.serialize

class LinkTypeListConverter {

    @TypeConverter
    fun linkTypeListToJson(linkTypes: List<BoardGame.Link>): String =
        serialize(linkTypes)

    @TypeConverter
    fun jsonToLinkTypeList(jsonLinkTypes: String): List<BoardGame.Link> =
        deserialize(jsonLinkTypes)
}
