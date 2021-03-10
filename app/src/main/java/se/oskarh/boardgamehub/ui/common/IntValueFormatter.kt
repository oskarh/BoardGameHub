package se.oskarh.boardgamehub.ui.common

import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.roundToInt

class IntValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return value.roundToInt().toString()
    }
}