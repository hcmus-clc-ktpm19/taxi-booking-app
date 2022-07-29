package com.example.wiberdriver.utils

import android.util.Log
import com.example.wiberdriver.utils.Const.TAG
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent

class StompUtils {
    companion object{
        fun lifecycle(stompClient: StompClient) {
            stompClient.lifecycle().subscribe { lifecycleEvent: LifecycleEvent ->
                when (lifecycleEvent.type) {
                    LifecycleEvent.Type.OPENED -> Log.d(TAG, "Stomp connection opened")
                    LifecycleEvent.Type.ERROR -> Log.e(
                        TAG,
                        "Error",
                        lifecycleEvent.exception
                    )
                    LifecycleEvent.Type.CLOSED -> Log.d(TAG, "Stomp connection closed")
                    else -> {
                        Log.d(TAG, "Something wrong")
                    }
                }
            }
        }
    }
}