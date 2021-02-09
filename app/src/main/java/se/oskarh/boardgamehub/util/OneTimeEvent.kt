package se.oskarh.boardgamehub.util

import java.util.concurrent.atomic.AtomicBoolean

// TODO: Merge with Event
class OneTimeEvent {

    private val isPending = AtomicBoolean(true)

    fun hasUpdate(): Boolean = isPending.compareAndSet(true, false)
}