package se.oskarh.boardgamehub.util

import android.view.MotionEvent
import android.view.View
import se.oskarh.boardgamehub.util.extension.animateToSize
import timber.log.Timber

// TODO: Deprecated in favor of ChangeSizeDetector, refactor this out and test every place I change it
// TODO: Fix comments properly. Remove this
object ChangeSizeAnimation : View.OnTouchListener {
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Timber.d("ACTION DOWN")
                view.animateToSize(ANIMATION_ITEM_SELECTED_SIZE)
            }
            MotionEvent.ACTION_CANCEL -> {
                Timber.d("ACTION CANCEL")
                view.animateToSize(ANIMATION_ITEM_NOT_SELECTED_SIZE)
            }
            MotionEvent.ACTION_UP -> run {
                Timber.d("ACTION UP")
                view.animateToSize(ANIMATION_ITEM_NOT_SELECTED_SIZE)
                view.performClick()
                // TODO: Potentially problematic with long press..?
                return true
            }
        }
        return false
    }
}