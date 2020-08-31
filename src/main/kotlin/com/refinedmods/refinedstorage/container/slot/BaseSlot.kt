package com.refinedmods.refinedstorage.container.slot

import net.minecraft.inventory.Inventory
import net.minecraft.screen.slot.Slot
import java.util.function.Supplier

open class BaseSlot(itemHandler: Inventory, inventoryIndex: Int, x: Int, y: Int):
        Slot(itemHandler, inventoryIndex, x, y)
{
    private var enableHandler = Supplier { true }
    fun setEnableHandler(enableHandler: Supplier<Boolean>): BaseSlot {
        this.enableHandler = enableHandler
        return this
    }

    open val isEnabled: Boolean
        get() = enableHandler.get()
}