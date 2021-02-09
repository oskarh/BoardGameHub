package se.oskarh.boardgamehub.ui.common

import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialog

open class HideableBottomSheetDialog(context: Context) : BottomSheetDialog(context) {

    var hideable: Boolean = true
        get() = true
        set(value) {
            behavior.isHideable = value
            setCancelable(value)
            field = value
        }
}