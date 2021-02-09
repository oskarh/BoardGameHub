package se.oskarh.boardgamehub.api.model

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "companies")
data class PublisherResponse(

    @Element(name = "company")
    var company: Company,

    @Attribute(name = "termsofuse")
    var termsOfUse: String)