# github action workflow

# CI, UnitTest
시점
- 모든 브랜치의 push, pull request에서 확인.

역할
- 빌드 유효확인. 
- 정적분석 
- 유닛테스트

# Andorid Test
시점
- 무거우므로, 실제 로직의 변경시에만 실행.(app, core, feature, presentation, 의존 변경시.)
- master브랜치의 변경시에만 실행.

역할
- 에뮬레이터 환경에서 동작 테스트.
