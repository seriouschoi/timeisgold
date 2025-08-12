# TODOs

## Architecture Refactor to Hexagonal
- [ ] Rename :data → :adapter-repository-db-room
- [ ] Move `TimeScheduleRepository` → `domain.port.TimeScheduleRepositoryPort`
- [ ] Rename `TimeScheduleRepositoryImpl` → `TimeScheduleRepositoryAdapter`
- [ ] Apply `@Inject` to UseCase constructors (javax.inject)
- [ ] 스케줄에 태그 추가. 추후 리포트제공시 태그 기반으로 수행률 확인.