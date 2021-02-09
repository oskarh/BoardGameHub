package se.oskarh.boardgamehub.ui.common

import com.github.mikephil.charting.formatter.ValueFormatter

class IntValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "${value.toInt()}"
    }
}