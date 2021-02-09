package se.oskarh.boardgamehub.repository

import retrofit2.Response

@Suppress("unused")
sealed class ApiResponse<out T> {
    companion object {
        fun <T> create(response: Response<T>): ApiResponse<T> {
            return if (response.isSuccessful) {
                val body = response.body()
                if (body == null || response.code() == 204) {
                    EmptyResponse()
                } else {
                    SuccessResponse(body)
                }
            } else {
                val message = response.errorBody()?.string().takeUnless { it.isNullOrEmpty() } ?: response.message()
                ErrorResponse(message ?: "Unknown error")
            }
        }

        fun <T> create(error: Throwable): ErrorResponse<T> {
            return ErrorResponse(error.message ?: "Unknown error")
        }
    }
}

class LoadingResponse<T> : ApiResponse<T>()

data class SuccessResponse<T>(val data: T) : ApiResponse<T>()

class EmptyResponse<T> : ApiResponse<T>()

data class ErrorResponse<T>(val errorMessage: String) : ApiResponse<T>()
