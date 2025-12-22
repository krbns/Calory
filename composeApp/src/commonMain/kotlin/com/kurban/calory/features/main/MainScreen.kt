@file:OptIn(ExperimentalResourceApi::class)

package com.kurban.calory.features.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import calory.composeapp.generated.resources.Res
import calory.composeapp.generated.resources.add
import calory.composeapp.generated.resources.add_to_diary
import calory.composeapp.generated.resources.app_title
import calory.composeapp.generated.resources.dismiss
import calory.composeapp.generated.resources.empty_list
import calory.composeapp.generated.resources.food_macros_100
import calory.composeapp.generated.resources.food_macros_entry
import calory.composeapp.generated.resources.grams_total
import calory.composeapp.generated.resources.macro_carb_short
import calory.composeapp.generated.resources.macro_fat_short
import calory.composeapp.generated.resources.macro_protein_short
import calory.composeapp.generated.resources.meals_today
import calory.composeapp.generated.resources.add_option_custom
import calory.composeapp.generated.resources.add_option_search
import calory.composeapp.generated.resources.add_option_title
import calory.composeapp.generated.resources.pick_from_search
import calory.composeapp.generated.resources.portion_grams
import calory.composeapp.generated.resources.profile_open
import calory.composeapp.generated.resources.remove
import calory.composeapp.generated.resources.search_foods
import calory.composeapp.generated.resources.select
import calory.composeapp.generated.resources.subtitle
import calory.composeapp.generated.resources.today
import calory.composeapp.generated.resources.total_consumed
import com.kurban.calory.core.theme.CaloryTheme
import com.kurban.calory.core.theme.elevation
import com.kurban.calory.core.theme.spacing
import com.kurban.calory.features.main.domain.model.Food
import com.kurban.calory.features.profile.domain.model.MacroTargets
import com.kurban.calory.features.main.ui.MainViewModel
import com.kurban.calory.features.main.ui.model.MainEffect
import com.kurban.calory.features.main.ui.model.MainIntent
import com.kurban.calory.features.main.ui.model.MainUiState
import com.kurban.calory.features.main.ui.model.UITrackedFood
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalResourceApi::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onOpenProfile: () -> Unit = {},
    onOpenCustomFoods: () -> Unit = {},
) {
    val viewModel = koinViewModel<MainViewModel>()
    val state by viewModel.uiState.collectAsState()
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is MainEffect.Error -> errorMessage = effect.message
            }
        }
    }

    MainContent(
        state = state,
        errorMessage = errorMessage,
        onQueryChanged = { viewModel.dispatch(MainIntent.QueryChanged(it)) },
        onSelectFood = { viewModel.dispatch(MainIntent.FoodSelected(it)) },
        onGramsChanged = { viewModel.dispatch(MainIntent.GramsChanged(it)) },
        onAddFood = { viewModel.dispatch(MainIntent.AddSelectedFood) },
        onRemoveEntry = { viewModel.dispatch(MainIntent.RemoveEntry(it)) },
        onErrorDismiss = {
            viewModel.dispatch(MainIntent.ClearError)
        },
        onOpenProfile = onOpenProfile,
        onOpenCustomFoods = onOpenCustomFoods,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
private fun MainContent(
    modifier: Modifier = Modifier,
    state: MainUiState,
    errorMessage: String?,
    onQueryChanged: (String) -> Unit,
    onSelectFood: (Food) -> Unit,
    onGramsChanged: (String) -> Unit,
    onAddFood: () -> Unit,
    onRemoveEntry: (Long) -> Unit,
    onErrorDismiss: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenCustomFoods: () -> Unit
) {
    var isOptionsSheetOpen by remember { mutableStateOf(false) }
    var isSearchSheetOpen by remember { mutableStateOf(false) }
    val optionsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val searchSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
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
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { isOptionsSheetOpen = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(stringResource(Res.string.add_to_diary)) }
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
                    .padding(
                        horizontal = MaterialTheme.spacing.extraLarge,
                        vertical = MaterialTheme.spacing.large
                    ),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
            ) {
                Header(onOpenProfile = onOpenProfile)
                SummaryCard(
                    calories = state.totalCalories,
                    proteins = state.totalProteins,
                    fats = state.totalFats,
                    carbs = state.totalCarbs,
                    targets = state.macroTargets
                )
                ConsumptionList(
                    items = state.tracked,
                    onRemove = onRemoveEntry,
                    modifier = Modifier.weight(1f)
                )
            }

            AnimatedVisibility(
                visible = errorMessage != null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(MaterialTheme.spacing.large),
                enter = fadeIn(animationSpec = tween(250, easing = FastOutSlowInEasing)),
                exit = fadeOut(animationSpec = tween(250, easing = FastOutSlowInEasing))
            ) {
                ErrorCard(message = errorMessage.orEmpty(), onDismiss = onErrorDismiss)
            }
        }
    }

    if (isOptionsSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isOptionsSheetOpen = false },
            sheetState = optionsSheetState
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        horizontal = MaterialTheme.spacing.extraLarge,
                        vertical = MaterialTheme.spacing.medium
                    )
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                Text(
                    text = stringResource(Res.string.add_option_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Button(
                    onClick = {
                        scope.launch {
                            optionsSheetState.hide()
                            isOptionsSheetOpen = false
                            isSearchSheetOpen = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(stringResource(Res.string.add_option_search))
                }
                Button(
                    onClick = {
                        scope.launch {
                            optionsSheetState.hide()
                            isOptionsSheetOpen = false
                            onOpenCustomFoods()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text(stringResource(Res.string.add_option_custom))
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            }
        }
    }

    if (isSearchSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isSearchSheetOpen = false },
            sheetState = searchSheetState
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        horizontal = MaterialTheme.spacing.extraLarge,
                        vertical = MaterialTheme.spacing.small
                    )
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                Text(
                    text = stringResource(Res.string.add_to_diary),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                SearchSection(
                    query = state.query,
                    results = state.searchResults,
                    isSearching = state.isSearching,
                    onQueryChanged = onQueryChanged,
                    onSelect = onSelectFood
                )
                SelectionSection(
                    selected = state.selectedFood,
                    gramsInput = state.gramsInput,
                    onGramsChanged = onGramsChanged,
                    onAdd = {
                        onAddFood()
                        scope.launch {
                            searchSheetState.hide()
                            isSearchSheetOpen = false
                        }
                    }
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            }
        }
    }
}

@Composable
private fun Header(onOpenProfile: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.app_title),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = onOpenProfile) {
                Text(stringResource(Res.string.profile_open))
            }
        }
        Text(
            text = stringResource(Res.string.subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun SummaryCard(
    calories: Double,
    proteins: Double,
    fats: Double,
    carbs: Double,
    targets: MacroTargets?,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.large)
    ) {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.extraLarge)) {
            Text(
                text = stringResource(Res.string.today),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(MaterialTheme.spacing.small))
            Text(
                text = buildMacroText(calories, targets?.calories, "ккал"),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(Res.string.total_consumed),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(MaterialTheme.spacing.medium))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MacroPill(
                    label = stringResource(Res.string.macro_protein_short),
                    value = proteins,
                    target = targets?.proteins,
                    color = MaterialTheme.colorScheme.secondary
                )
                MacroPill(
                    label = stringResource(Res.string.macro_fat_short),
                    value = fats,
                    target = targets?.fats,
                    color = MaterialTheme.colorScheme.tertiary
                )
                MacroPill(
                    label = stringResource(Res.string.macro_carb_short),
                    value = carbs,
                    target = targets?.carbs,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun MacroPill(label: String, value: Double, target: Double?, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .defaultMinSize(minWidth = 76.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f))
            .padding(
                horizontal = MaterialTheme.spacing.medium,
                vertical = MaterialTheme.spacing.compact
            )
    ) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = color)
        Text(
            text = buildMacroText(value, target),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

private fun buildMacroText(value: Double, target: Double?, suffix: String = ""): String {
    val valueText = value.roundToOne()
    val targetText = target?.roundToOne()
    return if (targetText != null) {
        if (suffix.isNotBlank()) "$valueText / $targetText $suffix" else "$valueText / $targetText"
    } else {
        if (suffix.isNotBlank()) "$valueText $suffix" else valueText
    }
}

@Composable
private fun SearchSection(
    query: String,
    results: List<Food>,
    isSearching: Boolean,
    onQueryChanged: (String) -> Unit,
    onSelect: (Food) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChanged,
            modifier = Modifier
                .fillMaxWidth(),
            label = { Text(stringResource(Res.string.search_foods)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            ),
            singleLine = true
        )

        val itemsToShow = if (isSearching && results.isEmpty()) emptyList() else results.take(6)
        if (itemsToShow.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                items(itemsToShow) { food ->
                    FoodRow(food = food, onSelect = { onSelect(food) })
                }
            }
        }
    }
}

@Composable
private fun FoodRow(
    food: Food,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = MaterialTheme.spacing.medium,
                    vertical = MaterialTheme.spacing.compact
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(
                        Res.string.food_macros_100,
                        food.calories.roundToOne(),
                        food.proteins.roundToOne(),
                        food.fats.roundToOne(),
                        food.carbs.roundToOne()
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            TextButton(onClick = onSelect) {
                Text(stringResource(Res.string.select))
            }
        }
    }
}

@Composable
private fun SelectionSection(
    selected: Food?,
    gramsInput: String,
    onGramsChanged: (String) -> Unit,
    onAdd: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.medium)
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.compact)
        ) {
            Text(
                text = stringResource(Res.string.add_to_diary),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            if (selected != null) {
                Text(
                    text = selected.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = stringResource(Res.string.pick_from_search),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                OutlinedTextField(
                    value = gramsInput,
                    onValueChange = onGramsChanged,
                    label = { Text(stringResource(Res.string.portion_grams)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Button(
                    onClick = onAdd,
                    enabled = selected != null && gramsInput.isNotBlank(),
                    modifier = Modifier.height(56.dp)
                ) {
                    Text(stringResource(Res.string.add))
                }
            }
        }
    }
}

@Composable
private fun ConsumptionList(
    items: List<UITrackedFood>,
    onRemove: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(MaterialTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.meals_today),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(Res.string.grams_total, items.sumOf { it.grams }),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (items.isEmpty()) {
                Text(
                    text = stringResource(Res.string.empty_list),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                ) {
                    items(items, key = { it.entryId }) { item ->
                        ConsumptionRow(item, onRemove)
                    }
                }
            }
        }
    }
}

@Composable
private fun ConsumptionRow(
    item: UITrackedFood,
    onRemove: (Long) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .padding(MaterialTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${item.grams} г",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(
                    Res.string.food_macros_entry,
                    item.calories.roundToOne(),
                    item.proteins.roundToOne(),
                    item.fats.roundToOne(),
                    item.carbs.roundToOne()
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(onClick = { onRemove(item.entryId) }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(Res.string.remove),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onDismiss: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.errorContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.large)
    ) {
        Row(
            modifier = Modifier
                .padding(
                    horizontal = MaterialTheme.spacing.large,
                    vertical = MaterialTheme.spacing.extraLarge
                )
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onDismiss) {
                Text(
                    stringResource(Res.string.dismiss),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

private fun Double.roundToOne(): String {
    val rounded = (this * 10.0).roundToInt().toDouble() / 10.0
    return if (rounded % 1.0 == 0.0) rounded.toInt().toString() else rounded.toString()
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val previewState = remember {
        MainUiState(
            query = "apple",
            gramsInput = "150",
            searchResults = listOf(
                Food(1, 0, "Banana", 89.0, 1.1, 0.3, 23.0),
                Food(2, 0, "Chicken breast", 165.0, 31.0, 3.6, 0.0),
                Food(3, 0, "Oats", 379.0, 17.0, 7.0, 67.0)
            ),
            tracked = listOf(
                UITrackedFood(1, 1, "Banana", 120, 106.8, 1.3, 0.4, 27.6),
                UITrackedFood(2, 2, "Chicken breast", 200, 330.0, 62.0, 7.2, 0.0)
            ),
            totalCalories = 436.8,
            totalProteins = 63.3,
            totalFats = 7.6,
            totalCarbs = 27.6,
            macroTargets = MacroTargets(
                calories = 2300.0,
                proteins = 140.0,
                fats = 70.0,
                carbs = 260.0
            )
        )
    }

    MainContent(
        state = previewState,
        errorMessage = null,
        onQueryChanged = {},
        onSelectFood = {},
        onGramsChanged = {},
        onAddFood = {},
        onRemoveEntry = {},
        onErrorDismiss = {},
        onOpenProfile = {},
        onOpenCustomFoods = {}
    )
}
