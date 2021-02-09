package se.oskarh.boardgamehub.api.model

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml
import se.oskarh.boardgamehub.api.LinkType
import se.oskarh.boardgamehub.db.boardgame.BoardGame

@Xml(name = "link")
data class BggLink(
    @Attribute(name = "id")
    var id: Int,

    @Attribute(name = "inbound")
    var inbound: String?,

    @Attribute(name = "type")
    var type: String,

    @Attribute(name = "value")
    var text: String) {

    fun toLink() = LinkType.toLinkTypeOrNull(type)?.let { linkType ->
        BoardGame.Link(linkType, id, text)
    }

    fun isInboundLink() = inbound.equals("true", true)
}