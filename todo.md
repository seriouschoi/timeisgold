# TODOs

## Architecture Refactor to Hexagonal
- [ ] Rename :data → :adapter-repository-db-room
- [ ] Move `TimeScheduleRepository` → `domain.port.TimeScheduleRepositoryPort`
- [ ] Rename `TimeScheduleRepositoryImpl` → `TimeScheduleRepositoryAdapter`
- [ ] Apply `@Inject` to UseCase constructors (javax.inject)