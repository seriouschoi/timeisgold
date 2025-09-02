package software.seriouschoi.timeisgold.domain.data.entities

/**
 * Created by jhchoi on 2025. 9. 2.
 * jhchoi
 */
sealed interface DomainError {
    data class Validation(val reason: String): DomainError
    data class NotFound(val id: String): DomainError
    data class Conflict(val key: String): DomainError
}

sealed class DomainResult<out T> {
    data class Success<T>(val value: T): DomainResult<T>()
    data class Failure(val error: DomainError): DomainResult<Nothing>()
}