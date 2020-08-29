package com.refinedmods.refinedstorage.container.transfer

import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory
import com.refinedmods.refinedstorage.tile.config.IType
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import java.util.function.Supplier

internal class FilterInventoryWrapper(
        itemTo: Inventory,
        fluidTo: FluidInventory,
        private val typeGetter: Supplier<Int>
) : IInventoryWrapper {
    private val item: ItemFilterInventoryWrapper = ItemFilterInventoryWrapper(itemTo)
    private val fluid: FluidFilterInventoryWrapper= FluidFilterInventoryWrapper(fluidTo)

    override fun insert(stack: ItemStack): InsertionResult {
        return if (typeGetter.get() == IType.ITEMS) item.insert(stack) else fluid.insert(stack)
    }

}