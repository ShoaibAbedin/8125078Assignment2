package com.s8125078.assignment2.data.remote

import com.google.gson.JsonParseException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.coroutines.cancellation.CancellationException

// Runs an API call and turns exceptions into ApiResult.Failure.
// Used by all repositories so the try/catch isn't repeated.
suspend fun <T> safeApiCall(call: suspend () -> T): ApiResult<T> =
    try {
        ApiResult.Success(call())
    } catch (e: CancellationException) {
        throw e // don't swallow coroutine cancellation
    } catch (e: HttpException) {
        ApiResult.Failure(if (e.code() in 400..499) ApiError.CLIENT else ApiError.SERVER)
    } catch (e: SocketTimeoutException) {
        // has to come before IOException since it's a subclass
        ApiResult.Failure(ApiError.TIMEOUT)
    } catch (e: IOException) {
        ApiResult.Failure(ApiError.NETWORK)
    } catch (e: JsonParseException) {
        ApiResult.Failure(ApiError.INVALID_RESPONSE)
    } catch (e: Exception) {
        ApiResult.Failure(ApiError.UNKNOWN)
    }
