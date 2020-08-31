package com.refinedmods.refinedstorage.tile.data

import com.refinedmods.refinedstorage.network.NetworkHandler
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.minecraft.block.entity.BlockEntity
import net.minecraft.network.PacketByteBuf
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.HashMap

class TileDataManager<E:BlockEntity>(
        val tile: E
) {
    private val parameters: MutableList<TileDataParameter<*, E>> = ArrayList()
    private val watchedParameters: MutableList<TileDataParameter<*, E>> = ArrayList()
    private val watchers: MutableList<TileDataWatcher<E>> = CopyOnWriteArrayList()

    fun addParameter(parameter: TileDataParameter<*, E>) {
        parameters.add(parameter)
    }

    fun getParameters(): List<TileDataParameter<*,E>> {
        return parameters
    }

    fun addWatchedParameter(parameter: TileDataParameter<*,E>) {
        addParameter(parameter)
        watchedParameters.add(parameter)
    }

    fun getWatchedParameters(): List<TileDataParameter<*,E>> {
        return watchedParameters
    }

    fun addWatcher(listener: TileDataWatcher<E>) {
        watchers.add(listener)
    }

    fun removeWatcher(listener: TileDataWatcher<E>) {
        watchers.remove(listener)
    }

    fun sendParameterToWatchers(parameter: TileDataParameter<*,E>) {
        // TODO Send params
//        watchers.forEach(Consumer { l: TileDataWatcher -> l.sendParameter(false, parameter) })
    }

    companion object {
        private var LAST_ID = 0
        private val REGISTRY: MutableMap<Int, TileDataParameter<Any, BlockEntity>> = HashMap()
        fun registerParameter(parameter: TileDataParameter<Any, BlockEntity>) {
            parameter.id = LAST_ID
            REGISTRY[LAST_ID++] = parameter
        }


        fun getParameter(id: Int): TileDataParameter<*,*>? {
            return REGISTRY[id]
        }

        fun <T : Any>setParameter(parameter: TileDataParameter<T, *>, value: T) {
            // TODO Setup network handler
            //RS.NETWORK_HANDLER.sendToServer(TileDataParameterUpdateMessage(parameter, value))


            val passedData: PacketByteBuf = PacketByteBuf(Unpooled.buffer())
//            passedData.writeInt(0)
            ClientSidePacketRegistry.INSTANCE.sendToServer(NetworkHandler.TILE_DATA_PARAMETER_UPDATE_MESSAGE_ID, passedData)
        }
    }

}