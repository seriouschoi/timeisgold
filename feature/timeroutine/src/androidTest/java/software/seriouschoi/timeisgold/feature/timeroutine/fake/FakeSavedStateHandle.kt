package software.seriouschoi.timeisgold.feature.timeroutine.fake

import androidx.lifecycle.SavedStateHandle
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * Created by jhchoi on 2025. 9. 4.
 * jhchoi
 */
inline fun <reified T : Any> T.toSavedStateHandle(): SavedStateHandle {
    val jsonElement = Json.encodeToJsonElement(this)
    val map = (jsonElement as JsonObject).mapValues { (_, value) ->
        when {
            value.jsonPrimitive.intOrNull != null -> value.jsonPrimitive.int
            value.jsonPrimitive.booleanOrNull != null -> value.jsonPrimitive.boolean
            value.jsonPrimitive.doubleOrNull != null -> value.jsonPrimitive.double
            value is JsonNull -> null
            else -> value.jsonPrimitive.content
        }
    }
    return SavedStateHandle(map)
}