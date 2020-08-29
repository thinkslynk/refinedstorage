package com.refinedmods.refinedstorage.container.slot.filter

import com.refinedmods.refinedstorage.container.slot.BaseSlot
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import reborncore.common.util.InventoryItem

open class FluidFilterSlot @JvmOverloads constructor(val fluidInventory: FluidInventory, inventoryIndex: Int, x: Int, y: Int, private val flags: Int = 0) :
        BaseSlot(InventoryItem.getItemInvetory(ItemStack.EMPTY, 0),
//                fluidInventory,
                inventoryIndex, x, y) {
    fun isItemValid(stack: ItemStack): Boolean {
        return false
    }

    open fun onContainerClicked(stack: ItemStack) {
//        fluidInventory.setFluid(id, getFluid(stack, true).value)
    }

    fun canTakeStack(playerIn: PlayerEntity?): Boolean {
        return false
    }

    val isSizeAllowed: Boolean
        get() = flags and FILTER_ALLOW_SIZE == FILTER_ALLOW_SIZE
    val isAlternativesAllowed: Boolean
        get() = flags and FILTER_ALLOW_ALTERNATIVES == FILTER_ALLOW_ALTERNATIVES

    companion object {
        const val FILTER_ALLOW_SIZE = 1
        const val FILTER_ALLOW_ALTERNATIVES = 2
    }
}