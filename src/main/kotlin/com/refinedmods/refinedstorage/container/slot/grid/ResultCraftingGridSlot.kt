package com.refinedmods.refinedstorage.container.slot.grid

import com.refinedmods.refinedstorage.api.network.grid.IGrid
import com.refinedmods.refinedstorage.apiimpl.util.ItemStackList

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.CraftingResultSlot

class ResultCraftingGridSlot(player: PlayerEntity?, private val grid: IGrid?, inventoryIndex: Int, x: Int, y: Int) : CraftingResultSlot(player, grid!!.craftingMatrix, grid!!.craftingResult, inventoryIndex, x, y) {
    // @Volatile: Overriding logic from the super onTake method for Grid behaviors like refilling stacks from the network
    fun onTake(player: PlayerEntity, stack: ItemStack): ItemStack {
        onCrafted(stack)
        if (!player.entityWorld.isClient) {
            grid!!.onCrafted(player, null, ItemStackList())
        }
        return ItemStack.EMPTY
    }
}