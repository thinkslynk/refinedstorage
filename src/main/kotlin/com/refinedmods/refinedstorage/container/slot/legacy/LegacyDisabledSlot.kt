package com.refinedmods.refinedstorage.container.slot.legacy

import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack

class LegacyDisabledSlot(inventory: Inventory, inventoryIndex: Int, x: Int, y: Int) : LegacyBaseSlot(inventory, inventoryIndex, x, y) {
    fun isItemValid(stack: ItemStack): Boolean {
        return false
    }
}