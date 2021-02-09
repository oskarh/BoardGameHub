package se.oskarh.boardgamehub.util

import se.oskarh.boardgamehub.util.extension.userPrimaryLocale
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

object RankFormatter : DecimalFormat(RANK_FORMAT_PATTERN, DecimalFormatSymbols(userPrimaryLocale)) {
    init {
        roundingMode = RoundingMode.HALF_UP
    }
}