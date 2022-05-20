package com.example.examer.utils

import android.os.CountDownTimer

/**
 * A utility function that is used to create an instance of
 * [CountDownTimer] .
 * @param millisInFuture millis since epoch when timer should stop.
 * @param countDownInterval the interval in millis after which the
 * user receives the callbacks.
 * @param onTimerTick a lambda that receives an instance of [Long]
 * indicating the amount of time until finished.
 * @param onTimerFinished an optional lambda the will be executed
 * when the timer finishes.
 */
fun buildCountDownTimer(
    millisInFuture: Long,
    countDownInterval: Long = 1_000,
    onTimerTick: (millisUntilFinished: Long) -> Unit,
    onTimerFinished: () -> Unit = {}
) = object : CountDownTimer(
    millisInFuture,
    countDownInterval,
) {
    override fun onTick(millisUntilFinished: Long): Unit = onTimerTick(millisUntilFinished)
    override fun onFinish(): Unit = onTimerFinished()
}