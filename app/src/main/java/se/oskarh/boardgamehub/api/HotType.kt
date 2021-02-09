package se.oskarh.boardgamehub.api

enum class HotType(val property: String) {
    BOARDGAME("boardgame"),
    BOARDGAME_COMPANY("boardgamecompany"),
    BOARDGAME_PERSON("boardgameperson"),
    RPG("rpg"),
    RPG_COMPANY("rpgcompany"),
    RPG_PERSON("rpgperson"),
    VIDEOGAME("videogame"),
    VIDEOGAME_COMPANY("videogamecompany")
}
