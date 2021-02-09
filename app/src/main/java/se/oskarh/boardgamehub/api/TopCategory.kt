package se.oskarh.boardgamehub.api

import androidx.annotation.StringRes
import se.oskarh.boardgamehub.R

enum class TopCategory(@StringRes val categoryName: Int, val url: String) {
    ALL(R.string.all, "https://www.boardgamegeek.com/browse/boardgame"),
    WARGAMES(R.string.wargames, "https://boardgamegeek.com/wargames/browse/boardgame"),
    STRATEGY(R.string.strategy, "https://boardgamegeek.com/strategygames/browse/boardgame"),
    FAMILY(R.string.family, "https://boardgamegeek.com/familygames/browse/boardgame"),
    CUSTOMIZABLE(R.string.customizable, "https://boardgamegeek.com/cgs/browse/boardgame"),
    ABSTRACTS(R.string.abstracts, "https://boardgamegeek.com/abstracts/browse/boardgame"),
    CHILDREN(R.string.children, "https://boardgamegeek.com/childrensgames/browse/boardgame"),
    PARTY(R.string.party, "https://boardgamegeek.com/partygames/browse/boardgame"),
    THEMATIC(R.string.thematic, "https://boardgamegeek.com/thematic/browse/boardgame");
}
