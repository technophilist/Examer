package com.example.examer.utils

import android.content.Context

/**
 * An extension function that returns true if the device's date/time
 * are set automatically by Android. It returns false, if the device's
 * date/time is set manually by the user.
 */
fun Context.isDeviceAutomaticDateTimeEnabled() = android.provider.Settings.Global.getInt(
    contentResolver,
    android.provider.Settings.Global.AUTO_TIME,
    0
) == 1

/**
 * An extension function that returns true if the device's time zone
 * is set automatically by Android. It returns false, if the the device's
 * time zone is set manually by the user.
 */
fun Context.isDeviceAutomaticTimeZoneEnabled() = android.provider.Settings.Global.getInt(
    contentResolver,
    android.provider.Settings.Global.AUTO_TIME_ZONE,
    0
) == 1