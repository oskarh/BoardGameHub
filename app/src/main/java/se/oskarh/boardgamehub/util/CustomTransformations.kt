package se.oskarh.boardgamehub.util

import androidx.lifecycle.LiveData

object CustomTransformations {

    fun <X> debounce(source: LiveData<X>, timeout: Long): LiveData<X> = DebounceLiveData(source, timeout)
}