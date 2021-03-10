package se.oskarh.boardgamehub.api.model.boardgamegeek

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "threads")
data class Threads(

    @Element(name = "thread")
    var threads: List<Thread>)