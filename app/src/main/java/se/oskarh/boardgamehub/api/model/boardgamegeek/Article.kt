package se.oskarh.boardgamehub.api.model.boardgamegeek

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "article")
data class Article(

    @Attribute(name = "id")
    var id: Int,

    @Attribute(name = "username")
    var username: String,

    @PropertyElement(name = "subject")
    var subject: String,

    // TODO: Parsing body fails for 899771 and 2207165 but works with 1515544. Because of CDATA?
    @PropertyElement(name = "body")
    var body: String,

    @Attribute(name = "link")
    var link: String,

    @Attribute(name = "postdate")
    var postDate: String,

    @Attribute(name = "editdate")
    var editDate: String,

    @Attribute(name = "numedits")
    var numedits: Int)
