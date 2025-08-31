package software.seriouschoi.timeisgold.domain.services

import software.seriouschoi.timeisgold.domain.port.TimeRoutineRepositoryPort
import javax.inject.Inject

internal class TimeRoutineDomainService @Inject constructor(
    private val timeRoutineRepository: TimeRoutineRepositoryPort
){

}