package com.s8125078.assignment2

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Needed for Hilt - sets up dependency injection for the whole app
@HiltAndroidApp
class ChronicleApplication : Application()
