package software.seriouschoi.timeisgold.feature.timeroutine.fake

import androidx.lifecycle.SavedStateHandle
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive

/**
 * Created by jhchoi on 2025. 9. 4.
 * jhchoi
 */
inline fun <reified T : Any> T.toSavedStateHandle(): SavedStateHandle {
    val jsonElement = Json.encodeToJsonElement(this)
    val map = (jsonElement as JsonObject).mapValues { it.value.jsonPrimitive.content }
    return SavedStateHandle(map)
}