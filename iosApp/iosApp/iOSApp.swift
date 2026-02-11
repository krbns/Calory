import SwiftUI

@main
struct CaloryApp: App {
    @StateObject private var dependencies = AppDependencies()

    var body: some Scene {
        WindowGroup {
            RootView()
                .environmentObject(dependencies)
                .preferredColorScheme(.dark)
        }
    }
}
