package com.kurban.calory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.arkivanov.decompose.defaultComponentContext
import com.kurban.calory.core.navigation.DefaultRootComponent
import com.kurban.calory.features.barcode.domain.scanner.BarcodeScanner
import org.koin.core.context.GlobalContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity : ComponentActivity(), KoinComponent {
    
    private val barcodeScanner by inject<BarcodeScanner>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        // Setup camera permission launcher
        val cameraPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { _ ->
            // Scanner will handle the permission result internally
        }
        
        // Setup scanner with permission launcher
        barcodeScanner.setContext(this)
        barcodeScanner.setupPermissionLauncher(cameraPermissionLauncher)
        
        val component =
            DefaultRootComponent(
                componentContext = defaultComponentContext(),
                koin = GlobalContext.get()
            )
        
        setContent {
            AppRoot(component)
        }
    }
}
