@file:OptIn(ExperimentalResourceApi::class)

package com.kurban.calory.features.customfood

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import calory.composeapp.generated.resources.Res
import calory.composeapp.generated.resources.add
import calory.composeapp.generated.resources.add_portion
import calory.composeapp.generated.resources.back
import calory.composeapp.generated.resources.custom_food_added_to_diary
import calory.composeapp.generated.resources.custom_food_calories
import calory.composeapp.generated.resources.custom_food_carbs
import calory.composeapp.generated.resources.custom_food_created
import calory.composeapp.generated.resources.custom_food_fats
import calory.composeapp.generated.resources.custom_food_name
import calory.composeapp.generated.resources.custom_food_proteins
import calory.composeapp.generated.resources.custom_food_sheet_title
import calory.composeapp.generated.resources.custom_foods_empty
import calory.composeapp.generated.resources.custom_foods_title
import calory.composeapp.generated.resources.food_macros_100
import calory.composeapp.generated.resources.per_100g_hint
import calory.composeapp.generated.resources.portion_invalid_grams
import calory.composeapp.generated.resources.portion_grams
import calory.composeapp.generated.resources.search_foods
import calory.composeapp.generated.resources.select
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kurban.calory.core.format.roundToOne
import com.kurban.calory.core.theme.elevation
import com.kurban.calory.core.theme.spacing
import com.kurban.calory.features.customfood.domain.model.CustomFood
import com.kurban.calory.features.customfood.ui.CustomFoodComponent
import com.kurban.calory.features.customfood.ui.model.CustomFoodEffect
import com.kurban.calory.features.customfood.ui.model.CustomFoodIntent
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomFoodScreen(
    component: CustomFoodComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.state.subscribeAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var isCreateSheetOpen by remember { mutableStateOf(false) }
    val createSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedForPortion by remember { mutableStateOf<CustomFood?>(null) }
    var portionInput by rememberSaveable { mutableStateOf("100") }
    var portionError by remember { mutableStateOf<String?>(null) }
    var nameInput by rememberSaveable { mutableStateOf("") }
    var caloriesInput by rememberSaveable { mutableStateOf("") }
    var proteinsInput by rememberSaveable { mutableStateOf("") }
    var fatsInput by rememberSaveable { mutableStateOf("") }
    var carbsInput by rememberSaveable { mutableStateOf("") }
    val invalidPortionMessage = stringResource(Res.string.portion_invalid_grams)

    LaunchedEffect(component) {
        component.effects.collect { effect ->
            when (effect) {
                is CustomFoodEffect.Error -> snackbarHostState.showSnackbar(effect.message)
                is CustomFoodEffect.FoodCreated -> {
                    snackbarHostState.showSnackbar(
                        getString(Res.string.custom_food_created, effect.name)
                    )
                    nameInput = ""
                    caloriesInput = ""
                    proteinsInput = ""
                    fatsInput = ""
                    carbsInput = ""
                    isCreateSheetOpen = false
                }

                is CustomFoodEffect.AddedToDiary -> {
                    snackbarHostState.showSnackbar(
                        getString(Res.string.custom_food_added_to_diary, effect.name)
                    )
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = MaterialTheme.spacing.large,
                        vertical = MaterialTheme.spacing.medium
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = component.onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.back)
                    )
                }
                Text(
                    text = stringResource(Res.string.custom_foods_title),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = MaterialTheme.spacing.small)
                )
            }
        },
        floatingActionButton = {
            FilledTonalButton(
                onClick = { isCreateSheetOpen = true },
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.padding(end = MaterialTheme.spacing.compact)
                )
                Text(text = stringResource(Res.string.add))
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(
                    horizontal = MaterialTheme.spacing.extraLarge,
                    vertical = MaterialTheme.spacing.medium
                ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
        ) {
            SearchBar(
                query = state.query,
                onQueryChanged = { component.dispatch(CustomFoodIntent.QueryChanged(it)) }
            )
            state.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.small)
                )
            }

            if (state.filteredFoods.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = MaterialTheme.spacing.large),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(Res.string.custom_foods_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
                ) {
                    items(state.filteredFoods, key = { it.id }) { food ->
                        CustomFoodRow(
                            food = food,
                            onAddClick = { selectedForPortion = food }
                        )
                    }
                }
            }
        }
    }

    if (isCreateSheetOpen) {
        ModalBottomSheet(
            sheetState = createSheetState,
            onDismissRequest = {
                isCreateSheetOpen = false
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = MaterialTheme.spacing.extraLarge,
                        vertical = MaterialTheme.spacing.medium
                    ),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                Text(
                    text = stringResource(Res.string.custom_food_sheet_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text(stringResource(Res.string.custom_food_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                MacroInputRow(
                    calories = caloriesInput,
                    proteins = proteinsInput,
                    fats = fatsInput,
                    carbs = carbsInput,
                    onCaloriesChange = { caloriesInput = it },
                    onProteinsChange = { proteinsInput = it },
                    onFatsChange = { fatsInput = it },
                    onCarbsChange = { carbsInput = it }
                )
                Text(
                    text = stringResource(Res.string.per_100g_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = {
                        component.dispatch(
                            CustomFoodIntent.CreateFood(
                                name = nameInput,
                                calories = caloriesInput,
                                proteins = proteinsInput,
                                fats = fatsInput,
                                carbs = carbsInput
                            )
                        )
                    },
                    enabled = !state.isSaving && nameInput.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = MaterialTheme.spacing.small)
                ) {
                    Text(text = stringResource(Res.string.add))
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            }
        }
    }

    selectedForPortion?.let { food ->
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
                        component.dispatch(CustomFoodIntent.AddToDiary(food.id, grams))
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
            title = { Text(text = food.name, style = MaterialTheme.typography.titleMedium) },
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

@Composable
private fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(stringResource(Res.string.search_foods)) },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun CustomFoodRow(
    food: CustomFood,
    onAddClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.small)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = MaterialTheme.spacing.large,
                    vertical = MaterialTheme.spacing.medium
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.compact)
            ) {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
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
            TextButton(onClick = onAddClick) {
                Text(text = stringResource(Res.string.select))
            }
        }
    }
}

@Composable
private fun MacroInputRow(
    calories: String,
    proteins: String,
    fats: String,
    carbs: String,
    onCaloriesChange: (String) -> Unit,
    onProteinsChange: (String) -> Unit,
    onFatsChange: (String) -> Unit,
    onCarbsChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
        OutlinedTextField(
            value = calories,
            onValueChange = onCaloriesChange,
            label = { Text(stringResource(Res.string.custom_food_calories)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            OutlinedTextField(
                value = proteins,
                onValueChange = onProteinsChange,
                label = { Text(stringResource(Res.string.custom_food_proteins)) },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = fats,
                onValueChange = onFatsChange,
                label = { Text(stringResource(Res.string.custom_food_fats)) },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = carbs,
                onValueChange = onCarbsChange,
                label = { Text(stringResource(Res.string.custom_food_carbs)) },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }
    }
}
