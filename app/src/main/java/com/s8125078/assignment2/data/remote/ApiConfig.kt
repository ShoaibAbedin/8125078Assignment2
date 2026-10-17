package com.s8125078.assignment2.data.remote

// Settings for the NIT3213 API
object ApiConfig {

    const val BASE_URL = "https://nit3213apinew.onrender.com/"

    // my class location -> /footscray/auth
    const val CAMPUS = "footscray"

    // Render's free tier sleeps when idle, so the first request can be really slow
    const val CONNECT_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 60L
}
