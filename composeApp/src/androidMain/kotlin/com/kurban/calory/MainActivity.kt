package com.kurban.calory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import com.kurban.calory.core.navigation.DefaultRootComponent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val component =
            DefaultRootComponent(
                componentContext = defaultComponentContext(),
            )

        setContent {
            AppRoot(component)
        }
    }
}
