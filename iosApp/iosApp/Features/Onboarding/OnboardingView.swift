import SwiftUI

struct OnboardingView: View {
    @StateObject var viewModel: OnboardingViewModel
    let onFinished: () -> Void

    var body: some View {
        NavigationStack {
            ZStack {
                CaloryGradientBackground()

                ScrollView {
                    VStack(alignment: .leading, spacing: 14) {
                        Text("Добро пожаловать в Calory")
                            .font(.title2.bold())
                            .foregroundStyle(CaloryStyle.onBackground)

                        Text("Заполните профиль, чтобы получить дневные цели по макроэлементам")
                            .font(.subheadline)
                            .foregroundStyle(CaloryStyle.onSurfaceVariant)

                        CaloryCard {
                            profileFields
                        }

                        if let errorMessage = viewModel.errorMessage {
                            Text(errorMessage)
                                .foregroundStyle(CaloryStyle.error)
                                .font(.footnote)
                        }

                        Button {
                            viewModel.save(onSuccess: onFinished)
                        } label: {
                            if viewModel.isSaving {
                                ProgressView().tint(CaloryStyle.onPrimary)
                            } else {
                                Text("Сохранить и продолжить")
                            }
                        }
                        .buttonStyle(CaloryPrimaryButtonStyle())
                        .disabled(viewModel.isSaving)
                    }
                    .padding(16)
                }
            }
            .navigationTitle("Онбординг")
        }
        .tint(CaloryStyle.primary)
    }

    private var profileFields: some View {
        VStack(spacing: 12) {
            CaloryInputField(
                placeholder: "Имя",
                text: $viewModel.form.name,
                capitalization: .words
            )

            Picker("Пол", selection: $viewModel.form.sex) {
                Text("Мужской").tag("MALE")
                Text("Женский").tag("FEMALE")
            }
            .pickerStyle(.segmented)

            CaloryInputField(
                placeholder: "Возраст",
                text: $viewModel.form.age,
                keyboardType: .numberPad,
                capitalization: .never
            )

            CaloryInputField(
                placeholder: "Рост (см)",
                text: $viewModel.form.heightCm,
                keyboardType: .numberPad,
                capitalization: .never
            )

            CaloryInputField(
                placeholder: "Вес (кг)",
                text: $viewModel.form.weightKg,
                keyboardType: .decimalPad,
                capitalization: .never
            )

            Picker("Цель", selection: $viewModel.form.goal) {
                Text("Похудеть").tag("LOSE_WEIGHT")
                Text("Набрать мышечную массу").tag("GAIN_MUSCLE")
            }
            .pickerStyle(.segmented)
        }
    }
}
