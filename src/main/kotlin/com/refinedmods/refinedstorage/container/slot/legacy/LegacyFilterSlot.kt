package com.refinedmods.refinedstorage.container.slot.legacy


import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack

class LegacyFilterSlot(inventory: Inventory, inventoryIndex: Int, x: Int, y: Int) : LegacyBaseSlot(inventory, inventoryIndex, x, y) {
    fun canTakeStack(player: PlayerEntity?): Boolean {
        return false
    }

    fun isItemValid(stack: ItemStack?): Boolean {
        return true
    }

    override fun setStack(stack: ItemStack) {
        if (!stack.isEmpty) {
            stack.count = 1
        }
        super.setStack(stack)
    }
}