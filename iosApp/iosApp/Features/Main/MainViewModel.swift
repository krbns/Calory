import Foundation
import Shared

@MainActor
final class MainViewModel: ObservableObject {
    @Published var trackedFoods: [MainTrackedFoodItem] = []
    @Published var totals: MainTotals = .empty
    @Published var targets: MacroTargetsViewData?
    @Published var dayId: String = ""
    @Published var query: String = ""
    @Published var gramsInput: String = "100"
    @Published var searchResults: [FoodSearchItem] = []
    @Published var isLoading: Bool = false
    @Published var errorMessage: String?
    @Published var barcodeAvailable: Bool = false

    private let bridge: MainBridge
    private var stateHandle: DisposableHandle?

    init(bridge: MainBridge) {
        self.bridge = bridge
    }

    deinit {
        stateHandle?.dispose()
    }

    func start() {
        if !dayId.isEmpty {
            return
        }

        dayId = bridge.currentDayId()
        subscribe(dayId: dayId)
    }

    func search() {
        let trimmed = query.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else {
            searchResults = []
            return
        }

        bridge.searchFoods(
            query: trimmed,
            onSuccess: { items in
                let foods = asIosFoodList(items)
                DispatchQueue.main.async {
                    self.searchResults = foods.map { $0.toViewData() }
                }
                return
            },
            onError: { message in
                DispatchQueue.main.async {
                    self.errorMessage = message
                }
                return
            }
        )
    }

    func addFood(_ item: FoodSearchItem) {
        guard let grams = Int32(gramsInput), grams > 0 else {
            errorMessage = "Введите корректный вес в граммах"
            return
        }

        isLoading = true
        bridge.addTrackedFood(
            foodName: item.name,
            grams: grams,
            onSuccess: { _ in
                DispatchQueue.main.async {
                    self.isLoading = false
                    self.query = ""
                    self.searchResults = []
                }
                return
            },
            onError: { message in
                DispatchQueue.main.async {
                    self.isLoading = false
                    self.errorMessage = message
                }
                return
            }
        )
    }

    func deleteTrackedFood(id: Int64) {
        bridge.deleteTrackedFood(
            entryId: id,
            onSuccess: {
                return
            },
            onError: { message in
                DispatchQueue.main.async {
                    self.errorMessage = message
                }
                return
            }
        )
    }

    private func subscribe(dayId: String) {
        stateHandle?.dispose()
        stateHandle = bridge.observeMainState(
            dayId: dayId,
            onEach: { state in
                DispatchQueue.main.async {
                    self.trackedFoods = asIosTrackedFoodList(state.trackedFoods).map { $0.toViewData() }
                    self.totals = MainTotals(
                        calories: state.totals.calories,
                        proteins: state.totals.proteins,
                        fats: state.totals.fats,
                        carbs: state.totals.carbs
                    )
                    if let targets = state.macroTargets {
                        self.targets = MacroTargetsViewData(
                            calories: targets.calories,
                            proteins: targets.proteins,
                            fats: targets.fats,
                            carbs: targets.carbs
                        )
                    } else {
                        self.targets = nil
                    }
                    self.barcodeAvailable = state.barcodeAvailable
                }
                return
            },
            onError: { message in
                DispatchQueue.main.async {
                    self.errorMessage = message
                }
                return
            }
        )
    }
}
