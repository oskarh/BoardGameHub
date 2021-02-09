package se.oskarh.boardgamehub.api.model

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import se.oskarh.boardgamehub.api.GameType
import se.oskarh.boardgamehub.db.boardgame.BoardGame

@Xml(name = "item")
data class BggBoardGameFamily(

    @Attribute(name = "id")
    var id: Long,

    @Attribute(name = "type")
    var type: String,

    @Element(name = "name")
    var names: List<Name>,

    @PropertyElement(name = "description")
    var description: String,

    @PropertyElement(name = "thumbnail")
    var thumbnail: String?,

    @PropertyElement(name = "image")
    var image: String?,

    @Element(name = "link")
    var links: List<BggLink>) {

    fun boardGames() =
            links.map { link ->
                BoardGame(link.id, link.text, GameType.from(link.type), 0, true)
            }
}