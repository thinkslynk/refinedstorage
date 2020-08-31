package com.refinedmods.refinedstorage.tile.data

import com.refinedmods.refinedstorage.network.NetworkHandler
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import java.util.function.Consumer

class TileDataWatcher(
        private val player: ServerPlayerEntity,
        private val manager: TileDataManager?
) {
    private var sentInitial = false
    private val cache: Array<Any> by lazy {
        if (manager != null) {
            arrayOfNulls<Any>(manager.getWatchedParameters().size)
        }

        emptyArray<Any>()
    }
    fun getPlayer(): ServerPlayerEntity {
        return player
    }

    fun onClosed() {
        manager!!.removeWatcher(this)
    }

    fun detectAndSendChanges() {
        // TODO Detect and send
        if (!sentInitial) {
            manager!!.getParameters().forEach(Consumer { p: TileDataParameter<*, *> -> sendParameter(true, p) })
            sentInitial = true
        } else {
            for (i in manager!!.getWatchedParameters().indices) {
                val parameter = manager.getWatchedParameters()[i]
                val real = parameter.valueProducer.apply(manager.tile)
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

    fun sendParameter(initial: Boolean, parameter: TileDataParameter<*, *>) {
        val passedData: PacketByteBuf = PacketByteBuf(Unpooled.buffer())
        passedData.writeInt(parameter.id)
        passedData.writeBoolean(initial)
        parameter.serializer.write(passedData, parameter.valueProducer.apply(manager!!.tile))
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, NetworkHandler.TILE_DATA_PARAMETER_MESSAGE_ID, passedData)
    }

    init {
        if (manager != null) {
            this.manager.addWatcher(this)
        }
    }
}