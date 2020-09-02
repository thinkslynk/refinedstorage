package com.refinedmods.refinedstorage.gui.screenhandlers

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.RSGui
import com.refinedmods.refinedstorage.apiimpl.network.node.ConstructorNetworkNode
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot
import com.refinedmods.refinedstorage.data.BaseBlockEntityData
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory
import com.refinedmods.refinedstorage.tile.ConstructorTile
import com.refinedmods.refinedstorage.tile.config.IType
import io.github.cottonmc.cotton.gui.widget.WBox
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.data.Axis
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment
import java.util.function.Supplier
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.slot.Slot
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


class ConstructorScreenHandler(
    syncId: Int,
    entityData: BaseBlockEntityData,
    player: PlayerEntity,
    node: ConstructorNetworkNode? = null
): BaseScreenHandler(RSGui.CONSTRUCTOR, entityData, player, syncId) {
    companion object{
        const val SECTION_PADDING = 5
    }

    // TODO OLD
//    init {
//        context.run { world: World, blockPos: BlockPos ->
//            val entity = world.getBlockEntity(blockPos) as ConstructorTile
//            for (i in 0..3) {
//                addSlot(Slot(entity.node.upgrades, i, 187, 6 + i * 18))
//            }
//            addSlot(
//                FilterSlot(entity.node.itemFilters, 0, 80, 20)
//                .setEnableHandler(Supplier { entity.node.type == IType.ITEMS }))
//            addSlot(
//                FluidFilterSlot(entity.node.fluidFilters, 0, 80, 20, 0)
//                    .setEnableHandler(Supplier { entity.node.type == IType.FLUIDS }))
//            addPlayerInventory(8, 55)
//            transferManager.addBiTransfer(player.inventory, entity.node.upgrades)
//            transferManager.addFilterTransfer(player.inventory, entity.node.itemFilters, entity.node.fluidFilters, Supplier { entity.node.type })
//        }
//    }

    init {
        val upgrades = node?.upgrades ?: SimpleInventory(4)
        val filter = node?.itemFilters ?: SimpleInventory(1)
        val fluidFilter = node?.fluidFilters ?: FluidInventory(0)

        val root = WBox(Axis.HORIZONTAL)
        setRootPanel(root)

        val left = WBox(Axis.VERTICAL)

        // Item to construct and player inventory
        val invPanel = createPlayerInventoryPanel()
        val filterSlot = WItemSlot.of(filter, 0, 1, 1)
        val center = WBox(Axis.VERTICAL)
        center.spacing = SECTION_PADDING
        center.horizontalAlignment = HorizontalAlignment.CENTER
        center.add(filterSlot)
        center.add(invPanel)

        val right = WItemSlot.of(upgrades, 0, 1, upgrades.size())

        // Compose
        root.add(left)
        root.add(center)
        root.add(right)

        root.validate(this)

        transferManager.addBiTransfer(player.inventory, upgrades)
        transferManager.addFilterTransfer(player.inventory, filter, fluidFilter, Supplier {
            node?.type ?: ConstructorTile.TYPE.value
        })
    }

}