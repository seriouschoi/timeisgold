package software.seriouschoi.timeisgold.core.common.ui.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.dp
import timber.log.Timber
import kotlin.math.absoluteValue

/**
 * Created by jhchoi on 2025. 10. 4.
 * jhchoi
 */
fun Modifier.multipleGesture(
    key: Any,
    slotBoundsMap: () -> Map<Int, Rect>,
    onSelected: (index: Int, dragTarget: DragTarget) -> Unit,
    onDrag: (index: Int, dx: Float, dy: Float, dragTarget: DragTarget) -> Unit,
    onDrop: (index: Int, dragTarget: DragTarget) -> Unit,
    onTap: (index: Int, dragTarget: DragTarget) -> Unit,
) = pointerInput(key) {
    awaitEachGesture {
        val down = awaitFirstDown()
        Timber.d("down position. x=${down.position.x}, y=${down.position.y}")

        val hit = slotBoundsMap().entries.firstOrNull {
            it.value.contains(down.position)
        }
        if (hit == null) return@awaitEachGesture
        val activeDragTarget = when {
            down.position.y - hit.value.top < 20.dp.toPx() -> DragTarget.Top
            down.position.y - hit.value.top > hit.value.height - 20.dp.toPx() -> DragTarget.Bottom
            else -> DragTarget.Card
        }
        onSelected(hit.key, activeDragTarget)
        Timber.d("gesture hit. index=${hit.key}, activeDragTarget=${activeDragTarget}")

        var distanceXAbs = 0f
        var distanceYAbs = 0f
        val downTimeStamp = System.currentTimeMillis()
        val movedOffset = Offset(5.dp.toPx(), 5.dp.toPx())
        var isMoved = false
        var longPressed = false
        while (true) {
            val event = awaitPointerEvent().changes.firstOrNull() ?: break
            if (event.pressed.not()) {
                event.consume()
                if (longPressed) {
                    onDrop(hit.key, activeDragTarget)
                    break
                }
                if (!isMoved) {
                    onTap(hit.key, activeDragTarget)
                    break
                }
                break
            }

            val dragAmount = event.positionChange()
            distanceXAbs += dragAmount.x.absoluteValue
            distanceYAbs += dragAmount.y.absoluteValue

            if (longPressed) {
                event.consume()

                onDrag(hit.key, dragAmount.x, dragAmount.y, activeDragTarget)
                continue
            }

            if (distanceXAbs > movedOffset.x || distanceYAbs > movedOffset.y) {
                if (!isMoved) {
                    Timber.d("move!! distanceX=$distanceXAbs, distanceY=$distanceYAbs")
                }
                isMoved = true
            }

            val now = System.currentTimeMillis()
            if (now - downTimeStamp > 200) {
                if (!isMoved) {
                    //long press!!
                    Timber.d("long press!!")
                    longPressed = true
                }
            }
        }
    }
}



enum class DragTarget { Card, Top, Bottom }