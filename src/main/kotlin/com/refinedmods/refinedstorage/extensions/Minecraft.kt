package com.refinedmods.refinedstorage.extensions

import net.minecraft.entity.data.TrackedDataHandler
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.ListTag
import net.minecraft.network.PacketByteBuf
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

val DOUBLE: TrackedDataHandler<Double?> = object : TrackedDataHandler<Double?> {
    override fun write(packetByteBuf: PacketByteBuf, double: Double?) {
        packetByteBuf.writeDouble(double!!)
    }

    override fun read(packetByteBuf: PacketByteBuf): Double? {
        return packetByteBuf.readDouble()
    }

    override fun copy(double: Double?): Double? {
        return double
    }
}

val LIST_TAG_TYPE by lazy { ListTag().type.toInt() }

