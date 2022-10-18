//
//  macosAppApp.swift
//  macosApp
//
import SwiftUI

@main
struct MacOSApp: App {
    let vm = MacOSCounterViewModel()
    var body: some Scene {
        WindowGroup(vm.platform(), id: "MainScreen") {
            ContentView(viewModel: vm)
        }
    }
}
