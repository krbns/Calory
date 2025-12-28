@file:OptIn(ExperimentalMaterial3Api::class)

package com.kurban.calory.features.onboarding

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import calory.composeapp.generated.resources.Res
import calory.composeapp.generated.resources.back
import calory.composeapp.generated.resources.onboarding_finish
import calory.composeapp.generated.resources.onboarding_next
import calory.composeapp.generated.resources.onboarding_step_body
import calory.composeapp.generated.resources.onboarding_step_goal
import calory.composeapp.generated.resources.onboarding_step_name
import calory.composeapp.generated.resources.onboarding_subtitle
import calory.composeapp.generated.resources.onboarding_title
import calory.composeapp.generated.resources.profile_age
import calory.composeapp.generated.resources.profile_goal_gain
import calory.composeapp.generated.resources.profile_goal_lose
import calory.composeapp.generated.resources.profile_height
import calory.composeapp.generated.resources.profile_name
import calory.composeapp.generated.resources.profile_sex_female
import calory.composeapp.generated.resources.profile_sex_male
import calory.composeapp.generated.resources.profile_weight
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kurban.calory.core.theme.spacing
import com.kurban.calory.features.onboarding.ui.OnboardingComponent
import com.kurban.calory.features.onboarding.ui.model.OnboardingEffect
import com.kurban.calory.features.onboarding.ui.model.OnboardingIntent
import com.kurban.calory.features.onboarding.ui.model.OnboardingStep
import com.kurban.calory.features.profile.domain.model.UserGoal
import com.kurban.calory.features.profile.domain.model.UserSex
import org.jetbrains.compose.resources.stringResource

@Composable
fun OnboardingScreen(
    component: OnboardingComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.subscribeAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(component) {
        component.effects.collect { effect ->
            when (effect) {
                is OnboardingEffect.Error -> snackbarHostState.showSnackbar(effect.message)
                OnboardingEffect.Completed -> component.onFinished()
            }
        }
    }

    val colors = MaterialTheme.colorScheme
    val gradient = Brush.linearGradient(
        listOf(colors.background, colors.surfaceVariant)
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(padding)
                .padding(horizontal = MaterialTheme.spacing.extraLarge, vertical = MaterialTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            Text(
                text = stringResource(Res.string.onboarding_title),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.onBackground
            )
            Text(
                text = stringResource(Res.string.onboarding_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant
            )

            StepIndicator(current = state.step)

            when (state.step) {
                OnboardingStep.NameAge -> NameAgeStep(
                    name = state.nameInput,
                    age = state.ageInput,
                    onNameChange = { component.dispatch(OnboardingIntent.NameChanged(it)) },
                    onAgeChange = { component.dispatch(OnboardingIntent.AgeChanged(it)) }
                )

                OnboardingStep.Body -> BodyStep(
                    sex = state.sex,
                    height = state.heightInput,
                    onSexSelected = { component.dispatch(OnboardingIntent.SexSelected(it)) },
                    onHeightChange = { component.dispatch(OnboardingIntent.HeightChanged(it)) }
                )

                OnboardingStep.Goal -> GoalStep(
                    goal = state.goal,
                    weight = state.weightInput,
                    onGoalSelected = { component.dispatch(OnboardingIntent.GoalSelected(it)) },
                    onWeightChange = { component.dispatch(OnboardingIntent.WeightChanged(it)) }
                )
            }

            state.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = colors.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                if (state.step != OnboardingStep.NameAge) {
                    OutlinedButton(
                        onClick = { component.dispatch(OnboardingIntent.Back) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = stringResource(Res.string.back))
                    }
                }
                val primaryModifier = if (state.step == OnboardingStep.NameAge) Modifier.fillMaxWidth() else Modifier.weight(1f)

                Button(
                    onClick = {
                        if (state.step == OnboardingStep.Goal) {
                            component.dispatch(OnboardingIntent.Submit)
                        } else {
                            component.dispatch(OnboardingIntent.Next)
                        }
                    },
                    enabled = !state.isSaving,
                    shape = RoundedCornerShape(18.dp),
                    modifier = primaryModifier,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primaryContainer,
                        contentColor = colors.onPrimaryContainer
                    )
                ) {
                    Text(
                        text = if (state.step == OnboardingStep.Goal) {
                            stringResource(Res.string.onboarding_finish)
                        } else {
                            stringResource(Res.string.onboarding_next)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StepIndicator(current: OnboardingStep) {
    val steps = listOf(OnboardingStep.NameAge, OnboardingStep.Body, OnboardingStep.Goal)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.small),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        steps.forEach { step ->
            val active = step.ordinal <= current.ordinal
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(
                        if (active) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
            )
        }
    }
}

@Composable
private fun NameAgeStep(
    name: String,
    age: String,
    onNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        Text(
            text = stringResource(Res.string.onboarding_step_name),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(stringResource(Res.string.profile_name)) }
        )
        OutlinedTextField(
            value = age,
            onValueChange = onAgeChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(stringResource(Res.string.profile_age)) }
        )
    }
}

@Composable
private fun BodyStep(
    sex: UserSex,
    height: String,
    onSexSelected: (UserSex) -> Unit,
    onHeightChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
        Text(
            text = stringResource(Res.string.onboarding_step_body),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
            FilterChip(
                selected = sex == UserSex.MALE,
                onClick = { onSexSelected(UserSex.MALE) },
                label = { Text(stringResource(Res.string.profile_sex_male)) }
            )
            FilterChip(
                selected = sex == UserSex.FEMALE,
                onClick = { onSexSelected(UserSex.FEMALE) },
                label = { Text(stringResource(Res.string.profile_sex_female)) }
            )
        }
        OutlinedTextField(
            value = height,
            onValueChange = onHeightChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(stringResource(Res.string.profile_height)) }
        )
    }
}

@Composable
private fun GoalStep(
    goal: UserGoal,
    weight: String,
    onGoalSelected: (UserGoal) -> Unit,
    onWeightChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
        Text(
            text = stringResource(Res.string.onboarding_step_goal),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
            FilterChip(
                selected = goal == UserGoal.GAIN_MUSCLE,
                onClick = { onGoalSelected(UserGoal.GAIN_MUSCLE) },
                label = { Text(stringResource(Res.string.profile_goal_gain)) }
            )
            FilterChip(
                selected = goal == UserGoal.LOSE_WEIGHT,
                onClick = { onGoalSelected(UserGoal.LOSE_WEIGHT) },
                label = { Text(stringResource(Res.string.profile_goal_lose)) }
            )
        }
        OutlinedTextField(
            value = weight,
            onValueChange = onWeightChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(stringResource(Res.string.profile_weight)) }
        )
    }
}
