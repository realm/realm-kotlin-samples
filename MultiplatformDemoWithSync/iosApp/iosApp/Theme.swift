//
//  Theme.swift
//  iosApp
//
//  Created by Christian Melchior on 01/10/2021.
//

import Foundation
import SwiftUI

// Credit: https://stackoverflow.com/a/56874327/1389357
extension Color {
    init(hex: UInt, alpha: Double = 1) {
        self.init(
            .sRGB,
            red: Double((hex >> 16) & 0xff) / 255,
            green: Double((hex >> 08) & 0xff) / 255,
            blue: Double((hex >> 00) & 0xff) / 255,
            opacity: alpha
        )
    }
}

struct RealmColor {
    // Greys
    static let charcoal = Color.init(hex: 0x1C233F)
    static let elephant = Color.init(hex: 0x9A9BA5)
    static let dov = Color.init(hex: 0xEBEBF2)

    // Orb colors
    static let ultramarine = Color.init(hex: 0x39477F)
    static let indigo = Color.init(hex: 0x59569E)
    static let grapeJelly = Color.init(hex: 0x9A59A5)
    static let mulberry = Color.init(hex: 0xD34CA3)
    static let flamingo = Color.init(hex: 0xF25192)
    static let sexySalmon = Color.init(hex: 0xFC9F95)
    static let melon = Color.init(hex: 0xFCC397)
}
