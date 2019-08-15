package agency.digitera.android.promdate.util

sealed class ResultStatus<out T: Any> {

    object RequireLogin : ResultStatus<Nothing>()
    data class Idle(val isLoading: Boolean) : ResultStatus<Nothing>()
    data class OnSuccess<out T: Any>(val resultStatus: T) : ResultStatus<T>()
    data class OnAccessFailure<out T : Any>(val resultError: T) : ResultStatus<T>()
    data class OnFailure(val throwable: Throwable) : ResultStatus<Nothing>()

}