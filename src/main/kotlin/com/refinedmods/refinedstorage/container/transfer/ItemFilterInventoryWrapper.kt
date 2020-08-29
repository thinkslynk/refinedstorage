package com.refinedmods.refinedstorage.container.transfer

import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack

internal class ItemFilterInventoryWrapper(
        private val filterInv: Inventory
) : IInventoryWrapper {
    override fun insert(stack: ItemStack): InsertionResult {
        val stop = InsertionResult(InsertionResultType.STOP)
        for (i in 0 until filterInv.size()) {
            if (instance().comparer.isEqualNoQuantity(filterInv.getStack(i), stack)) {
                return stop
            }
        }
        for (i in 0 until filterInv.size()) {
            if (filterInv.getStack(i).isEmpty) {
                val c = stack.copy()
                c.count = 1
                filterInv.setStack(i, c)
                break
            }
        }
        return stop
    }
}