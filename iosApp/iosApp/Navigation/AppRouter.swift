import Foundation

@MainActor
final class AppRouter: ObservableObject {
    enum Root {
        case loading
        case onboarding
        case main
    }

    @Published var root: Root = .loading

    func showOnboarding() {
        root = .onboarding
    }

    func showMain() {
        root = .main
    }
}
