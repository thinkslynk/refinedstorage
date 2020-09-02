package com.refinedmods.refinedstorage.gui.screenhandlers

import com.refinedmods.refinedstorage.RSGui
import com.refinedmods.refinedstorage.container.slot.DisabledSlot
import com.refinedmods.refinedstorage.data.BaseBlockEntityData
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import reborncore.common.util.InventoryItem

class AmountScreenHandler(
    player: PlayerEntity,
    entityData: BaseBlockEntityData,
    stack: ItemStack?=null,
    windowId: Int=0
) : BaseScreenHandler(RSGui.AMOUNT, entityData, player, windowId) {
    init {
        val inventory = if (stack != null) InventoryItem.getItemInvetory(stack, 1) else SimpleInventory(1)
        addSlot(DisabledSlot(inventory, 0, 89, 48))
    }
}