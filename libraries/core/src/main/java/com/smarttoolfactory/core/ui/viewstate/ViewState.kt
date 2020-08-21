package com.smarttoolfactory.core.ui.viewstate

/**
 * Class to set or retrieve current state of a layout or view. Three states
 *
 * * Loading: The time passed when operation starts and ends with success or fail
 * * Error: When data operation has failed. Can show message or display retry instructions or buttons
 * * Success: When data is retrieved from remote or local source
 *
 */
class ViewState<T>(
    val status: Status,
    val data: List<T>? = null,
    val error: Throwable? = null
) {
    fun isLoading() = status == Status.LOADING

    fun getErrorMessage() = error?.message

    fun shouldShowErrorMessage() = error != null
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}
