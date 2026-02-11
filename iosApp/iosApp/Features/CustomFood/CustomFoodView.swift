import SwiftUI

struct CustomFoodView: View {
    @StateObject var viewModel: CustomFoodViewModel
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        ZStack {
            CaloryGradientBackground()

            ScrollView {
                VStack(spacing: 14) {
                    CaloryCard {
                        Text("Создать свой продукт")
                            .font(.headline)
                            .foregroundStyle(CaloryStyle.onSurface)

                        CaloryInputField(
                            placeholder: "Название",
                            text: $viewModel.name,
                            capitalization: .words
                        )

                        macroField("Калории /100г", text: $viewModel.calories)
                        macroField("Белки /100г", text: $viewModel.proteins)
                        macroField("Жиры /100г", text: $viewModel.fats)
                        macroField("Углеводы /100г", text: $viewModel.carbs)

                        Button {
                            viewModel.createFood()
                        } label: {
                            if viewModel.isSaving {
                                ProgressView().tint(CaloryStyle.onPrimary)
                            } else {
                                Text("Создать")
                            }
                        }
                        .buttonStyle(CaloryPrimaryButtonStyle())
                        .disabled(viewModel.isSaving)
                    }

                    CaloryCard {
                        Text("Свои продукты")
                            .font(.headline)
                            .foregroundStyle(CaloryStyle.onSurface)

                        CaloryInputField(
                            placeholder: "Порция, граммы",
                            text: $viewModel.gramsToAdd,
                            keyboardType: .numberPad,
                            capitalization: .never
                        )

                        if viewModel.foods.isEmpty {
                            Text("Пока нет своих продуктов")
                                .foregroundStyle(CaloryStyle.onSurfaceVariant)
                        }

                        ForEach(viewModel.foods) { item in
                            VStack(alignment: .leading, spacing: 8) {
                                Text(item.name)
                                    .font(.headline)
                                    .foregroundStyle(CaloryStyle.onSurface)
                                Text("ккал \(Int(item.calories)) • Б \(Int(item.proteins)) Ж \(Int(item.fats)) У \(Int(item.carbs))")
                                    .font(.caption)
                                    .foregroundStyle(CaloryStyle.onSurfaceVariant)
                                Button("Добавить в дневник") {
                                    viewModel.addToDiary(foodId: item.id)
                                }
                                .buttonStyle(CaloryPrimaryButtonStyle())
                            }
                            .padding(12)
                            .background(CaloryStyle.surfaceVariant.opacity(0.45), in: RoundedRectangle(cornerRadius: 12))
                        }
                    }

                    if let errorMessage = viewModel.errorMessage {
                        Text(errorMessage)
                            .foregroundStyle(CaloryStyle.error)
                            .font(.footnote)
                            .frame(maxWidth: .infinity, alignment: .leading)
                    }
                }
                .padding(16)
            }
        }
        .navigationTitle("Свои продукты")
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button("Готово") { dismiss() }
                    .foregroundStyle(CaloryStyle.onBackground)
            }
        }
        .task { viewModel.start() }
        .tint(CaloryStyle.primary)
    }

    private func macroField(_ title: String, text: Binding<String>) -> some View {
        CaloryInputField(
            placeholder: title,
            text: text,
            keyboardType: .decimalPad,
            capitalization: .never
        )
    }
}
