package com.example.demo.common.receiver

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.demo.common.receiver.event.LocalLifecycleEvent


class LocalEventLifecycleViewModel(application: Application) : AndroidViewModel(application) {

    private val mEventLiveData by lazy {
        MutableLiveData<LocalLifecycleEvent>()
    }

    @Suppress("UNCHECKED_CAST")
    fun register(
        lifecycleOwner: LifecycleOwner,
        onLocalEventCallback: OnLocalEventCallback<LocalLifecycleEvent>
    ) {
        this.mEventLiveData.observe(lifecycleOwner, Observer {
            onLocalEventCallback.onEventCallback(it)
        })
    }

    fun unRegister(lifecycleOwner: LifecycleOwner) {
        this.mEventLiveData.removeObservers(lifecycleOwner)
    }

    fun <Event : LocalLifecycleEvent> postLifecycleEvent(event: Event) {
        this.mEventLiveData.postValue(event)
    }

    interface OnLocalEventCallback<Event : LocalLifecycleEvent> {
        fun onEventCallback(event: Event)
    }
}