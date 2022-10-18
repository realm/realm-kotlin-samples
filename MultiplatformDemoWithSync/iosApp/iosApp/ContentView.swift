//
//  ContentView.swift
//  iosApp
//

import SwiftUI
import shared

struct ContentView: View {
    @ObservedObject var vm = IOSCounterViewModel()
    let screen = UIScreen.main.bounds
    var body: some View {
        ZStack {
            Color.white
                .frame(
                    minWidth: screen.width,
                    minHeight: screen.height
                )
            VStack(spacing: 0) {
                CounterButton(screen: screen, action: {
                    vm.increment()
                })
                CounterButton(screen: screen, action: {
                    vm.decrement()
                })
            }
            .frame(
                minWidth: screen.width,
                minHeight: screen.height
            )
            
            Text(vm.counter)
                .fontWeight(.bold)
                .font(.system(size: 150))
            
            ZStack(alignment: .topTrailing) {
                Button {
                    if (vm.enabled) {
                        vm.disableWifi()
                    } else {
                        vm.enableWifi()
                    }
                } label: {
                    if (vm.enabled) {
                        Image("wifi")
                    } else {
                        Image("wifi_off")
                    }
                }.padding(EdgeInsets(top: 50, leading: 25, bottom: 25, trailing: 25))
            }.frame(minWidth: screen.width, minHeight: screen.height, alignment: .topTrailing)
 
        }
    }
}

struct CounterButton: View {
    var screen: CGRect
    var action: () -> Void
    var body: some View {
        Button {
            action()
        } label: {
            RealmColor.indigo
                .frame(
                    minWidth: screen.width,
                    minHeight: screen.height
                )
        }
        .buttonStyle(PlainButtonStyle())
        .frame(
            minWidth: screen.width,
            minHeight: screen.height,
            alignment: .center
        )
    }
}


struct ContentView_Previews: PreviewProvider {
   static var previews: some View {
       ContentView()
           .previewDevice(PreviewDevice(rawValue: "Mac"))
   }
}
