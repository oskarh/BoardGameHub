package se.oskarh.boardgamehub.api.model.boardgamegeek

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "thread")
data class ThreadDetailsResponse(

    @Attribute(name = "id")
    var id: Int,

    @Element(name = "articles")
    var articles: Articles,

    @PropertyElement(name = "subject")
    var subject: String,

    @Attribute(name = "numarticles")
    var numberOfArticles: String,

    @Attribute(name = "link")
    var link: String,

    @Attribute(name = "termsofuse")
    var termsOfUse: String)