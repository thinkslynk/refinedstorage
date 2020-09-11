package com.refinedmods.refinedstorage.gui.screenhandlers

import com.refinedmods.refinedstorage.RSGui
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.data.DestructorGuiData
import com.refinedmods.refinedstorage.data.sync.S2CSyncedData
import com.refinedmods.refinedstorage.tile.config.IType
import java.util.Collections.singletonList
import java.util.function.Supplier
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.slot.Slot

class DestructorScreenHandler(
    windowId: Int,
    player: PlayerEntity,
    buf: PacketByteBuf? = null,
    data: DestructorGuiData = DestructorGuiData()
): BaseScreenHandler(windowId, player, data.entity, RSGui.DESTRUCTOR) {
    private val syncedData = S2CSyncedData(
        DestructorGuiData.ID,
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

        buf?.let { syncedData.load(it) }

        (0 until 4).forEach { addSlot(Slot(syncedData.data.upgrades, it, 187, 6 + it * 18)) }
        (0 until 9).forEach { i ->
            addSlot(FilterSlot(syncedData.data.itemFilters, i, 8 + 18 * i, 20)
                .setEnableHandler(Supplier { syncedData.data.iType == IType.ITEMS }))
        }
        // TODO Fluids
//        (0 until 9).forEach { i ->
//            addSlot(FluidFilterSlot(syncedData.data.fluidFilters, i, 8 + 18 * i, 20)
//                .setEnableHandler(Supplier { syncedData.data.iType == IType.FLUIDS }))
//        }

        addPlayerInventory(8, 55)
    }

}