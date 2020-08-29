package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.container.slot.DisabledSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandlerContext
import reborncore.common.util.InventoryItem

class AmountContainer(player: PlayerEntity, stack: ItemStack) :
        BaseContainer(null, ScreenHandlerContext.EMPTY, player, 0) {
    init {
        val inventory = InventoryItem.getItemInvetory(stack, 1)
        addSlot(DisabledSlot(inventory, 0, 89, 48))
    }
}