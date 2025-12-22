@file:OptIn(ExperimentalResourceApi::class)

package com.kurban.calory.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
import com.kurban.calory.core.theme.elevation
import com.kurban.calory.core.theme.spacing
import com.kurban.calory.features.profile.domain.model.UserGoal
import com.kurban.calory.features.profile.domain.model.UserSex
import com.kurban.calory.features.profile.ui.ProfileViewModel
import com.kurban.calory.features.profile.ui.model.ProfileEffect
import com.kurban.calory.features.profile.ui.model.ProfileIntent
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    val viewModel = koinViewModel<ProfileViewModel>()
    val state by viewModel.uiState.collectAsState()
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.profile_title),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(Res.string.back),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(MaterialTheme.spacing.small)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(50)
                            )
                            .padding(
                                horizontal = MaterialTheme.spacing.medium,
                                vertical = MaterialTheme.spacing.extraSmall
                            )
                            .clickable { onBack() }
                    )
                }
                ProfileCard {
                    ProfileSectionTitle(stringResource(Res.string.profile_sex))
                    Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
                        FilterChip(
                            selected = state.sex == UserSex.MALE,
                            onClick = { viewModel.dispatch(ProfileIntent.SexSelected(UserSex.MALE)) },
                            label = { Text(stringResource(Res.string.profile_sex_male)) }
                        )
                        FilterChip(
                            selected = state.sex == UserSex.FEMALE,
                            onClick = { viewModel.dispatch(ProfileIntent.SexSelected(UserSex.FEMALE)) },
                            label = { Text(stringResource(Res.string.profile_sex_female)) }
                        )
                    }

                    ProfileSectionTitle(stringResource(Res.string.profile_goal))
                    Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
                        FilterChip(
                            selected = state.goal == UserGoal.GAIN_MUSCLE,
                            onClick = { viewModel.dispatch(ProfileIntent.GoalSelected(UserGoal.GAIN_MUSCLE)) },
                            label = { Text(stringResource(Res.string.profile_goal_gain)) }
                        )
                        FilterChip(
                            selected = state.goal == UserGoal.LOSE_WEIGHT,
                            onClick = { viewModel.dispatch(ProfileIntent.GoalSelected(UserGoal.LOSE_WEIGHT)) },
                            label = { Text(stringResource(Res.string.profile_goal_lose)) }
                        )
                    }

                    ProfileSectionTitle(stringResource(Res.string.profile_age))
                    OutlinedTextField(
                        value = state.ageInput,
                        onValueChange = { viewModel.dispatch(ProfileIntent.AgeChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    ProfileSectionTitle(stringResource(Res.string.profile_height))
                    OutlinedTextField(
                        value = state.heightInput,
                        onValueChange = { viewModel.dispatch(ProfileIntent.HeightChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    ProfileSectionTitle(stringResource(Res.string.profile_weight))
                    OutlinedTextField(
                        value = state.weightInput,
                        onValueChange = { viewModel.dispatch(ProfileIntent.WeightChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                Button(
                    onClick = { viewModel.dispatch(ProfileIntent.Save) },
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
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
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
private fun ProfileSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraSmall))
}
