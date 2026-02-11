import Foundation
import Shared

@MainActor
final class ProfileViewModel: ObservableObject {
    @Published var form: ProfileFormData = .empty
    @Published var isSaving: Bool = false
    @Published var errorMessage: String?

    private let bridge: ProfileBridge
    private var handle: DisposableHandle?

    init(bridge: ProfileBridge) {
        self.bridge = bridge
    }

    deinit {
        handle?.dispose()
    }

    func start() {
        if handle != nil {
            return
        }

        handle = bridge.observeProfile(
            onEach: { profile in
                DispatchQueue.main.async {
                    if let profile {
                        self.form = profile.toFormData()
                    }
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

    func save() {
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
