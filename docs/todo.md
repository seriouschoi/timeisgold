# TODOs
- [x] build-logic을 정의하여 공통된 build.gradle 정리.
- [x] presentation모듈의 build.gradle.kts의 공통 빌드 정보를 컨벤션 플러그인으로 이관.
- [x] 하나의 컨벤션 플러그인에 모여있는 빌드 정보를 쪼개서 나머지 모듈도 적용.

- [ ] db get함수를 전부 flow로 리턴하기.
  - [ ] GetTimeRoutineFlowTest 작성하기.
- [ ] @Relation -> @DatabaseView
  - db 의 relation객체를 datbaseview를 사용해서, 만든는게 더 명료해 보임.
  - DatabaseView하나를 만듦으로서, 불필요한 쿼리들도 줄일 수 있음.

- [ ] 화면 작업. 
  - [ ] :presentation을 :feature 서브 모듈들로 쪼개야함.
  - [ ] feature/timeroutinetab-bar 모듈 정의 및 개발
    - [todo](feature/timeroutine-bar/todo.md)


- [x] 모든 마크다운 문서를 docs 디렉토리 아래 모으기.


## TimeSlot Tag.
- [ ] 타임슬롯에 태그 추가. 추후 리포트제공시 태그 기반으로 수행률 확인.
  - TimeSlotTag 추가.
