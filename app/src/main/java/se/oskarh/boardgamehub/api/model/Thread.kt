package se.oskarh.boardgamehub.api.model

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "thread")
data class Thread(

    @Attribute(name = "id")
    var id: Int,

    @Attribute(name = "subject")
    var subject: String,

    @Attribute(name = "author")
    var author: String,

    @Attribute(name = "numarticles")
    var numberOfArticles: String,

    @Attribute(name = "postdate")
    var postdate: String,

    @Attribute(name = "lastpostdate")
    var lastPostDate: String)