package com.refinedmods.refinedstorage.container.slot


import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack

class OutputSlot(inventory: Inventory, inventoryIndex: Int, x: Int, y: Int) : BaseSlot(inventory, inventoryIndex, x, y) {
    fun isItemValid(stack: ItemStack): Boolean {
        return false
    }
}