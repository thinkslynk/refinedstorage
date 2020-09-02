package com.refinedmods.refinedstorage.network.tiledata

import com.refinedmods.refinedstorage.container.BaseContainer
import com.refinedmods.refinedstorage.tile.data.TileDataManager
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.fabricmc.fabric.api.network.PacketConsumer
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.network.PacketByteBuf
import java.util.function.BiConsumer

class TileDataParameterUpdateMessage : PacketConsumer {
    override fun accept(context: PacketContext, buffer: PacketByteBuf) {
        val id: Int = buffer.readInt()
        val parameter = TileDataManager.getParameter<Any, BlockEntity>(id)
        var value: Any? = null

        if (parameter != null) {
            try {
                value = parameter.serializer.read(buffer)
            } catch (e: Exception) {

            }
        }

        context.taskQueue.execute {
            var screenHandler = context.player.currentScreenHandler

            if(screenHandler is BaseContainer) {
                val consumer: BiConsumer<BlockEntity, Any> = parameter!!.valueConsumer!!

                consumer?.accept((screenHandler as BaseContainer).tile, value!!)
            }
        }
    }
}