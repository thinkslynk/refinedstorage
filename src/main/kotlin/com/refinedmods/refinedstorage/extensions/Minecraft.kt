package com.refinedmods.refinedstorage.extensions

import com.refinedmods.refinedstorage.render.RenderSettings
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.block.entity.BlockEntity
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.contracts.contract


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
    if(this.isEmpty) emptyList() else (0 until this.size()).map { this.getStack(it) }

fun Inventory.drop(world: World, pos: BlockPos) {
    // TODO figure out how to drop an inventory at a position...
}

fun Inventory.toSimpleInventory(): SimpleInventory {
    return SimpleInventory(*getStacks().toTypedArray())
}

fun TextRenderer.draw(matrices: MatrixStack, text: Text, x: Float, y: Float) {
    this.draw(matrices, text, x, y, RenderSettings.INSTANCE.secondaryColor)
}

fun TextRenderer.draw(matrices: MatrixStack, text: String, x: Float, y: Float) {
    this.draw(matrices, text, x, y, RenderSettings.INSTANCE.secondaryColor)
fun World.isServer(): Boolean {
    contract {
        returns(true) implies (this@isServer is ServerWorld)
    }
    return !this.isClient
}

inline fun BlockEntity.onServer(block: (ServerWorld) -> Unit) {
    val world = world ?: error("Not placed yet")
    if (world.isServer()) block(world)
}

inline fun <T> BlockEntity.onSide(server: (ServerWorld) -> T, client: (World) -> T): T {
    val world = world ?: error("Not placed yet")
    return if (world.isServer()) server(world)
    else client(world)
}

@Deprecated("migration", ReplaceWith("Constants.NBT.LIST_TAG", "com.refinedmods.refinedstorage.extensions.Constants"))
const val LIST_TAG_TYPE = Constants.NBT.LIST_TAG

@Deprecated(
    "migration",
    ReplaceWith("Constants.NBT.COMPOUND_TAG", "com.refinedmods.refinedstorage.extensions.Constants")
)
const val COMPOUND_TAG_TYPE = Constants.NBT.COMPOUND_TAG
