package com.example.examer.utils

import android.content.Context

fun Context.isDeviceAutomaticDateTimeEnabled() = android.provider.Settings.Global.getInt(
    contentResolver,
    android.provider.Settings.Global.AUTO_TIME,
    0
) == 1

fun Context.isDeviceAutomaticTimeZoneEnabled() = android.provider.Settings.Global.getInt(
    contentResolver,
    android.provider.Settings.Global.AUTO_TIME_ZONE,
    0
) == 1