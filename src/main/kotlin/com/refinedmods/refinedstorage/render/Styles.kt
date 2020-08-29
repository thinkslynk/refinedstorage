package com.refinedmods.refinedstorage.render

import net.minecraft.text.Style
import net.minecraft.text.TextColor
import net.minecraft.util.Formatting

object Styles {
    val WHITE: Style = Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.WHITE))
    @JvmField
    val GRAY: Style = Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.GRAY))
    val YELLOW: Style = Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.YELLOW))
    @JvmField
    val RED: Style = Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.RED))
    val BLUE: Style = Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.BLUE))
    val AQUA: Style = Style.EMPTY.withColor(TextColor.fromFormatting(Formatting.AQUA))
}