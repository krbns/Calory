@file:OptIn(ExperimentalResourceApi::class)

package com.kurban.calory.features.barcode

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import calory.composeapp.generated.resources.Res
import calory.composeapp.generated.resources.back
import calory.composeapp.generated.resources.barcode_scanning_title
import calory.composeapp.generated.resources.dismiss
import calory.composeapp.generated.resources.product_not_found
import calory.composeapp.generated.resources.scanning_error
import calory.composeapp.generated.resources.start_scanning
import calory.composeapp.generated.resources.stop_scanning
import calory.composeapp.generated.resources.add_to_diary
import calory.composeapp.generated.resources.add_portion
import calory.composeapp.generated.resources.portion_grams
import calory.composeapp.generated.resources.portion_invalid_grams
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kurban.calory.core.theme.elevation
import com.kurban.calory.core.theme.spacing
import com.kurban.calory.features.barcode.domain.model.BarcodeProduct
import com.kurban.calory.features.barcode.domain.model.ScanResult
import com.kurban.calory.features.barcode.ui.BarcodeScannerComponent
import com.kurban.calory.features.barcode.ui.model.BarcodeProductSearchResult
import com.kurban.calory.features.barcode.ui.model.BarcodeScannerEffect
import com.kurban.calory.features.barcode.ui.model.BarcodeScannerIntent
import com.kurban.calory.features.barcode.ui.model.BarcodeScannerUiState
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@OptIn(ExperimentalResourceApi::class)
@Composable
fun BarcodeScannerScreen(
    component: BarcodeScannerComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.subscribeAsState()
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedForPortion by remember { mutableStateOf<BarcodeProduct?>(null) }
    var portionInput by remember { mutableStateOf("100") }
    var portionError by remember { mutableStateOf<String?>(null) }
    val invalidPortionMessage = stringResource(Res.string.portion_invalid_grams)

    LaunchedEffect(component) {
        component.effects.collect { effect ->
            when (effect) {
                is BarcodeScannerEffect.ShowError -> {
                    errorMessage = effect.message
                }
                is BarcodeScannerEffect.NavigateBack -> {
                    component.onBack()
                }
                is BarcodeScannerEffect.NavigateToAddToDiary -> {
                    selectedForPortion = effect.product
                }
                else -> { /* Handle other effects */ }
            }
        }
    }

    BarcodeScannerContent(
        state = state,
        component = component,
        errorMessage = errorMessage,
        onIntent = { component.dispatch(it) },
        onAddToDiary = { selectedForPortion = it },
        onErrorDismiss = {
            errorMessage = null
            component.dispatch(BarcodeScannerIntent.ClearError)
        },
        modifier = modifier
    )
    
    selectedForPortion?.let { product ->
        AlertDialog(
            onDismissRequest = {
                selectedForPortion = null
                portionError = null
            },
            confirmButton = {
                TextButton(onClick = {
                    val grams = portionInput.replace(',', '.').toDoubleOrNull()?.roundToInt()
                    if (grams == null || grams <= 0) {
                        portionError = invalidPortionMessage
                    } else {
                        component.dispatch(BarcodeScannerIntent.AddToDiary(product, grams))
                        portionError = null
                        selectedForPortion = null
                        portionInput = "100"
                    }
                }) {
                    Text(stringResource(Res.string.add_portion))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    selectedForPortion = null
                    portionError = null
                }) {
                    Text(text = stringResource(Res.string.back))
                }
            },
            title = { Text(text = product.name, style = MaterialTheme.typography.titleMedium) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
                    OutlinedTextField(
                        value = portionInput,
                        onValueChange = { portionInput = it },
                        label = { Text(stringResource(Res.string.portion_grams)) },
                        singleLine = true,
                        colors = TextFieldDefaults.colors()
                    )
                    portionError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalResourceApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun BarcodeScannerContent(
    state: BarcodeScannerUiState,
    component: BarcodeScannerComponent,
    errorMessage: String?,
    onIntent: (BarcodeScannerIntent) -> Unit,
    onAddToDiary: (BarcodeProduct) -> Unit,
    onErrorDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val surfaceGradient = remember {
        Brush.linearGradient(
            listOf(
                colors.background,
                colors.surfaceVariant
            )
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.barcode_scanning_title),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = colors.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = component.onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back),
                            tint = colors.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = colors.onBackground,
                    titleContentColor = colors.onBackground
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(surfaceGradient)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(MaterialTheme.spacing.extraLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Main scanning area
                ScanningArea(
                    isScanning = state.isScanning,
                    isSupported = state.isSupported,
                    onStartScanning = { onIntent(BarcodeScannerIntent.StartScanning) },
                    onStopScanning = { onIntent(BarcodeScannerIntent.StopScanning) },
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

                // Product result area
                ProductResultArea(
                    productResult = state.productResult,
                    isLoading = state.isLoading,
                    onAddToDiary = onAddToDiary,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                // Error display
                ErrorDisplay(
                    error = state.error ?: errorMessage,
                    onDismiss = onErrorDismiss,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Permission request button
                if (state.scanResult is ScanResult.PermissionDenied) {
                    Button(
                        onClick = { component.dispatch(BarcodeScannerIntent.StartScanning) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = MaterialTheme.spacing.large),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = MaterialTheme.elevation.large)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))
                        Text(stringResource(Res.string.start_scanning))
                    }
                }
            }
        }
        
        // Loading overlay
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    strokeWidth = 4.dp,
                    color = colors.primary
                )
            }
        }
    }
}

@Composable
private fun ScanningArea(
    isScanning: Boolean,
    isSupported: Boolean,
    onStartScanning: () -> Unit,
    onStopScanning: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isSupported) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            Text(
                text = "Barcode scanning is not supported on this device",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    } else if (isScanning) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated scanning indicator
            ScanningIndicator()
            
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
            
            Button(
                onClick = onStopScanning,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = MaterialTheme.elevation.medium)
            ) {
                Text(stringResource(Res.string.stop_scanning))
            }
        }
    } else {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))
            
            Button(
                onClick = onStartScanning,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = MaterialTheme.elevation.large)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))
                Text(stringResource(Res.string.start_scanning))
            }
        }
    }
}

@Composable
private fun ScanningIndicator() {
    Box(
        modifier = Modifier
            .size(240.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        PlatformCameraPreview(
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.18f))
        )

        ScanningLines()
    }
}

@Composable
private fun ScanningLines() {
    val transition = rememberInfiniteTransition(label = "scanLineTransition")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanLineProgress"
    )
    val lineHeight = 18.dp

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val travelDistance = maxHeight - lineHeight
        val lineOffset = travelDistance * progress

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(lineHeight)
                .offset(y = lineOffset)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.45f),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .offset(y = lineOffset + 8.dp)
                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.95f))
        )
    }
}

@Composable
private fun ProductResultArea(
    productResult: BarcodeProductSearchResult?,
    isLoading: Boolean,
    onAddToDiary: (BarcodeProduct) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        isLoading -> {
            // Loading indicator (handled by main overlay)
        }
        
        productResult is BarcodeProductSearchResult.Success -> {
            ProductFoundCard(
                product = productResult.product,
                onAddToDiary = onAddToDiary,
                modifier = modifier
            )
        }
        
        productResult is BarcodeProductSearchResult.NotFound -> {
            ProductNotFoundCard(
                barcode = productResult.barcode,
                modifier = modifier
            )
        }
        
        productResult is BarcodeProductSearchResult.Error -> {
            ErrorCard(
                error = productResult.message,
                modifier = modifier
            )
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun ProductFoundCard(
    product: BarcodeProduct,
    onAddToDiary: (BarcodeProduct) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.medium)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            Text(
                text = "✅ ${product.name}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
            
            if (!product.brand.isNullOrBlank()) {
                Text(
                    text = product.brand.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = "Штрих-код: ${product.barcode}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            
            Button(
                onClick = { onAddToDiary(product) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.add_to_diary))
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun ProductNotFoundCard(
    barcode: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.medium)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            Text(
                text = "❌",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.error
            )
            
            Text(
                text = stringResource(Res.string.product_not_found),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Штрих-код: $barcode",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun ErrorCard(
    error: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.medium)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            Text(
                text = "⚠️",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.error
            )
            
            Text(
                text = stringResource(Res.string.scanning_error),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun ErrorDisplay(
    error: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = error != null,
        modifier = modifier,
        enter = fadeIn(animationSpec = tween(300, easing = FastOutSlowInEasing)),
        exit = fadeOut(animationSpec = tween(300, easing = FastOutSlowInEasing))
    ) {
        if (error != null) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.small)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spacing.medium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(Res.string.dismiss))
                    }
                }
            }
        }
    }
}
