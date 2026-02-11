import SwiftUI

struct RootView: View {
    @EnvironmentObject private var dependencies: AppDependencies
    @StateObject private var router = AppRouter()

    var body: some View {
        Group {
            switch router.root {
            case .loading:
                ProgressView("Загрузка...")
                    .task {
                        resolveInitialRoute()
                    }
            case .onboarding:
                OnboardingView(
                    viewModel: OnboardingViewModel(bridge: dependencies.container.onboardingBridge),
                    onFinished: {
                        router.showMain()
                    }
                )
            case .main:
                MainView(
                    viewModel: MainViewModel(bridge: dependencies.container.mainBridge),
                    profileBridge: dependencies.container.profileBridge,
                    customFoodBridge: dependencies.container.customFoodBridge
                )
            }
        }
    }

    private func resolveInitialRoute() {
        dependencies.container.onboardingBridge.needsOnboarding(
            onSuccess: { value in
                let needsOnboarding = value.boolValue
                DispatchQueue.main.async {
                    if needsOnboarding {
                        router.showOnboarding()
                    } else {
                        router.showMain()
                    }
                }
            },
            onError: { _ in
                DispatchQueue.main.async {
                    router.showOnboarding()
                }
            }
        )
    }
}
