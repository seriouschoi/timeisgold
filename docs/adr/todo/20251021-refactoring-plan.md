초기에 만든 usecase, repository, dao정리.

시점. crud가 이뤄지는 feature하나 완료후.

방법.

usecase정리.
jacoco로 커버리지 측정.
사용하지 않는 usecase제거.

repository 정리.
usecase정리후 jacoco를 실행하여, 사용하지 않는 레포지토리 함수 확인.
실제로 사용하지 않는 함수 정리 및 삭제.

vo정리 및 도메인 리팩토링.
혼용되서 사용된 entity, vo개념 정리.
createTime등의 메타데이터를 vo에서 분리하여 MetaEnvelope으로 감싸서 처리.

리팩토링된 구조 기준으로 테스트 코드 작성 진행.
AI를 통해 자동으로 테스트를 작성할 수 있는지 확인.

VO(Value Object)
변경 불가능한 값의 객체.
값 자체로 의미가 있어야함.
아마 도메인의 객체를 이걸로 하는게 맞을듯.
어...근데 식별자, id를 가지는 것은 안되는건가..?
그렇다면 MetaEnvelope 은 createTime, 식별자까지 가지는건 어떨까..?
```kotlin
data class MetaEnvelope<T>(
    val id: String,
    val createTime: Instant,
    val value: T // VO
)
```
이러면 VO는 값에 더 집중될것 같긴 하다.

DTO(Data Transfer Object)
계층간 데이터 전달을 위한 객체.
주로 presentation/data layer간 통신에 사용한다고 하는데..
presentation의 uiState -> VO로 변환해서 도메인에 전달하고,
도메인은 VO를 자기가 알고 있는 레포지토리에 그대로 던지는게 낫지 않나?
레포지토리를 VO를 받아서 처리하고,
즉 DTO는 사실상 레포지토리가 외부의 리모트와 통신할 형태를 정의할때 정의하는 객체가 될것 같다.

Entity
고유 식별자를 가지고, 영속성 대상이 되는 객체.
DB의 테이블 row에 대응.
생성시간, 업데이트 시간등 변경 가능한 필드를 포함한다.
근데 난 이걸 문제해결의 최소단위객체라고 생각하고 정의했는데..이건 잘못된 개념일까?
본질적으로는 틀리진 않지만, DDD에서 문제해결 단위는 Aggregate Root에 더 가깝다.
DB 스키마 정의할때, 더 적합한 용어이므로, 영속성이 되는 객체를 정의할때만 쓰는게 더 좋을듯.

# 리팩토링할 구조.
```text
[UI] 
  ⇄ UiState (PresentationModel) 
    ⇄ VO 
      ⇄ UseCase 
        ⇄ VO 
          ⇄ Repository 
            ⇄ Entity 
              ⇄ DB Row

[API]
  ⇄ DTO (NetworkModel)
```
Aggregate Root??


# [ ] LocalDateTime -> OffsetDateTime
createTime처럼 로그 목적의 시간은 OffsetDateTime으로 저장한다.
이유: LocalDateTime은 현재 위치의 시간을 저장하므로, 
위치정보없이 저장된 시간만으로는 정확한 시간을 특정할 수 없다.

OffsetDateTime은 UTC기준이므로 이게 더 적절.

# n개의 요소를 combine하는데 그중에 한 요소가 변경될때만 flow를 발행하고 싶으면..
```kotlin
combine(checkedDayOfWeeks, dayOfWeek) { checkedDayOfWeeks, dayOfWeek ->
    checkedDayOfWeeks to dayOfWeek
}.distinctUntilChangedBy {
    it.first
}
```

# 리팩토링 진행.
루틴 제목, 요일, 슬롯 입력등의 흐름을 선형적으로 제공하지 않다보니,
피쳐 완료후 리팩토링이 아닌 지금 리팩토링이 필요해졌다.

1. 레포지토리를 새로 하나 만들자.
   1. 이유. 루틴이 없을때도 슬롯을 바로 추가하면, 제목없는 루틴이 자동으로 생성되게 만들것이다.
   2. 지금의 레포지토리는 루틴과 슬롯이 나뉘어 있음. 오히려 불분명함.
2. 새로운 데이터 타입을 만들어서 정의부터 하고, 기존 데이터 타입을 전부 Deprecated로 표시한다.
   1. 새로운 타입은 아래와 같다.
      1. VO: 도메인의 타입.
      2. MetaEnvelop: Meta정보를 포함한 봉투.
      3. *Schema라는 이름의 데이터 객체는 전부 Entity라는 이름으로 바꾼다.
         1. Entity는 DB와 관련된 개념으로 쓰는게 더 나을듯.
3. createTime으로 System.currentTimeMillis 대신 OffsetDateTime으로 전부 대체하기.
   1. LocalDateTime. .. 이것도 마찬가지.

timeSlot을 저장할때, 루틴을 자동생성하는 건 useCase에서 처리할 일..
레포지토리는 문자그대로 CRUD만 한다고 생각하자...딴생각 하지 말고..

MetaEnvelop은 Meta가 not null이어야지..


