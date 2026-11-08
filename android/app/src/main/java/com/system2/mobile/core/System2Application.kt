package com.system2.mobile.core

import android.app.Application
import android.util.Log

class System2Application : Application() {
    override fun onCreate() {
        super.onCreate()
        AppLogger.init(this)
    }
}

object AppLogger {
    private const val TAG = "SYSTEM2"
    private var debug = true

    fun init(app: Application) {
        // Local-only logging; never upload logs off-device.
        debug = true
    }

    fun d(msg: String) { if (debug) Log.d(TAG, msg) }
    fun i(msg: String) { Log.i(TAG, msg) }
    fun w(msg: String) { Log.w(TAG, msg) }
    fun e(msg: String, t: Throwable? = null) { Log.e(TAG, msg, t) }
}

sealed class S2Result<out T> {
    data class Ok<out T>(val value: T) : S2Result<T>()
    data class Err(val reason: String, val cause: Throwable? = null) : S2Result<Nothing>()
}
