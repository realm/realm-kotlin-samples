//
//  ViewModel.swift
//  iosApp
//
//  Created by Christian Melchior on 24/09/2021.
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

    private let vm: SharedCounterViewModel = SharedCounterViewModel()
        
    func increment() {
        vm.increment()
    }

    func decrement() {
        vm.decrement()
    }
    
    func start() {
        addObserver(observer: vm.observeCounter().watch { counterValue in
            self.counter = counterValue! as String
        })
    }
    
    override func stop() {
        super.stop()
        vm.close()
    }
}
