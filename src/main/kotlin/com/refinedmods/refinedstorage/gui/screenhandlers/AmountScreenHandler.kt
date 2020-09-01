package com.refinedmods.refinedstorage.gui.screenhandlers

import com.refinedmods.refinedstorage.RSGui
import com.refinedmods.refinedstorage.container.slot.DisabledSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandlerContext
import reborncore.common.util.InventoryItem

class AmountScreenHandler(player: PlayerEntity, stack: ItemStack?=null, windowId: Int=0) :
        BaseScreenHandler(RSGui.AMOUNT, ScreenHandlerContext.EMPTY, player, windowId) {
    init {
        val inventory = if (stack != null) InventoryItem.getItemInvetory(stack, 1) else SimpleInventory(1)
        addSlot(DisabledSlot(inventory, 0, 89, 48))
    }
}