package se.oskarh.boardgamehub.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import timber.log.Timber
import java.util.Timer
import java.util.TimerTask

class DebounceLiveData<T>(source: LiveData<T>, private val timeout: Long) : MediatorLiveData<T>() {

    init {
        super.addSource(source) { newValue ->
            setPendingValue(newValue)
        }
    }

    private var timer = Timer()

    private var pendingValue: T? = null

    private fun setPendingValue(newValue: T?) {
        Timber.d("Debounce: Restarting timer. Needing to be changed: ${newValue != pendingValue} [$newValue] [$pendingValue]")
        pendingValue = newValue
        timer.cancel()
        timer = scheduleTimer()
    }

    private fun scheduleTimer() =
        Timer().apply {
            schedule(object : TimerTask() {
                override fun run() {
                    Timber.d("Debounce: Trying to post value $pendingValue")
                    updateValue()
                }
            }, timeout)
        }

    private fun updateValue() {
        Timber.d("Debounce: Sending New query is $pendingValue")
        super.postValue(pendingValue)
    }
}