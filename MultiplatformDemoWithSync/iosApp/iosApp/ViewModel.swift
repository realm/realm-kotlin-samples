//
//  ViewModel.swift
//  iosApp
//
import Foundation
import Combine
import shared

// Generic Observable View Model, making it easier to control the lifecycle
// of multiple Flows.
class ObservableViewModel {
    private var jobs = Array<Closeable>() // List of Kotlin Coroutine Jobs

    func addObserver(observer: Closeable) {
        jobs.append(observer)
    }
    
    func stop() {
        jobs.forEach { job in job.close() }
    }
}

class IOSCounterViewModel: ObservableViewModel, ObservableObject {
    @Published var counter: String = "-"
    @Published var enabled: Bool = true

    private let vm: SharedCounterViewModel = SharedCounterViewModel()
        
    override init() {
        super.init()
        start()
    }
    
    deinit {
        super.stop()
        vm.close()
    }
    
    func increment() {
        vm.increment()
    }

    func decrement() {
        vm.decrement()
    }
    
    func disableWifi() {
        vm.disableWifi()
    }
    
    func enableWifi() {
        vm.enableWifi()
    }
    
    func start() {
        addObserver(observer: vm.observeCounter().watch { counterValue in
            self.counter = counterValue! as String
        })
        addObserver(observer: vm.observeWifiState().watch { wifiEnabled in
            if (wifiEnabled!.boolValue) {
                self.enabled = true
            } else {
                self.enabled = false
            }
        })
    }
}
