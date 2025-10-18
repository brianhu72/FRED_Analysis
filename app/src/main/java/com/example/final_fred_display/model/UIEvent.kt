package com.example.final_fred_display.model

class UIEvent<T>(
    val payload: T


) {



    private var isConsumed = false


    fun consume(then: (T) -> Unit) {
        if (isConsumed) {
            return
        }

        then(payload)
        isConsumed = true
    }


    suspend fun consumeSuspend(then: suspend (T) -> Unit) {
        if (isConsumed) {
            return
        }

        then(payload)
        isConsumed = true
    }
}
