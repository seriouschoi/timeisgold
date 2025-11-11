# 복잡한 Flow.

아래 예시의 flow는 두개의 데이터 소스를 조합해서, 선택 가능한 요일을 가져오는 flow이다.
이때, 데이터 소스를 찾지 못했을 경우에는 요일 선택 없음으로 처리하고, 그외 오류는 DomainError로 승격시킨다.
각 조건을 잘게 쪼개서 구현한다는 생각으로 여러개의 flow로 분산되었다.

```kotlin
fun watchSelectableDayOfWeeks(dayOfWeek: DayOfWeek): Flow<DomainResult<List<DayOfWeek>>> {
    val allDayOfWeeksSource = timeRoutineRepository.watchAllDayOfWeeks()
    val currentRoutineSource = timeRoutineRepository.watchRoutine(dayOfWeek)

    val allDayOfWeekFailed = allDayOfWeeksSource.mapNotNull {
        it as? DataResult.Failure
    }.filter {
        it.error !is DataError.NotFound
    }

    val currentRoutineFailed = currentRoutineSource.mapNotNull {
        it as? DataResult.Failure
    }.filter {
        it.error !is DataError.NotFound
    }

    val failed = merge(allDayOfWeekFailed, currentRoutineFailed).map {
        it.asDomainResult<List<DayOfWeek>>()
    }

    val allDayOfWeeks = allDayOfWeeksSource.mapNotNull {
        it as? DataResult.Success
    }.mapNotNull {
        it.value
    }
    val notFoundAllDayOfWeeks = allDayOfWeeksSource.mapNotNull {
        it as? DataResult.Failure
    }.mapNotNull {
        it.error as? DataError.NotFound
    }.map {
        emptySet<DayOfWeek>()
    }
    val existingDayOfWeeks = merge(allDayOfWeeks, notFoundAllDayOfWeeks)


    val currentRoutineDayOfWeeks = currentRoutineSource.mapNotNull {
        it as? DataResult.Success
    }.mapNotNull {
        it.value?.payload?.dayOfWeeks ?: emptySet()
    }
    val notFoundCurrentDayOfWeeks = currentRoutineSource.mapNotNull {
        it as? DataResult.Failure
    }.mapNotNull {
        it.error as? DataError.NotFound
    }.map {
        emptySet<DayOfWeek>()
    }

    val currentDayOfWeek = merge(currentRoutineDayOfWeeks, notFoundCurrentDayOfWeeks)

    val selectableDayOfWeeks: Flow<DomainResult.Success<List<DayOfWeek>>> = combine(
        existingDayOfWeeks,
        currentDayOfWeek,
    ) { existingDayOfWeeks, currentDayOfWeeks ->
        DayOfWeek.entries.filter { day ->
            val usedByOtherRoutine = existingDayOfWeeks.contains(day)
            val usedByCurrentRoutine = currentDayOfWeeks.contains(day)

            // 현재 루틴에서 사용 중이거나, 다른 루틴에서 사용 중이 아닌 경우만 활성
            !usedByOtherRoutine || usedByCurrentRoutine
        }
    }.map {
        DomainResult.Success(
            it
        )
    }

    return merge(
        selectableDayOfWeeks,
        failed
    )
}
```
각 요소들을 분할해서 처리한다는 생각으로 만들었지만, 실제로는 너무 복잡한 flow로 인해,
동작의 흐름이 한눈에 보이지 않는다.

이를 차라리 하나의 flow로 만들어보자. 
아래는 그 예이다.
데이터 소스를 combine하고, 오류로 처리할 요소를 찾아서 when으로 처리 한다.
2중 when이 지저분하게 보이겠지만, 전자의 흐름보다는 깔끔하다.

```kotlin
fun watchSelectableDayOfWeeks2(dayOfWeek: DayOfWeek): Flow<DomainResult<List<DayOfWeek>>> {
    val allDayOfWeeksSource = timeRoutineRepository.watchAllDayOfWeeks()
    val currentRoutineSource = timeRoutineRepository.watchRoutine(dayOfWeek)

    return combine(
        allDayOfWeeksSource,
        currentRoutineSource
    ) { allDayOfWeeks, currentRoutine ->

        //not found 이외 실패는 domainError.
        val allFailure = (allDayOfWeeks as? DataResult.Failure)?.takeIf { it.error !is DataError.NotFound }
        val currentFailure = (currentRoutine as? DataResult.Failure)?.takeIf { it.error !is DataError.NotFound }

        when {
            allFailure != null -> allFailure.asDomainResult()
            currentFailure != null -> currentFailure.asDomainResult()
            else -> {
                val usedDayOfWeeks = when(allDayOfWeeks) {
                    is DataResult.Success -> allDayOfWeeks.value
                    is DataResult.Failure -> emptySet()
                }
                val currentDayOfWeeks = when(currentRoutine) {
                    is DataResult.Success -> currentRoutine.value?.payload?.dayOfWeeks ?: emptySet()
                    is DataResult.Failure -> emptySet()
                }

                val selectableDayOfWeeks = DayOfWeek.entries.filter { day ->
                    val usedByOtherRoutine = day in usedDayOfWeeks
                    val usedByCurrentRoutine = day in currentDayOfWeeks

                    // 현재 루틴에서 사용 중이거나, 다른 루틴에서 사용 중이 아닌 경우만 활성
                    !usedByOtherRoutine || usedByCurrentRoutine
                }

                DomainResult.Success(selectableDayOfWeeks)
            }
        }
    }
}
```

1. { } 를 줄이기보다, 한 { } 레벨 안에서 최대한 단순하게 보이게 만들어야 한다.
2. 분기 처리는 연산자 체인 바깥에서 처리한다.
3. 
이런 구현을 하기 위해선 다음과 같이 생각을 해봐야 한다.

1. usecase의 입력과 출력을 정확히 정리한다.
   1. 이 경우, 입력은 요일, 출력은 선택 가능한 요일이다.
2. 에러 계층을 정리한다.
   1. 값으로 흡수할 에러, 도메인 오류로 보낼 에러를 정리한다.
3. Flow연산은 가급적 최소한만 쓴다.
   1. 기존 예시는 flow들이 너무 많았다.
      1. 성공용 flow
      2. 실패용 flow
      3. not found용 flow.
   2. 그리고 이 세갈래 flow들을 map, filter, merge를 반복하고 있어서, 흐름이 추적하기 어렵다.


