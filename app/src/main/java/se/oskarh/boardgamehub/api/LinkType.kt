package se.oskarh.boardgamehub.api

enum class LinkType(val property: String) {
    ARTIST("boardgameartist"),
    CATEGORY("boardgamecategory"),
    COMPILATION("boardgamecompilation"),
    DESIGNER("boardgamedesigner"),
    EXPANSION("boardgameexpansion"),
    FAMILY("boardgamefamily"),
    IMPLEMENTATION("boardgameimplementation"),
    INTEGRATION("boardgameintegration"),
    MECHANIC("boardgamemechanic"),
    PUBLISHER("boardgamepublisher");

    companion object {
        fun toLinkTypeOrNull(name: String) =
            values().firstOrNull { it.property.equals(name.trim(), true) }
    }
}
