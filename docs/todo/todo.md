# TODOs

## TimeSlot추가.
- [ ] 사용자가 시간을 선택해서 오지 않았다면, 현재 루틴의 빈시간을 자동으로 기본 선택하기.
- [x] 저장 기능 개발.
- [ ] valid check개발.

## Routine Page.
- 상단
  - [x] 타임 루틴 이름, 요일
  - [x] 루틴이 없을 때는 요일만 표시.
  - [x] TopAppBar로 바꾸자..
  - [x] 루틴 유무를 별개의 상태로 만들지 말고, 상단 앱바 구조는 유지
    - 앱바 우측에 "시간표 만들기" 버튼 표시로 처리.

- 컨텐츠
  - [ ] 현재 요일의 타임 슬롯 목록.
    - [x] 슬롯을 Flow로 가져오자.
    - 슬롯을 표시하기 위해선,
    - [ ] ***00:00~24:00 시간표가 있어야 하며, 그안에 타임슬롯을 출력해야 한다.***
    - [x] FAB메뉴로 타임 슬롯 추가.
  - [x] 루틴이 없을경우, 오늘은 루틴이 없습니다. 루틴을 만들까요? 라는 메시지 출력.

## 페이저 탑바에 오늘로 이동 버튼 추가해야 함.
- 루틴페이저 상단 바에 오늘로 이동 버튼 하나 있어야 함.
  - 어디에 넣지..
  - 우측에 아이콘 하나 더 넣을까...근데 좀 지저분할것 같은데..
  - 루틴 제목도 들어가는데...
  - 오른쪽에 메뉴를 두개 넣자.
    - 오늘로 이동 버튼.
    - 햄버거 메뉴.
      - 루틴 수정/삭제 버튼.

## time routine edit test.
- [x] 우선 deprecated 된 함수들 제거.
- [ ] time routine edit viewmodel test 추가. (intent의 테스트 전부 작성.)

## time routine edit
- [ ] uuid생성 규칙 일원화를 위해 도메인에 접근자 정의하기.
  - UUID.randomUUID().toString() 이걸 일일히 하다보면, 다른곳에서 다른 규칙을 쓸 우려가 있음.
- [ ] TimeRoutineRepositoryPort가 너무 큼. Composition과 Routine을 나눠야 함.
- [ ] 현재까지 만들어진 모든 뷰모델을 SavedStateHandle을 사용하여 초기화할 값 전달.
- [ ] 테스트 커버리지 도구 도입.

# TimeRoutineComposition 남발로 인한 사이드이펙트 개선
- [x] 아래의 내용을 진행.
  - [x] TimeRoutine데이터의 경계를 확정하기.
  - [x] 남용된 Composition대신 경계가 확정된 Definition을 사용.
  - [x] Composition은 남용하지 않기.

# TimeSlot Tag.
- [ ] 타임슬롯에 태그 추가. 추후 리포트제공시 태그 기반으로 수행률 확인.
  - TimeSlotTag 추가.

# TimeSlot Memo
- [ ] 수행 기록제공을 할때, 사용자가 메모를 남기는 기능. 
  - 사진과 함께 다이어리 처럼 남기는 컨텐츠 제작도구 형태.

# 개인용/공개용 앱으로 빌드 구성 분리.
- 목적: 클라우드 기능을 서비스로 제공할 경우, 지속적인 비용 부담. 개인용으로만 사용.
- [ ] :app 모듈을 :app-public, :app-personal 모듈로 분리.
- [ ] 기존 :app 모듈의 내용을 :presentation 모듈로 분리.
- [ ] :app-public, :app-personal은 각 빌드 구성에 필요한 모듈만 포함.
  - :app-public은 data-room-adapter만 제공.
  - :app-personal은 data-cloud-adapter를 제공.


## 도메인과 데이터를 미리 정의하고, 미리 만든 테스트가 개발을 방해하는 상황.
- 리팩토링에 의해 터지는 테스트가 의미가 있을까?
  - 차라리 지우는게 맞음.
- [x] 우선 테스트를 차라리 다 지우고,
  - 한 피쳐를 만든 이후 테스트를 만들자.
- [ ] 향후 테스트는 자동화 도구에 의해 자동 생성 시키는것도 고민해보자.(Gemini CLI)

## Data모듈에서 모든 리턴을 DataResult로 하는게 좋아보이는데..?