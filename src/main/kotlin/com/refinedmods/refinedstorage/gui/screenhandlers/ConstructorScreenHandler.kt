package com.refinedmods.refinedstorage.gui.screenhandlers

import com.refinedmods.refinedstorage.RSGui
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.data.ConstructorGuiData
import com.refinedmods.refinedstorage.data.sync.S2CSyncedData
import com.refinedmods.refinedstorage.tile.config.IType
import java.util.Collections.singletonList
import java.util.function.Supplier
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.slot.Slot

class ConstructorScreenHandler(
    windowId: Int,
    player: PlayerEntity,
    buf: PacketByteBuf? = null,
    data: ConstructorGuiData = ConstructorGuiData()
): BaseScreenHandler(windowId, player, data.entity, RSGui.CONSTRUCTOR) {
    private val syncedData = S2CSyncedData(
        ConstructorGuiData.ID,
        player.world.isClient,
        data,
        singletonList(player)
    )

    override fun close(player: PlayerEntity) {
        super.close(player)
        syncedData.unregister()
    }

    // TODO OLD
    init {
        syncedData.register()

        buf?.let {
            syncedData.load(it)
        }

        (0 until 4).forEach {
            addSlot(Slot(syncedData.data.upgrades, it, 187, 6 + it * 18))
        }

        addSlot(
            FilterSlot(syncedData.data.itemFilters, 0, 80, 20)
            .setEnableHandler(Supplier { syncedData.data.iType == IType.ITEMS }))
//        addSlot(
//            FluidFilterSlot(data.fluidFilters, 0, 80, 20, 0)
//                .setEnableHandler(Supplier { data.iType == IType.FLUIDS }))
        addPlayerInventory(8, 55)
    }

//    init {
//        val upgrades = node?.upgrades ?: SimpleInventory(4)
//        val filter = node?.itemFilters ?: SimpleInventory(1)
//        val fluidFilter = node?.fluidFilters ?: FluidInventory(0)
//
//        val root = WBox(Axis.HORIZONTAL)
//        setRootPanel(root)
//
//        val left = WBox(Axis.VERTICAL)
//
//        // Item to construct and player inventory
//        val invPanel = createPlayerInventoryPanel()
//        val filterSlot = WItemSlot.of(filter, 0, 1, 1)
//        val center = WBox(Axis.VERTICAL)
//        center.spacing = SECTION_PADDING
//        center.horizontalAlignment = HorizontalAlignment.CENTER
//        center.add(filterSlot)
//        center.add(invPanel)
//
//        val right = WItemSlot.of(upgrades, 0, 1, upgrades.size())
//
//        // Compose
//        root.add(left)
//        root.add(center)
//        root.add(right)
//
//        root.validate(this)
//
//        transferManager.addBiTransfer(player.inventory, upgrades)
//        transferManager.addFilterTransfer(player.inventory, filter, fluidFilter, Supplier {
//            node?.type ?: ConstructorTile.TYPE.value
//        })
//    }

}