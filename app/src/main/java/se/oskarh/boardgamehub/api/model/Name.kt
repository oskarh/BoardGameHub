package se.oskarh.boardgamehub.api.model

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml
import se.oskarh.boardgamehub.db.boardgame.BoardGame

@Xml(name = "name")
data class Name(
    @Attribute(name = "sortindex")
    var sortIndex: Int,

    @Attribute(name = "type")
    var type: String,

    @Attribute(name = "value")
    var value: String) {

    fun toName() = BoardGame.Name(type, value)
}
