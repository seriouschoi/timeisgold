package software.seriouschoi.timeisgold

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import software.seriouschoi.timeisgold.presentation.PresentationRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PresentationRoot()
        }
    }
}