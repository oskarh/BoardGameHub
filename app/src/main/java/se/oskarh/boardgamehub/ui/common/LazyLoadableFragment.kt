package se.oskarh.boardgamehub.ui.common

import androidx.fragment.app.Fragment
import java.util.concurrent.atomic.AtomicBoolean

abstract class LazyLoadableFragment : Fragment() {

    private var hasLoaded = AtomicBoolean(false)

    protected open fun onLoad() = Unit

    fun load() {
        if (hasLoaded.compareAndSet(false, true)) {
            onLoad()
        }
    }
}