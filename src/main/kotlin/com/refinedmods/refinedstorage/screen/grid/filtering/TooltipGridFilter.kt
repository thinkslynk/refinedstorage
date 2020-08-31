package com.refinedmods.refinedstorage.screen.grid.filtering

import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack
import net.minecraft.text.Text
import java.util.function.Predicate

class TooltipGridFilter(tooltip: String) : Predicate<IGridStack> {
    private val tooltip: String = tooltip.toLowerCase()
    override fun test(stack: IGridStack): Boolean {
        val tooltip: List<Text>? = stack.tooltip
        for (i in 1 until tooltip!!.size) {
            if (tooltip[i].string.toLowerCase().contains(this.tooltip.toLowerCase())) {
                return true
            }
        }
        return false
    }

}