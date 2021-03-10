package se.oskarh.boardgamehub.api.model.boardgamegeek

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "forum")
data class GetForumResponse(

    @Element(name = "threads")
    var threads: Threads,

    @Attribute(name = "id")
    var id: Int,

    @Attribute(name = "title")
    var title: String,

    @Attribute(name = "numthreads")
    var numberOfThreads: Int,

    @Attribute(name = "numposts")
    var numberOfPosts: Int,

    @Attribute(name = "lastpostdate")
    var lastPostDate: String,

    @Attribute(name = "termsofuse")
    var termsOfUse: String)
