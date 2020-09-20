package com.refinedmods.refinedstorage.gui.screenhandlers

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.RSGui
import com.refinedmods.refinedstorage.container.BaseContainer
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot
import com.refinedmods.refinedstorage.data.BaseBlockEntityData
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory
import com.refinedmods.refinedstorage.tile.config.IType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandlerContext

class FilterScreenHandler(
    windowId: Int,
    player: PlayerEntity,
    entityData: BaseBlockEntityData? = null,
    val stack: ItemStack
):
        BaseScreenHandler(windowId, player, entityData, RSGui.FILTER) {

    override val disabledSlotNumber: Int = player.inventory.selectedSlot

    init {
        // TODO Filter
//        var y = 20
//        var x = 8
//        val filter = FilterItemsItemHandler(stack)
//        val fluidFilter: FluidInventory = FilterFluidInventory(stack)
//        for (i in 0..26) {
//            addSlot(FilterSlot(filter, i, x, y).setEnableHandler { FilterItem.getType(stack) == IType.ITEMS })
//            addSlot(FluidFilterSlot(fluidFilter, i, x, y).setEnableHandler { FilterItem.getType(stack) == IType.FLUIDS })
//            if ((i + 1) % 9 == 0) {
//                x = 8
//                y += 18
//            } else {
//                x += 18
//            }
//        }
//        addSlot(FilterSlot(FilterIconItemHandler(stack), 0, 8, 117).setEnableHandler { FilterItem.getType(stack) == IType.ITEMS })
//        addSlot(FluidFilterSlot(FilterIconFluidInventory(stack), 0, 8, 117).setEnableHandler { FilterItem.getType(stack) == IType.FLUIDS })
//        addPlayerInventory(8, 149)
//        transferManager.addFilterTransfer(player.inventory, filter, fluidFilter, Supplier { FilterItem.getType(stack) })
    }
}