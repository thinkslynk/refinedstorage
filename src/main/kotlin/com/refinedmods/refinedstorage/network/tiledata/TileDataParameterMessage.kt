package com.refinedmods.refinedstorage.network.tiledata

import com.refinedmods.refinedstorage.tile.data.TileDataManager
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.fabricmc.fabric.api.network.PacketConsumer
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.network.PacketByteBuf


class TileDataParameterMessage : PacketConsumer {
    override fun accept(context: PacketContext, buffer: PacketByteBuf) {
        val id: Int = buffer.readInt()
        val initial: Boolean = buffer.readBoolean()
        val parameter = TileDataManager.getParameter<Any, BlockEntity>(id)

        if (parameter != null) {
            try {
                parameter.setValue(initial, parameter.serializer.read(buffer))
            } catch (e: Exception) {
                // NO OP
            }
        }
    }
}
