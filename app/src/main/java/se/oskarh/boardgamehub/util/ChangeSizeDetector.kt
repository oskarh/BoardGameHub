package se.oskarh.boardgamehub.util

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import se.oskarh.boardgamehub.util.extension.animateToSize

class ChangeSizeDetector(private val myView: View, private val onLongClicked: () -> Unit) : GestureDetector.SimpleOnGestureListener() {
    override fun onDown(e: MotionEvent?): Boolean {
        myView.animateToSize(ANIMATION_ITEM_SELECTED_SIZE)
        return true
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        myView.performClick()
        myView.animateToSize(ANIMATION_ITEM_NOT_SELECTED_SIZE)
        return true
    }

    override fun onLongPress(e: MotionEvent?) {
        onLongClicked()
        myView.animateToSize(ANIMATION_ITEM_NOT_SELECTED_SIZE)
    }
}