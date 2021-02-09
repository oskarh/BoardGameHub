package se.oskarh.boardgamehub.api

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import se.oskarh.boardgamehub.R

enum class GameType(val property: String, @StringRes val title: Int, @DrawableRes val icon: Int) {
    BOARDGAME("boardgame", R.string.board_game_standalone_title, R.drawable.ic_standalone_white_24dp),
    BOARDGAME_ACCESSORY("boardgameaccessory", R.string.board_game_accessory_title, R.drawable.ic_standalone_white_24dp),
    BOARDGAME_EXPANSION("boardgameexpansion", R.string.board_game_expansion_title, R.drawable.ic_expansion_white_24dp),
    RPG_ISSUE("rpgissue", R.string.rpg_issue_title, R.drawable.ic_standalone_white_24dp),
    RPG_ITEM("rpgitem", R.string.rpg_item_title, R.drawable.ic_standalone_white_24dp),
    UNKNOWN("unknown", R.string.unknown_title, R.drawable.ic_standalone_white_24dp);

    companion object {
        fun from(input: String) = values().firstOrNull { it.property.equals(input, true) } ?: UNKNOWN
    }
}