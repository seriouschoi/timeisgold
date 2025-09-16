# 화면 구조
- Scaffold	
  - 화면의 전체 레이아웃 골격 제공 (topBar, bottomBar, FAB, snackbar 호스팅)	
  - 모든 화면의 루트
- TopAppBar/CenterAlignedTopAppBar/Medium/LargeTopAppBar	
  - 상단 앱바 (타이틀, 액션, 네비게이션)	
  - 화면 헤더
- NavigationBar
  - 하단 3~5개 주요 섹션 이동 버튼	
  - Bottom Navigation
- NavigationRail	
  - 태블릿/대화면에서 좌측 세로 네비게이션	
  - 태블릿, 데스크톱
- NavigationDrawer	
  - 햄버거 메뉴로 열리는 사이드 패널	
  - 글로벌 메뉴
- BottomSheetScaffold	
  - 하단에서 올라오는 시트 + 컨텐츠 레이아웃	
  - BottomSheet UX
- ModalBottomSheet / BottomSheet	
  - 단순 모달 시트 (Scaffold 없이)	
  - 설정, 상세 보기 등
- SnackbarHost	
  - 앱 전체에서 Snackbar 관리	
  - 보통 Scaffold 안
- FloatingActionButton (FAB)	
  - 화면의 주요 1차 액션 
  - 보통 오른쪽 하단

# 섹션 헤더/패널에 쓰이는 요소

TopAppBar와 같은 역할을 더 작은 단위에서 수행하는 UI 컴포넌트도 있습니다.

- Card	
  - 표면(surface)을 분리해 강조	
  - elevation, shape 지원
- Surface	
  - 배경 + contentColor 제공, UI 단위 블록화	
  - TopAppBar 내부도 사실 Surface
- ListItem	
  - 제목/부제목/아이콘 있는 행	
  - Settings 화면 등
- Divider	
  - 섹션 구분선	
  - 색상 자동 적용 (MaterialTheme)
- TabRow	
  - 상단 탭 네비게이션	
  - TopAppBar 아래에 종종 배치
- Tab	
  - 개별 탭	
  - 선택 상태, 색상 자동

# Material이 스타일 적용되는 컨테이너

- Surface	
  - 가장 기본 컨테이너. 배경색, contentColor, shape, elevation 제공
- Card	
  - Surface + padding/elevation 기본값
- ElevatedCard, OutlinedCard, FilledCard	
  - 다양한 시각적 변형
- Dialog, AlertDialog	
  - 모달 대화상자
- DrawerSheet	
  - NavigationDrawer 안에서 쓰이는 시트

# 추천 패턴
- 앱 루트: Scaffold
- 상단: TopAppBar (또는 CenterAlignedTopAppBar)
- 하단: NavigationBar or BottomAppBar
- FAB: FloatingActionButton
- 컨텐츠 블록: Card + Surface
- 섹션 구분: Divider + ListItem
