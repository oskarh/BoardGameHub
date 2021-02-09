package se.oskarh.boardgamehub.ui.common

import android.content.Context
import com.google.android.material.chip.Chip

// TODO: Create this in XML
class DefaultChip(context: Context, label: String) : Chip(context) {

    constructor(context: Context) : this(context, "")

    init {
        text = label
        isClickable = true
    }
}