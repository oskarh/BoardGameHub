package se.oskarh.boardgamehub.api.model

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "forum")
data class ForumListItem(

    @Attribute(name = "id")
    var id: Int,

    @Attribute(name = "groupId")
    var groupId: Int,

    @Attribute(name = "title")
    var title: String,

    @Attribute(name = "noposting")
    var noPosting: Int,

    @Attribute(name = "description")
    var description: String,

    @Attribute(name = "numthreads")
    var numberOfThreads: Int,

    @Attribute(name = "numposts")
    var numberOfPosts: Int,

    @Attribute(name = "lastpostdate")
    var lastPostDate: String)