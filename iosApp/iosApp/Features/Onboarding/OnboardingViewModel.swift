import Foundation
import Shared

@MainActor
final class OnboardingViewModel: ObservableObject {
    @Published var form: ProfileFormData = .empty
    @Published var isSaving: Bool = false
    @Published var errorMessage: String?

    private let bridge: OnboardingBridge

    init(bridge: OnboardingBridge) {
        self.bridge = bridge
    }

    func save(onSuccess: @escaping () -> Void) {
        guard let age = Int32(form.age), let height = Int32(form.heightCm), let weight = Double(form.weightKg) else {
            errorMessage = "Введите корректные числовые значения профиля"
            return
        }

        isSaving = true
        errorMessage = nil

        bridge.saveProfile(
            name: form.name,
            sex: form.sex,
            age: age,
            heightCm: height,
            weightKg: weight,
            goal: form.goal,
            onSuccess: {
                DispatchQueue.main.async {
                    self.isSaving = false
                    onSuccess()
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
}
