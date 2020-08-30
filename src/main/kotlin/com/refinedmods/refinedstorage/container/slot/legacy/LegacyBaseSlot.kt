package com.refinedmods.refinedstorage.container.slot.legacy

import net.minecraft.inventory.Inventory
import net.minecraft.screen.slot.Slot
import java.util.function.Supplier

open class LegacyBaseSlot(
        inventory: Inventory,
        inventoryIndex: Int,
        x: Int, y: Int
):
        Slot(inventory, inventoryIndex, x, y)
{
    private var enableHandler = Supplier { true }
    fun setEnableHandler(enableHandler: Supplier<Boolean>): LegacyBaseSlot {
        this.enableHandler = enableHandler
        return this
    }

    val isEnabled: Boolean
        get() = enableHandler.get()
}