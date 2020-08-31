package com.refinedmods.refinedstorage.extensions

import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


fun ItemStack.safeAdd(size: Int) {
    this.count = (count.toLong() + size.toLong())
            .coerceAtMost(maxCount.toLong())
            .coerceAtLeast(0)
            .toInt()
}

fun ItemStack.safeSubtract(size: Int) {
    this.safeAdd(0 - size)
}

fun Inventory.getStacks(): Collection<ItemStack> =
        (0..this.size()).map { this.getStack(it) }

fun Inventory.drop(world: World, pos: BlockPos) {
    // TODO figure out how to drop an inventory at a position...
}


@Deprecated("migration", ReplaceWith("Constants.NBT.LIST_TAG", "com.refinedmods.refinedstorage.extensions.Constants"))
const val LIST_TAG_TYPE = Constants.NBT.LIST_TAG
@Deprecated("migration", ReplaceWith("Constants.NBT.COMPOUND_TAG", "com.refinedmods.refinedstorage.extensions.Constants"))
const val COMPOUND_TAG_TYPE = Constants.NBT.COMPOUND_TAG
