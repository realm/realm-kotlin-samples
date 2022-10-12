//
//  ContentView.swift
//  macosApp
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
            
            ZStack(alignment: .topTrailing) {
                Button {
                    if (viewModel.wifiEnabled) {
                        viewModel.disableWifi()
                    } else {
                        viewModel.enableWifi()
                    }
                } label: {
                    var image: String = viewModel.wifiEnabled ? "wifi_on": "wifi_off"
                    Image(image)
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                        .frame(width: 48, height: 48)
                }.padding(EdgeInsets(top: 16, leading: 16, bottom: 16, trailing: 16)).buttonStyle(BorderlessButtonStyle())
            }.frame(minWidth: screen.width, minHeight: screen.height, alignment: .topTrailing)
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
