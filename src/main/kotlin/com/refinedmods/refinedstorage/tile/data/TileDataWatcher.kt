package com.refinedmods.refinedstorage.tile.data

import com.refinedmods.refinedstorage.network.NetworkHandler
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.block.entity.BlockEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity

class TileDataWatcher(
        private val player: ServerPlayerEntity,
        private val manager: TileDataManager
) {
    private var sentInitial = false
    private val cache: Array<Any?> = arrayOfNulls(manager.getWatchedParameters().size)

    fun getPlayer(): ServerPlayerEntity {
        return player
    }

    fun onClosed() {
        manager.removeWatcher(this)
    }

    fun detectAndSendChanges() {
        if (!sentInitial) {
            manager.getParameters()
                    .forEach { p -> sendParameter(true, p) }
            sentInitial = true
        } else {
            for (i in manager.getWatchedParameters().indices) {
                val parameter = manager.getWatchedParameters()[i]

                val real: Any = parameter.valueProducer.apply(manager.tile)
                val cached = cache[i]
                if (real != cached) {
                    cache[i] = real

                    // Avoid sending watched parameter twice (after initial packet)
                    if (cached != null) {
                        sendParameter(false, parameter)
                    }
                }
            }
        }
    }

    fun <T: Any, E: BlockEntity> sendParameter(initial: Boolean, parameter: TileDataParameter<T, E>) {
        val passedData = PacketByteBuf(Unpooled.buffer())

        passedData.writeInt(parameter.id)
        passedData.writeBoolean(initial)
        val toWrite = parameter.valueProducer.apply(manager.tile as E)
        parameter.serializer.write(passedData, toWrite)
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, NetworkHandler.TILE_DATA_PARAMETER_MESSAGE_ID, passedData)
    }

    init {
        this.manager.addWatcher(this)
    }
}