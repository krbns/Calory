@file:OptIn(ExperimentalResourceApi::class)

package com.kurban.calory.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import calory.composeapp.generated.resources.Res
import calory.composeapp.generated.resources.profile_age
import calory.composeapp.generated.resources.back
import calory.composeapp.generated.resources.profile_goal
import calory.composeapp.generated.resources.profile_goal_gain
import calory.composeapp.generated.resources.profile_goal_lose
import calory.composeapp.generated.resources.profile_height
import calory.composeapp.generated.resources.profile_save
import calory.composeapp.generated.resources.profile_saved
import calory.composeapp.generated.resources.profile_sex
import calory.composeapp.generated.resources.profile_sex_female
import calory.composeapp.generated.resources.profile_sex_male
import calory.composeapp.generated.resources.profile_title
import calory.composeapp.generated.resources.profile_weight
import calory.composeapp.generated.resources.profile_name
import calory.composeapp.generated.resources.unit_cm
import calory.composeapp.generated.resources.unit_kg
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kurban.calory.core.theme.elevation
import com.kurban.calory.core.theme.spacing
import com.kurban.calory.features.profile.domain.model.UserGoal
import com.kurban.calory.features.profile.domain.model.UserSex
import com.kurban.calory.features.profile.ui.ProfileComponent
import com.kurban.calory.features.profile.ui.model.ProfileEffect
import com.kurban.calory.features.profile.ui.model.ProfileIntent
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun ProfileScreen(
    component: ProfileComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.state.subscribeAsState()
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(component) {
        component.effects.collect { effect ->
            when (effect) {
                is ProfileEffect.Error -> errorMessage = effect.message
            }
        }
    }

    val colors = MaterialTheme.colorScheme
    val surfaceGradient = Brush.linearGradient(
        listOf(
            colors.background,
            colors.surfaceVariant
        )
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent
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
                        vertical = MaterialTheme.spacing.small
                    ),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = component.onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Text(
                        text = stringResource(Res.string.profile_title),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                ProfileCard {
                    OutlinedTextField(
                        value = state.nameInput,
                        onValueChange = { component.dispatch(ProfileIntent.NameChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text(stringResource(Res.string.profile_name)) }
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        ProfileSectionTitle(
                            text = stringResource(Res.string.profile_sex),
                            bottomSpacing = 0.dp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
                            FilterChip(
                                selected = state.sex == UserSex.MALE,
                                onClick = { component.dispatch(ProfileIntent.SexSelected(UserSex.MALE)) },
                                label = { Text(stringResource(Res.string.profile_sex_male)) }
                            )
                            FilterChip(
                                selected = state.sex == UserSex.FEMALE,
                                onClick = { component.dispatch(ProfileIntent.SexSelected(UserSex.FEMALE)) },
                                label = { Text(stringResource(Res.string.profile_sex_female)) }
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        ProfileSectionTitle(
                            text = stringResource(Res.string.profile_goal),
                            bottomSpacing = 0.dp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
                            FilterChip(
                                selected = state.goal == UserGoal.GAIN_MUSCLE,
                                onClick = { component.dispatch(ProfileIntent.GoalSelected(UserGoal.GAIN_MUSCLE)) },
                                label = { Text(stringResource(Res.string.profile_goal_gain)) }
                            )
                            FilterChip(
                                selected = state.goal == UserGoal.LOSE_WEIGHT,
                                onClick = { component.dispatch(ProfileIntent.GoalSelected(UserGoal.LOSE_WEIGHT)) },
                                label = { Text(stringResource(Res.string.profile_goal_lose)) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = state.ageInput,
                        onValueChange = { component.dispatch(ProfileIntent.AgeChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text(stringResource(Res.string.profile_age)) }
                    )

                    OutlinedTextField(
                        value = state.heightInput,
                        onValueChange = { component.dispatch(ProfileIntent.HeightChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text(stringResource(Res.string.profile_height)) },
                        suffix = if (state.heightInput.isNotBlank()) {
                            { Text(stringResource(Res.string.unit_cm)) }
                        } else {
                            null
                        }
                    )

                    OutlinedTextField(
                        value = state.weightInput,
                        onValueChange = { component.dispatch(ProfileIntent.WeightChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text(stringResource(Res.string.profile_weight)) },
                        suffix = if (state.weightInput.isNotBlank()) {
                            { Text(stringResource(Res.string.unit_kg)) }
                        } else {
                            null
                        }
                    )
                }

                Button(
                    onClick = { component.dispatch(ProfileIntent.Save) },
                    enabled = !state.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(stringResource(Res.string.profile_save))
                }

                if (state.saved) {
                    Text(
                        text = stringResource(Res.string.profile_saved),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.wrapContentWidth()
                    )
                }

                val message = errorMessage ?: state.errorMessage
                if (message != null) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.large)
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
        ) {
            content()
        }
    }
}

@Composable
private fun ProfileSectionTitle(
    text: String,
    bottomSpacing: Dp = MaterialTheme.spacing.extraSmall
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(bottomSpacing))
}
