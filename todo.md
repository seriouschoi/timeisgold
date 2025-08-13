# TODOs

## Architecture Refactor to Hexagonal
- [ ] Rename :data → :adapter-repository-db-room
- [ ] Move `TimeScheduleRepository` → `domain.port.TimeScheduleRepositoryPort`
- [ ] Rename `TimeScheduleRepositoryImpl` → `TimeScheduleRepositoryAdapter`
- [ ] Apply `@Inject` to UseCase constructors (javax.inject)
- [ ] 타임슬롯에 태그 추가. 추후 리포트제공시 태그 기반으로 수행률 확인.
  - TimeSlotTag 추가.
- [ ] 엔티티 이름 변경. TimeSchedule -> TimeRoutine.
  - 기존 타임스케줄이 TimeSlot과 혼동됨.