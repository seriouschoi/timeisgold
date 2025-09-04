# github action workflow

# CI, UnitTest
정책
- 모든 브랜치의 push, pull request에서 확인.

역할
- 빌드 유효확인. 
- 정적분석 
- 유닛테스트

# Andorid Test
정책
- 무거우므로, 문서를 제외한 실제 로직의 변경시 실행.
- master 브랜치의 변경시에만 실행.

역할
- 에뮬 환경에서 통합 테스트.