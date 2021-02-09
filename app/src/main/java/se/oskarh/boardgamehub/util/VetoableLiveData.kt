package se.oskarh.boardgamehub.util

import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import timber.log.Timber

class VetoableLiveData : MutableLiveData<String>() {

    init {
        super.setValue(value.orEmpty())
    }

    @MainThread
    override fun setValue(newValue: String) {
        Timber.d("VetoableLiveData set new value $value $newValue was vetoed: ${isUpdateAllowed(value, newValue)}")
        if (isUpdateAllowed(value, newValue)) {
            super.setValue(newValue)
        }
    }

    override fun postValue(newValue: String) {
        Timber.d("VetoableLiveData post new value $value $newValue was vetoed: ${isUpdateAllowed(value, newValue)}")
        if (isUpdateAllowed(value, newValue)) {
            super.postValue(value)
        }
    }

    private fun isUpdateAllowed(oldValue: String?, newValue: String): Boolean {
        return when {
            oldValue.isNullOrBlank() && newValue.isBlank() -> false
            else -> !oldValue?.trim().equals(newValue.trim(), true)
        }
    }
}