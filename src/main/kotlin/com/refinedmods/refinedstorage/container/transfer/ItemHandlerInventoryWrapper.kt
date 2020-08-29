package com.refinedmods.refinedstorage.container.transfer

import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import reborncore.api.items.InventoryUtils
import java.util.*

internal class ItemHandlerInventoryWrapper(
        val handler: Inventory
) : IInventoryWrapper {
    override fun insert(stack: ItemStack): InsertionResult {
        return InsertionResult(InventoryUtils.insertItemStacked(handler, stack, false))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val that = other as ItemHandlerInventoryWrapper
        return handler == that.handler
    }

    override fun hashCode(): Int {
        return Objects.hash(handler)
    }
}