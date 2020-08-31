package com.refinedmods.refinedstorage.tile.data

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.network.NetworkHandler
import com.refinedmods.refinedstorage.network.tiledata.TileDataParameterUpdateMessage
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.minecraft.block.entity.BlockEntity
import net.minecraft.network.PacketByteBuf
import java.lang.ClassCastException
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer
import kotlin.collections.HashMap
import java.util.*

class TileDataManager(
        val tile: BlockEntity
) {
    private val parameters: MutableList<TileDataParameter<*, *>> = ArrayList()
    private val watchedParameters: MutableList<TileDataParameter<*, *>> = ArrayList()
    private val watchers: MutableList<TileDataWatcher> = CopyOnWriteArrayList()

    fun addParameter(parameter: TileDataParameter<*, *>) {
        parameters.add(parameter)
    }

    fun getParameters(): List<TileDataParameter<*, *>> {
        return parameters
    }

    fun addWatchedParameter(parameter: TileDataParameter<*, *>) {
        addParameter(parameter)
        watchedParameters.add(parameter)
    }

    fun getWatchedParameters(): List<TileDataParameter<*, *>> {
        return watchedParameters
    }

    fun addWatcher(listener: TileDataWatcher) {
        watchers.add(listener)
    }

    fun removeWatcher(listener: TileDataWatcher?) {
        watchers.remove(listener)
    }

    fun sendParameterToWatchers(parameter: TileDataParameter<*, *>?) {
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


        fun <T: Any, E: BlockEntity> getParameter(id: Int): TileDataParameter<T, E>? {
            return try {
                REGISTRY[id] as TileDataParameter<T, E>?
            } catch (e: ClassCastException){
                null
            }
        }

        fun setParameter(parameter: TileDataParameter<*, *>?, value: Any?) {
            // TODO Setup network handler
            //RS.NETWORK_HANDLER.sendToServer(TileDataParameterUpdateMessage(parameter, value))


            var passedData: PacketByteBuf = PacketByteBuf(Unpooled.buffer())
//            passedData.writeInt(0)
            ClientSidePacketRegistry.INSTANCE.sendToServer(NetworkHandler.TILE_DATA_PARAMETER_UPDATE_MESSAGE_ID, passedData)
        }
    }

}