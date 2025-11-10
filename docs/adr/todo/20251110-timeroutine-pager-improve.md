# time routine pager개선.

## 문제점.
뷰모델이 가진 여러개의 상태가 변경될때 마다 usecase를 통해 apply를 하고 있는 상황.
이로 인해, 다른 이벤트에 의해 uiState가 임시상태를 가지고 있어도, 해당 상태가 apply되는 문제가 생겼다.
물론 apply가 되기전에 debounce등의 처리를 하고 싶지만, 의도되지 않은 apply의 실행 자체를 막는게 올바른 방향.

이를 개선하기위해, 실제 사용자가 루틴의 타이틀, 요일의 선택으로 인해 intent가 발핼될때만 apply를 한다.

여기서 문제가...
routine 뷰모델에서 사용자의 인텐트에 의해서만 apply를 하려고 했는데,
뷰모델의 인텐트가 뷰모델이 가진 stateHolder의 intent를 들고 있는 랩핑된 구조라서,
상태를 처리하는 과정이 매우 지저분하다.
정확히는 뷰모델의 인텐트가 stateHolder의 intent에 의해 오염된 상황.
즉 이 오염부터 해결해야 한다.

근데 오염을 제거했다고 치자..
그러면 이젠 AStateComposable은 사용자 이벤트에 의해,
AStateIntent를 발행할 것이다.
그리고 AStateComposable이 파라미터로 들고 있는 AStateIntent 람다 콜백에 전달 할 것이다.
PageComposable은 AStateComposable의 콜백에 의해 AStateIntent를 수신하게 된다.
여기서 PageComposable은 PageViewModel이 들고 있는 AStateHolder에 intent를 전달하기위해,
물론 그보다는 AStateComposable의 의미를 해석해서, PageComposable입장에서의 PageIntent를 발행해서 PageViewModel에 전달할 것이다.
이 시점에서 PageComposable은 AStateIntent를 판단하는 로직이 들어가게 된다.
PageViewModel은 PageIntent를 받아서, 이 인텐트가 AStateHolder에 전달할 인텐트라면 다시 전달하게 될것이다.
그럼 지금 단순히 AStateComposable의 사용자 동작 하나로, 2회의 타입 전환이 있었고,
이로 인한 맵핑의 로직이 두군데서 생겼다.
이게 맞는걸까?

~~차라리 뷰모델이 sendIntent(aIntent: AStateIntent) 함수를 하나 들고 있는게 나을까?~~

이걸 맵핑이라고만 생각하지 말고.. 해당 page의 인텐트를 정의한다는 관점에서만 바라보는게 맞을것 같다.
즉 AStateComposable에서 발생한 인텐트는 page관점에서 해석이 들어가고..
pageviewmodel는 그 인텐트에 의한 처리를 하는데 그 과정에서 viewModel이 들고 있는 stateHolder에 
메시지가 되었든 액션이 되었든 전달할 수도 있는것 같아...

근데 여기서 좀 애매해지는게..
AStateComposable의 동작을 intent로 발행하고..
AStateHolder가 intent의 분기 처리를 해서 동작을 구현하게 했는데..
이 구조가 문제가 있는건가..?

차라리 StateHolder는 명령형 메소드만 제공하는 것이 낫겠다.
즉 인텐트는 한 뷰모델 계층에서만 존재하고..
그러면 AStateComposable은... AState를 구성하고..
이벤트는 그냥 람다를 호출하는것도 맞을것 같네..

## 정리.
Intent는 ViewModel계층에만 남긴다.
State + StateComposable + StateHolder는 아래와 같은 구성이 된다.
StateComposable은 State를 구성한다.
StateComposable의 이벤트는 람다 콜백으로 전달한다.
StateComposable를 들고 있는 ScreenComposable은 람다 콜백을 ScreenIntent로 만들어, ScreenViewModel에 전달한다.
ScreenViewModel은, 해당 Intent를 처리하고, 필요에 따라 StateHolder에 명령형 메소드를 호출하기도 한다.

## 이어서..
TimeRoutinePagerViewModel.watchRoutineEdit에서는 
사용자 의도에 의한 동작만 수신하여,(Intent를 수신하여..)
데이터의 변경 의도가 감지되면, usecase에 apply한다.
이 과정에서 위의 intent의 정리가 필요하므로, 상단에 정리된 intent정리를 선행한다.