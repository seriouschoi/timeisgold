# 초기에 만든 usecase, repository, dao정리.

1. 사용하지 않는 usecase제거.
2. repository 정리.
3. 잘못 사용중인 entity, vo 정리.
   1. uuid, createTime를 vo에서 분리하여 MetaInfo로 정의. 

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
```

# System.currentTimeMillis() -> Instant.
createTime을 UTC기준으로 저장.

# 리팩토링 진행.
원래 피쳐 완료후 리팩토링을 하려고 했으나 
기준의 명시적 생성 플로우를 암묵적 즉시 생성 플로우로 바뀌게 되면서,
지금 리팩토링이 필요해짐.

1. 레포지토리를 새로 하나 만들자. -> 완료.
   1. 이유. 루틴이 없을때도 슬롯을 바로 추가하면, 제목없는 루틴이 자동으로 생성되게 만들것이다.
   2. ~~지금의 레포지토리는 루틴과 슬롯이 나뉘어 있음. 오히려 불분명함.~~
      1. 이건 오히려 잘못된 생각. 해당 문제는 비지니스로직이며, usecase에서 처리함.
      2. 허나 기존 레포지토리는 플로우에 대한 잘못된 이해와 과설계로 인해 불필요하게 추가된 함수들이 많고,
      3. dao와 repo, usecase의 경계를 잘못 이해하고 만들었으므로, 새로 만드는게 맞긴 함.
2. 새로운 데이터 타입을 만들어서 정의부터 하고, 기존 데이터 타입을 전부 Deprecated로 표시한다. -> 완료.
   1. 새로운 타입은 아래와 같다.
      1. VO: 도메인의 타입.
      2. MetaEnvelop: Meta정보를 포함한 봉투.
      3. *Schema라는 이름의 데이터 객체는 전부 Entity라는 이름으로 바꾼다. -> 완료.
         1. Entity는 DB와 관련되서 쓰는게 더 나을것 같음.
3. createTime으로 System.currentTimeMillis 대신 Instant로 대체.

timeSlot을 저장할때, 루틴을 자동생성하는 건 useCase에서 처리할 일..
레포지토리는 문자그대로 CRUD만 한다고 생각하자...딴생각 하지 말고..

## NewRepoAdapter만들기. -> 완료.

## id를 nullable로 하기 보다, = 0으로 초기화만 하자. -> 완료.
nullable체크를 자꾸 추가하는게 번거롭다.
애초에 DB에선 여기가 null일수 없는데, 이걸 nullable로 정의해놔서 직관적이지도 않고,
기본적으로 autoGen true해놨으면, id가 0이면 자동으로 autoGen이 타게 되어 있다.