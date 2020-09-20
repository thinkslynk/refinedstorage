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

    init {
        syncedData.register()

        buf?.let {
            syncedData.load(it)
        }

        repeat(4) {
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

}