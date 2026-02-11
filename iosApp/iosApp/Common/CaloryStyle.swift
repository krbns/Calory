import SwiftUI
import UIKit

enum CaloryStyle {
    static let background = Color(hex: 0x0B132B)
    static let onBackground = Color(hex: 0xE9F1FF)
    static let surface = Color(hex: 0x111B34)
    static let surfaceVariant = Color(hex: 0x1B2A4A)
    static let onSurface = Color(hex: 0xE9F1FF)
    static let onSurfaceVariant = Color(hex: 0xC5D1EC)
    static let primary = Color(hex: 0x8EF7B5)
    static let onPrimary = Color(hex: 0x013220)
    static let secondary = Color(hex: 0xFFCE89)
    static let tertiary = Color(hex: 0xB7C4FF)
    static let error = Color(hex: 0xFF6B6B)
}

struct CaloryGradientBackground: View {
    var body: some View {
        LinearGradient(
            colors: [CaloryStyle.background, CaloryStyle.surfaceVariant],
            startPoint: .topLeading,
            endPoint: .bottomTrailing
        )
        .ignoresSafeArea()
    }
}

struct CaloryCard<Content: View>: View {
    let content: Content

    init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            content
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(
            RoundedRectangle(cornerRadius: 18, style: .continuous)
                .fill(CaloryStyle.surface.opacity(0.92))
        )
        .overlay(
            RoundedRectangle(cornerRadius: 18, style: .continuous)
                .stroke(CaloryStyle.onSurfaceVariant.opacity(0.15), lineWidth: 1)
        )
    }
}

struct CaloryPrimaryButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .font(.headline)
            .foregroundStyle(CaloryStyle.onPrimary)
            .frame(maxWidth: .infinity)
            .padding(.vertical, 14)
            .background(
                RoundedRectangle(cornerRadius: 14, style: .continuous)
                    .fill(CaloryStyle.primary.opacity(configuration.isPressed ? 0.85 : 1.0))
            )
    }
}

struct CaloryInputField: View {
    let placeholder: String
    @Binding var text: String
    var keyboardType: UIKeyboardType = .default
    var capitalization: TextInputAutocapitalization = .sentences

    var body: some View {
        TextField(
            "",
            text: $text,
            prompt: Text(placeholder).foregroundColor(CaloryStyle.onSurfaceVariant.opacity(0.85))
        )
        .keyboardType(keyboardType)
        .textInputAutocapitalization(capitalization)
        .foregroundStyle(CaloryStyle.onSurface)
        .tint(CaloryStyle.primary)
        .padding(12)
        .background(CaloryStyle.surfaceVariant.opacity(0.6), in: RoundedRectangle(cornerRadius: 12))
    }
}

private extension Color {
    init(hex: UInt32) {
        let r = Double((hex >> 16) & 0xFF) / 255.0
        let g = Double((hex >> 8) & 0xFF) / 255.0
        let b = Double(hex & 0xFF) / 255.0
        self.init(red: r, green: g, blue: b)
    }
}
