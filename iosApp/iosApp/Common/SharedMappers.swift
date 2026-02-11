import Foundation
import Shared

struct MainTrackedFoodItem: Identifiable {
    let id: Int64
    let name: String
    let grams: Int32
    let calories: Double
    let proteins: Double
    let fats: Double
    let carbs: Double
}

struct MainTotals {
    let calories: Double
    let proteins: Double
    let fats: Double
    let carbs: Double

    static let empty = MainTotals(calories: 0, proteins: 0, fats: 0, carbs: 0)
}

struct MacroTargetsViewData {
    let calories: Double
    let proteins: Double
    let fats: Double
    let carbs: Double
}

struct FoodSearchItem: Identifiable {
    let id: Int64
    let name: String
    let calories: Double
    let proteins: Double
    let fats: Double
    let carbs: Double
}

struct CustomFoodItemViewData: Identifiable {
    let id: Int64
    let name: String
    let calories: Double
    let proteins: Double
    let fats: Double
    let carbs: Double
}

struct ProfileFormData {
    var name: String = ""
    var sex: String = "MALE"
    var age: String = "25"
    var heightCm: String = "175"
    var weightKg: String = "70"
    var goal: String = "LOSE_WEIGHT"

    static let empty = ProfileFormData()
}

extension IosTrackedFoodDto {
    func toViewData() -> MainTrackedFoodItem {
        MainTrackedFoodItem(
            id: id,
            name: name,
            grams: grams,
            calories: calories,
            proteins: proteins,
            fats: fats,
            carbs: carbs
        )
    }
}

extension IosFoodDto {
    func toViewData() -> FoodSearchItem {
        FoodSearchItem(
            id: id,
            name: name,
            calories: calories,
            proteins: proteins,
            fats: fats,
            carbs: carbs
        )
    }
}

extension IosCustomFoodDto {
    func toViewData() -> CustomFoodItemViewData {
        CustomFoodItemViewData(
            id: id,
            name: name,
            calories: calories,
            proteins: proteins,
            fats: fats,
            carbs: carbs
        )
    }
}

extension IosUserProfileDto {
    func toFormData() -> ProfileFormData {
        ProfileFormData(
            name: name,
            sex: sex,
            age: String(age),
            heightCm: String(heightCm),
            weightKg: String(weightKg),
            goal: goal
        )
    }
}

func asIosFoodList(_ value: Any?) -> [IosFoodDto] {
    if let typed = value as? [IosFoodDto] {
        return typed
    }
    if let nsArray = value as? NSArray {
        return nsArray.compactMap { $0 as? IosFoodDto }
    }
    return []
}

func asIosCustomFoodList(_ value: Any?) -> [IosCustomFoodDto] {
    if let typed = value as? [IosCustomFoodDto] {
        return typed
    }
    if let nsArray = value as? NSArray {
        return nsArray.compactMap { $0 as? IosCustomFoodDto }
    }
    return []
}

func asIosTrackedFoodList(_ value: Any?) -> [IosTrackedFoodDto] {
    if let typed = value as? [IosTrackedFoodDto] {
        return typed
    }
    if let nsArray = value as? NSArray {
        return nsArray.compactMap { $0 as? IosTrackedFoodDto }
    }
    return []
}
