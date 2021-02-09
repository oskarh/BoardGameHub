package se.oskarh.boardgamehub.api.model

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "people")
data class PeopleResponse(

    @Element(name = "person")
    var person: Person,

    @Attribute(name = "termsofuse")
    var termsOfUse: String)
