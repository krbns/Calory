import SwiftUI
import Shared

struct MainView: View {
    @StateObject var viewModel: MainViewModel
    let profileBridge: ProfileBridge
    let customFoodBridge: CustomFoodBridge

    @State private var showProfile = false
    @State private var showCustomFood = false

    var body: some View {
        NavigationStack {
            ZStack {
                CaloryGradientBackground()

                ScrollView {
                    VStack(spacing: 14) {
                        summaryCard
                        searchCard
                        diaryCard
                        barcodeCard
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 12)
                }
            }
            .navigationTitle("Calory")
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button("Профиль") { showProfile = true }
                        .foregroundStyle(CaloryStyle.onBackground)
                }
                ToolbarItem(placement: .topBarTrailing) {
                    Button("Свои") { showCustomFood = true }
                        .foregroundStyle(CaloryStyle.onBackground)
                }
            }
            .sheet(isPresented: $showProfile) {
                NavigationStack {
                    ProfileView(viewModel: ProfileViewModel(bridge: profileBridge))
                }
            }
            .sheet(isPresented: $showCustomFood) {
                NavigationStack {
                    CustomFoodView(viewModel: CustomFoodViewModel(bridge: customFoodBridge))
                }
            }
            .task { viewModel.start() }
            .overlay {
                if viewModel.isLoading {
                    ProgressView()
                        .tint(CaloryStyle.primary)
                }
            }
            .alert("Ошибка", isPresented: Binding(
                get: { viewModel.errorMessage != nil },
                set: { if !$0 { viewModel.errorMessage = nil } }
            )) {
                Button("ОК", role: .cancel) {}
            } message: {
                Text(viewModel.errorMessage ?? "")
            }
        }
        .tint(CaloryStyle.primary)
    }

    private var summaryCard: some View {
        CaloryCard {
            Text("Сегодня")
                .font(.headline)
                .foregroundStyle(CaloryStyle.onSurface)
            Text(viewModel.dayId)
                .font(.caption)
                .foregroundStyle(CaloryStyle.onSurfaceVariant)

            macroRow("Калории", viewModel.totals.calories, viewModel.targets?.calories, CaloryStyle.secondary)
            macroRow("Белки", viewModel.totals.proteins, viewModel.targets?.proteins, CaloryStyle.primary)
            macroRow("Жиры", viewModel.totals.fats, viewModel.targets?.fats, CaloryStyle.tertiary)
            macroRow("Углеводы", viewModel.totals.carbs, viewModel.targets?.carbs, CaloryStyle.secondary)
        }
    }

    private var searchCard: some View {
        CaloryCard {
            Text("Добавить в дневник")
                .font(.headline)
                .foregroundStyle(CaloryStyle.onSurface)

            CaloryInputField(
                placeholder: "Поиск продукта",
                text: $viewModel.query,
                capitalization: .never
            )

            CaloryInputField(
                placeholder: "Порция, граммы",
                text: $viewModel.gramsInput,
                keyboardType: .numberPad,
                capitalization: .never
            )

            Button("Найти") {
                viewModel.search()
            }
            .buttonStyle(CaloryPrimaryButtonStyle())

            ForEach(viewModel.searchResults) { item in
                VStack(alignment: .leading, spacing: 8) {
                    Text(item.name)
                        .foregroundStyle(CaloryStyle.onSurface)
                        .font(.headline)
                    Text("ккал \(Int(item.calories)) • Б \(Int(item.proteins)) Ж \(Int(item.fats)) У \(Int(item.carbs))")
                        .font(.caption)
                        .foregroundStyle(CaloryStyle.onSurfaceVariant)
                    Button("Добавить в дневник") {
                        viewModel.addFood(item)
                    }
                    .buttonStyle(CaloryPrimaryButtonStyle())
                }
                .padding(12)
                .background(CaloryStyle.surfaceVariant.opacity(0.45), in: RoundedRectangle(cornerRadius: 12))
            }
        }
    }

    private var diaryCard: some View {
        CaloryCard {
            Text("Приемы пищи")
                .font(.headline)
                .foregroundStyle(CaloryStyle.onSurface)

            if viewModel.trackedFoods.isEmpty {
                Text("Пока нет записей")
                    .foregroundStyle(CaloryStyle.onSurfaceVariant)
            }

            ForEach(viewModel.trackedFoods) { item in
                HStack(alignment: .top, spacing: 10) {
                    VStack(alignment: .leading, spacing: 5) {
                        Text(item.name)
                            .foregroundStyle(CaloryStyle.onSurface)
                            .font(.subheadline.weight(.semibold))
                        Text("\(item.grams) г • ккал \(Int(item.calories)) • Б \(Int(item.proteins)) Ж \(Int(item.fats)) У \(Int(item.carbs))")
                            .font(.caption)
                            .foregroundStyle(CaloryStyle.onSurfaceVariant)
                    }
                    Spacer()
                    Button(role: .destructive) {
                        viewModel.deleteTrackedFood(id: item.id)
                    } label: {
                        Image(systemName: "trash")
                    }
                }
                .padding(12)
                .background(CaloryStyle.surfaceVariant.opacity(0.45), in: RoundedRectangle(cornerRadius: 12))
            }
        }
    }

    private var barcodeCard: some View {
        CaloryCard {
            HStack {
                Image(systemName: "qrcode.viewfinder")
                    .foregroundStyle(CaloryStyle.onSurfaceVariant)
                Text("Сканировать штрихкод")
                    .foregroundStyle(CaloryStyle.onSurface)
                Spacer()
                Text("Скоро")
                    .font(.caption)
                    .foregroundStyle(CaloryStyle.onSurfaceVariant)
            }
            .opacity(0.8)
        }
    }

    private func macroRow(_ title: String, _ current: Double, _ target: Double?, _ color: Color) -> some View {
        HStack {
            Circle().fill(color).frame(width: 8, height: 8)
            Text(title)
                .foregroundStyle(CaloryStyle.onSurface)
            Spacer()
            if let target {
                Text("\(Int(current)) / \(Int(target))")
                    .foregroundStyle(CaloryStyle.onSurfaceVariant)
            } else {
                Text("\(Int(current))")
                    .foregroundStyle(CaloryStyle.onSurfaceVariant)
            }
        }
    }
}
