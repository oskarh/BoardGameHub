package se.oskarh.boardgamehub.api.model.boardgamegeek

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "person")
data class Person(

    @PropertyElement(name = "name")
    var name: String,

    @PropertyElement(name = "description")
    var description: String)
