package com.s8125078.assignment2

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for Chronicle.
 *
 * [HiltAndroidApp] triggers Hilt's code generation and makes this class the root of the
 * app's dependency graph, so every injected object (Retrofit, repositories, ViewModels)
 * is created and managed from here (Lecture 7).
 */
@HiltAndroidApp
class ChronicleApplication : Application()
