package se.oskarh.boardgamehub.api.model

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "company")
data class Company(

    @PropertyElement(name = "name")
    var name: String,

    @PropertyElement(name = "description")
    var description: String)