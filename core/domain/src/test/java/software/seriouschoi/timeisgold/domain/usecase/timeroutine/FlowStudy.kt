package software.seriouschoi.timeisgold.domain.usecase.timeroutine

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * Created by jhchoi on 2025. 9. 9.
 * jhchoi
 */
class FlowStudy {
    val flow1 = flow {
        emit(1)
        delay(100)
        emit(2)
        emit(3)
    }

    val flowA = flow {
        emit("a")
        emit("b")
        delay(100)
        emit("c")
    }

    data class StudyData(
        val name: String,
        val age: Int,
    )

    @Test
    fun combine() {
        runTest {
            // flowA와 flow1의 방출때마다 둘의 최신값을 처리한 flow를 만든다.
            combine(flowA, flow1) { fA, f1 ->
                "$fA$f1"
            }.collect {
                println(it)
            }
        }
    }

    @Test
    fun merge() {
        runTest {
            //두개의 flow에서 방출되는 즉시 하나의 flow로 합쳐 방출하는 플로우.
            merge(flowA, flow1).collect {
                println(it)
            }
        }
    }

    @Test
    fun scan() {
        runTest {
            //flow의 결과를 연속적으로 누적해서 방출하는 플로우.
            flow1.scan(0) { acc, value ->
                println("acc=$acc, value=$value")
                acc + value
            }.collect {
                println(it)
            }
        }
    }

    @Test
    fun mergeAndScan() {
        runTest {
            //두개의 flow에서 방출되는 즉시 하나의 flow로 합쳐서 방출된 값을,
            //연속적으로 누적해서 방출.
            merge(flowA, flow1).scan("") { acc: String, value: Any ->
                println("acc=$acc, value=$value")
                "${acc}${value}_"
            }.collect {
                println(it)
            }
        }
    }


    @Test
    fun zip() {
        runTest {
            //두개의 flow에서 같은 순번으로 방출되는 값을 짝지어서, 방출하는 flow
            //한쪽이 먼저 방출되어도, 상대 flow에서 같은 순번의 값이 나올때까지 대기한다.
            //둘중 상대적으로 짧은 flow가 끝나면 zip도 종료.
            flowA.zip(flow1) { fA, f1 ->
                "$fA$f1"
            }.collect {
                println(it)
            }
        }
    }
}