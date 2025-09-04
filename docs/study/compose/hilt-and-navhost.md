# hiltViewModel() in composable
## ViewModelStoreOwner와 NavBackStackEntry
- Compose Navigation에서는 각 NavBackStackEntry가 자체적으로 ViewModelStoreOwner(뷰모델 저장관리주체) 역할을 합니다.
- hiltViewModel(backStackEntry), 혹은 hiltViewModel() 을 호출하면,
    - 그 backStackEntry 범위에서 ViewModel을 관리하게 됩니다.
- 따라서 NavBackStackEntry가 백스택에서 살아있는 동안 ViewModel 인스턴스는 재사용됩니다.
- 반대로, 해당 엔트리가 **pop**되면 ViewModelStore도 사라지고 → ViewModel은 dispose → 새로 진입하면 새 인스턴스가 생성됩니다.

## NavHost와의 관계
- NavHost는 실제로 NavBackStackEntry들을 보관하는 컨테이너입니다.
- 만약 동일한 NavHost 안에서 같은 route를 push/pop 하면, hiltViewModel()은 그 backStackEntry에 바인딩된 ViewModel을 재사용합니다.
- 하지만 다른 NavHost라면 ViewModelStoreOwner가 달라지므로, hiltViewModel()은 새로운 ViewModel을 생성합니다.

## 예시
```kotlin
NavHost(navController, startDestination = "home") {
    composable("home") { backStackEntry ->
        val vm: HomeViewModel = hiltViewModel(backStackEntry)
        HomeScreen(vm)
    }
    composable("detail/{id}") { backStackEntry ->
        val vm: DetailViewModel = hiltViewModel(backStackEntry)
        DetailScreen(vm)
    }
}
```
- home -> detail 이동 시: HomeViewModel은 여전히 유지됨 (backStackEntry 살아있음).
- detail -> popBackStack() 시: DetailViewModel은 파괴됨.
- 다시 detail로 navigate하면 -> 새로운 DetailViewModel 인스턴스 생성.

## 요약
- hiltViewModel()은 NavBackStackEntry 단위로 ViewModel을 제공.
- NavHost가 다르면 ViewModel도 별도 관리 → 새로 생성됨.
- 같은 NavHost 안에서는 backStackEntry 생존 여부에 따라 재사용/재생성 여부가 결정됨.

# hiltViewModel(backStackEntry) 와 hiltViewModel() 차이
인자로 아무 것도 주지 않으면, Compose는 **현재 Composition의 ViewModelStoreOwner**를 자동으로 찾는다.  
기본적으로는 현재 NavBackStackEntry가 Owner로 쓰입니다.  
그래서 NavHost 안의 composable 블록에서 그냥 hiltViewModel()을 호출하면,  
해당 route에 해당하는 NavBackStackEntry에 속한 ViewModel이 생성됩니다.

hiltViewModel(backStackEntry)는 명시적으로 NavBackStackEntry를 전달한다.  
자식 route에서 부모 route의 viewModel을 재사용하고 싶을때 사용한다.

예시)
```kotlin
composable("parent") { parentEntry ->
    val parentVM: ParentViewModel = hiltViewModel(parentEntry)

    NavHost(navController, startDestination = "child") {
        composable("child") { childEntry ->
            // childEntry에 속하면 childVM은 child 스코프
            val childVM: ChildViewModel = hiltViewModel(childEntry)

            // parentEntry를 명시적으로 지정하면 같은 ParentViewModel 재사용 가능
            val sameParentVM: ParentViewModel = hiltViewModel(parentEntry)
        }
    }
}
```

근데 이 경우는 피하는게 좋을것이다. 부모 뷰모델과 자식 뷰와 결합은 권하는 구조는 아니다.  
부모 뷰모델이 자식 뷰모델의 책임까지 가지면서, 잘못된 결합이 생길 우려가 높다.
