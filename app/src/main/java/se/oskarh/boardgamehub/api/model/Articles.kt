package se.oskarh.boardgamehub.api.model

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "articles")
data class Articles(

    @Element(name = "article")
    var articles: List<Article>)