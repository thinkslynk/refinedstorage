package com.refinedmods.refinedstorage.container.transfer

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import reborncore.api.items.InventoryUtils
import java.util.*

internal class InventoryInventoryWrapper(
        val inventory: Inventory
) : IInventoryWrapper {
    private var wrapper: Inventory = inventory
    override fun insert(stack: ItemStack): InsertionResult {
        return InsertionResult(InventoryUtils.insertItemStacked(wrapper, stack, false))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val that = other as InventoryInventoryWrapper
        return inventory == that.inventory
    }

    override fun hashCode(): Int {
        return Objects.hash(inventory)
    }

    init {
        // TODO Inventory wrappers
//        if (inventory is PlayerInventory) {
//            // Don't use PlayerMainInvWrapper to avoid stack animations.
//            wrapper = RangedWrapper(InvWrapper(inventory), 0, (inventory as PlayerInventory).mainInventory.size())
//        } else {
//            wrapper = InvWrapper(inventory)
//        }
    }
}