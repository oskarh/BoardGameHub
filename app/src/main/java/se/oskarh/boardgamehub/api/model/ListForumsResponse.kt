package se.oskarh.boardgamehub.api.model

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "forums")
data class ListForumsResponse(

    @Element(name = "forum")
    var forumListItems: List<ForumListItem>,

    @Attribute(name = "id")
    var id: Int,

    @Attribute(name = "type")
    var type: String,

    @Attribute(name = "termsofuse")
    var termsOfUse: String)
