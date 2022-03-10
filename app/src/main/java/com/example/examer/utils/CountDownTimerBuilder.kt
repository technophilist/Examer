package com.example.examer.utils

import android.os.CountDownTimer

fun createCountDownTimer(
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