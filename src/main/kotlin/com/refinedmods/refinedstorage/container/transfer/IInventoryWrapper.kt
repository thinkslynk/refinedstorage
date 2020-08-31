package com.refinedmods.refinedstorage.container.transfer

import net.minecraft.item.ItemStack

internal interface IInventoryWrapper {
    fun insert(stack: ItemStack): InsertionResult
}