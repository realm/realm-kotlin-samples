//
//  macosAppApp.swift
//  macosApp
//
//  Created by Christian Melchior on 01/10/2021.
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
