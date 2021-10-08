//
//  ContentView.swift
//  macosApp
//
//  Created by Christian Melchior on 24/09/2021.
//
import SwiftUI
import shared

struct Screen {
    var width: CGFloat;
    var height: CGFloat;
}

struct ContentView: View {
    @ObservedObject var viewModel: MacOSCounterViewModel
    var screen = Screen(width: 320, height: 500)
    var body: some View {
        ZStack {
            Color.white
                .frame(
                    minWidth: screen.width,
                    minHeight: screen.height
                )

            VStack(spacing: 0) {
                CounterButton(screen: screen, action:{
                    viewModel.increment()
                })
                CounterButton(screen: screen, action: {
                    viewModel.decrement()
                })
            }
            .frame(
                minWidth: screen.width,
                minHeight: screen.height
            )
            
            Text(viewModel.counter)
                .fontWeight(.bold)
                .font(.system(size: 150))

        }
        .onAppear {
            viewModel.start()
        }
        .onDisappear {
            viewModel.stop()
        }
    }
}

struct CounterButton: View {
    var screen: Screen
    var action: () -> Void
    var body: some View {
        Button {
            action()
        } label: {
            RealmColor.mulberry
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
       ContentView(viewModel: MacOSCounterViewModel())
           .previewDevice(PreviewDevice(rawValue: "Mac"))
   }
}
