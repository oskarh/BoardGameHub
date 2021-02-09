package se.oskarh.boardgamehub.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import se.oskarh.boardgamehub.util.PREFETCH_PERIOD_MS
import java.util.Timer
import java.util.TimerTask

class PrefetchTimer : Timer() {

    private val _timerLiveData = MutableLiveData<Long>()

    val liveData: LiveData<Long> = _timerLiveData

    private var timerTask = newTimerTask()

    init {
        scheduleAtFixedRate(timerTask, 0, PREFETCH_PERIOD_MS)
    }

    fun reset() {
        timerTask.cancel()
        timerTask = newTimerTask()
        scheduleAtFixedRate(timerTask, 0, PREFETCH_PERIOD_MS)
    }

    private fun newTimerTask() = object : TimerTask() {
        override fun run() {
            _timerLiveData.postValue(System.currentTimeMillis())
        }
    }
}