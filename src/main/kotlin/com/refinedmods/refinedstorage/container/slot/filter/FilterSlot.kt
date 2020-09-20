package com.refinedmods.refinedstorage.container.slot.filter

import com.refinedmods.refinedstorage.container.slot.BaseSlot
import com.refinedmods.refinedstorage.extensions.getCustomLogger
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack

class FilterSlot(
    handler: Inventory,
    inventoryIndex: Int,
    x: Int,
    y: Int,
    flags: Int = 0
): BaseSlot(handler, inventoryIndex, x, y) {
    override val isSizeAllowed: Boolean = flags and FILTER_ALLOW_SIZE == FILTER_ALLOW_SIZE
    val isBlockAllowed: Boolean = flags and FILTER_ALLOW_BLOCKS == FILTER_ALLOW_BLOCKS
    val isAlternativesAllowed: Boolean = flags and FILTER_ALLOW_ALTERNATIVES == FILTER_ALLOW_ALTERNATIVES

    companion object {
        const val FILTER_ALLOW_SIZE = 1
        const val FILTER_ALLOW_BLOCKS = 2
        const val FILTER_ALLOW_ALTERNATIVES = 4
    }

    override fun canInsert(stack: ItemStack): Boolean {
        return if (super.canInsert(stack)) {
            if (isBlockAllowed) {
                stack.item is BlockItem
            } else true
        } else false
    }

    override fun setStack(stack: ItemStack) {
        if (!stack.isEmpty && !isSizeAllowed) {
            stack.count = 1
        }
        super.setStack(stack)
    }

    fun canTakeStack(playerIn: PlayerEntity?): Boolean {
        return false
    }
}