package software.seriouschoi.timeisgold.domain.data

/**
 * Created by jhchoi on 2025. 9. 19.
 * jhchoi
 */
fun <T> DataResult<T>.asDomainResult(): DomainResult<T> {
    return when (this) {
        is DataResult.Failure -> {
            when (this.error) {
                DataError.Conflict -> DomainResult.Failure(DomainError.Conflict.Data)
                DataError.NotFound -> DomainResult.Failure(DomainError.NotFound.TimeSlot)
                else -> DomainResult.Failure(DomainError.Technical.Unknown)
            }
        }

        is DataResult.Success -> {
            DomainResult.Success(this.value)
        }
    }
}