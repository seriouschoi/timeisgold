# 프로젝트 개요.
- 앱 이름: My Daily Routine
- 기능
  - 시간 슬롯 관리 및 수행률 추이 제공.
- 목적
  - 클린아키텍쳐 적용 연습
  - 다중 모듈 구조 적용(now in android 참고)
  - SOLID 원칙을 고민하며, 결합도와 응집도를 최적화 
  - 도메인/테스트 주도 개발 방법 으로 구현.   

# 주요 기능
- [ ] 루틴 스케줄 관리.
- [ ] 반복 요일 설정
- [ ] 루틴 알림 기능.
- [ ] 시간 슬롯별 메모 관리
- [ ] 루틴 알림에서 빠르게 수행여부를 눌러서, 수행률 트래킹.

# 문서
- [documents](/docs)
- [todo](/docs/todo)

# 모듈
- [:app](/app) 
  - 모듈간 조립
  - 의존성 조립 (DI)
  - 앱 오케스트레이션 (NavHost)
- [:core](/core) - 공용 인프라. 도메인, data, logger등.
- [:feature](/feature) - 기능, 화면(기능)
- [:build-logic](/build-logic) - gradle build 설정 등.

# 학습 자료 모음
[:doc:study](/docs/study)
