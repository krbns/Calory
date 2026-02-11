import Foundation
import Shared

@MainActor
final class AppDependencies: ObservableObject {
    let container: IosAppContainer

    init() {
        container = IosBridgeFactory.shared.createContainer()
    }

    deinit {
        container.dispose()
    }
}
