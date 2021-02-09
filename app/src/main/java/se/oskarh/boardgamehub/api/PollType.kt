package se.oskarh.boardgamehub.api

enum class PollType(val property: String) {
    LANGUAGE_DEPENDENCE("language_dependence"),
    SUGGESTED_NUMBER_OF_PLAYERS("suggested_numplayers"),
    SUGGESTED_PLAYER_AGE("suggested_playerage");

    companion object {
        fun toPollTypeOrNull(name: String) =
            values().firstOrNull { it.property.equals(name.trim(), true) }
    }
}