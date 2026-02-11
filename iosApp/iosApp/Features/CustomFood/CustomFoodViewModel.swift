import Foundation
import Shared

@MainActor
final class CustomFoodViewModel: ObservableObject {
    @Published var foods: [CustomFoodItemViewData] = []
    @Published var name: String = ""
    @Published var calories: String = ""
    @Published var proteins: String = ""
    @Published var fats: String = ""
    @Published var carbs: String = ""
    @Published var gramsToAdd: String = "100"
    @Published var isSaving: Bool = false
    @Published var errorMessage: String?

    private let bridge: CustomFoodBridge
    private var handle: DisposableHandle?

    init(bridge: CustomFoodBridge) {
        self.bridge = bridge
    }

    deinit {
        handle?.dispose()
    }

    func start() {
        if handle != nil {
            return
        }

        handle = bridge.observeFoods(
            onEach: { items in
                let mapped = asIosCustomFoodList(items).map { $0.toViewData() }
                DispatchQueue.main.async {
                    self.foods = mapped
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

    func createFood() {
        guard
            let caloriesValue = Double(calories),
            let proteinsValue = Double(proteins),
            let fatsValue = Double(fats),
            let carbsValue = Double(carbs)
        else {
            errorMessage = "Введите корректные числовые значения"
            return
        }

        isSaving = true
        errorMessage = nil

        bridge.createFood(
            name: name,
            calories: caloriesValue,
            proteins: proteinsValue,
            fats: fatsValue,
            carbs: carbsValue,
            onSuccess: { _ in
                DispatchQueue.main.async {
                    self.isSaving = false
                    self.name = ""
                    self.calories = ""
                    self.proteins = ""
                    self.fats = ""
                    self.carbs = ""
                }
                return
            },
            onError: { message in
                DispatchQueue.main.async {
                    self.isSaving = false
                    self.errorMessage = message
                }
                return
            }
        )
    }

    func addToDiary(foodId: Int64) {
        guard let grams = Int32(gramsToAdd), grams > 0 else {
            errorMessage = "Введите корректный вес в граммах"
            return
        }

        bridge.addCustomFoodToDiary(
            foodId: foodId,
            grams: grams,
            onSuccess: { _ in
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
