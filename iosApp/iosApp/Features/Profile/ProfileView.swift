import SwiftUI

struct ProfileView: View {
    @StateObject var viewModel: ProfileViewModel
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        ZStack {
            CaloryGradientBackground()

            ScrollView {
                VStack(spacing: 14) {
                    CaloryCard {
                        profileFields
                    }

                    if let errorMessage = viewModel.errorMessage {
                        Text(errorMessage)
                            .foregroundStyle(CaloryStyle.error)
                            .font(.footnote)
                            .frame(maxWidth: .infinity, alignment: .leading)
                    }

                    Button {
                        viewModel.save()
                    } label: {
                        if viewModel.isSaving {
                            ProgressView().tint(CaloryStyle.onPrimary)
                        } else {
                            Text("Сохранить")
                        }
                    }
                    .buttonStyle(CaloryPrimaryButtonStyle())
                    .disabled(viewModel.isSaving)
                }
                .padding(16)
            }
        }
        .navigationTitle("Профиль")
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button("Готово") { dismiss() }
                    .foregroundStyle(CaloryStyle.onBackground)
            }
        }
        .task { viewModel.start() }
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
