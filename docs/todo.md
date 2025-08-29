# TODOs
- [x] build-logic을 정의하여 공통된 build.gradle 정리.
- [x] presentation모듈의 build.gradle.kts의 공통 빌드 정보를 컨벤션 플러그인으로 이관.
- [x] 하나의 컨벤션 플러그인에 모여있는 빌드 정보를 쪼개서 나머지 모듈도 적용.

[:doc:core:db](core/data-room-adapter)  
[:core:db](/core/data-room-adapter)
- [x] db get함수를 전부 flow로 리턴하기.
  - [x] GetTimeRoutineFlowTest 작성하기.
- [x] @Relation -> @DatabaseView
  - db 의 relation객체를 datbaseview를 사용해서, 만든는게 더 명료해 보임.
  - DatabaseView하나를 만듦으로서, 불필요한 쿼리들도 줄일 수 있음.
  - [x] Deprecated된 Relation제거.
  - [x] 테스트 다시 구현.
- [x] 도메인 정의 정리. TimeRoutine과 DayOfWeek분리.
  - [x] 테스트 다시 구현.
  - [x] turbine, advanceUntilIdle, backgroundScope.launch 남발한 영역들 제거.
  - [x] 테스트 exception 조건을 정확히 명시하기.

- [ ] 정책에 dayOfWeek설정 안하고 저장 못하게, 막기.

- [ ] 화면 작업. 
  - [ ] :presentation을 :feature 서브 모듈들로 쪼개야함.
  - [ ] feature/timeroutinetab-bar 모듈 정의 및 개발
    - [todo](feature/timeroutine-bar/todo.md)


- [x] 모든 마크다운 문서를 docs 디렉토리 아래 모으기.


## TimeSlot Tag.
- [ ] 타임슬롯에 태그 추가. 추후 리포트제공시 태그 기반으로 수행률 확인.
  - TimeSlotTag 추가.

## TimeSlot Memo
- [ ] 수행 기록제공을 할때, 사용자가 메모를 남기는 기능. 
  - 사진과 함께 다이어리 처럼 남기는 컨텐츠 제작도구 형태.