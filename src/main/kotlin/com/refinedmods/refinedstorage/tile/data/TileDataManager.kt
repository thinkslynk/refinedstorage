package com.refinedmods.refinedstorage.tile.data

import com.refinedmods.refinedstorage.network.NetworkHandler
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.minecraft.block.entity.BlockEntity
import net.minecraft.network.PacketByteBuf
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.HashMap

class TileDataManager(
        val tile: BlockEntity
) {
    private val parameters: MutableList<TileDataParameter<Any, BlockEntity>> = ArrayList()
    private val watchedParameters: MutableList<TileDataParameter<Any, BlockEntity>> = ArrayList()
    private val watchers: MutableList<TileDataWatcher> = CopyOnWriteArrayList()

    fun <T: Any, E: BlockEntity> addParameter(parameter: TileDataParameter<T, E>) {
        parameters.add(parameter as TileDataParameter<Any, BlockEntity>)
    }

    fun getParameters(): List<TileDataParameter<Any, BlockEntity>> {
        return parameters
    }

    fun <T: Any, E: BlockEntity> addWatchedParameter(parameter: TileDataParameter<T, E>) {
        addParameter(parameter)
        watchedParameters.add(parameter as TileDataParameter<Any, BlockEntity>)
    }

    fun getWatchedParameters(): List<TileDataParameter<Any, BlockEntity>> {
        return watchedParameters
    }

    fun addWatcher(listener: TileDataWatcher) {
        watchers.add(listener)
    }

    fun removeWatcher(listener: TileDataWatcher?) {
        watchers.remove(listener)
    }

    fun <T: Any, E: BlockEntity> sendParameterToWatchers(parameter: TileDataParameter<T, E>?) {
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