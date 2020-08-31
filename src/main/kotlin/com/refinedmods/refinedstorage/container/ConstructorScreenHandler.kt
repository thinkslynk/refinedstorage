package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.tile.ConstructorTile
import com.refinedmods.refinedstorage.tile.config.IType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.slot.Slot
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.function.Supplier

class ConstructorScreenHandler(context: ScreenHandlerContext, player: PlayerEntity, windowId: Int):
        BaseContainer(RS.CONSTRUCTOR_SCREEN_HANDLER, context, player, windowId) {

    init {
        context.run { world: World, blockPos: BlockPos ->
            val entity = world.getBlockEntity(blockPos) as ConstructorTile
            for (i in 0..3) {
                addSlot(Slot(entity.node.upgrades, i, 187, 6 + i * 18))
            }
            addSlot(FilterSlot(entity.node.itemFilters, 0, 80, 20)
                    .setEnableHandler(Supplier { entity.node.type == IType.ITEMS }))
            // TODO Fluid
//            addSlot(FluidFilterSlot(entity.node.fluidFilters, 0, 80, 20, 0)
//                    .setEnableHandler(Supplier { entity.node.type == IType.FLUIDS }))
            addPlayerInventory(8, 55)
            transferManager.addBiTransfer(player.inventory, entity.node.upgrades)
            transferManager.addFilterTransfer(player.inventory, entity.node.itemFilters, entity.node.fluidFilters, Supplier { entity.node.type })
        }
    }
}