package com.refinedmods.refinedstorage.network.tiledata

import com.refinedmods.refinedstorage.tile.data.TileDataManager
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.block.entity.BlockEntity
import net.minecraft.network.PacketByteBuf

class TileDataParameterMessage<T: Any, E: BlockEntity>(
        private val tile: E,
        private val parameter: TileDataParameter<T, E>?,
        private val initial: Boolean
) {

    companion object {
        fun <T: Any, E: BlockEntity> decode(buf: PacketByteBuf): Boolean? {
            val id: Int = buf.readInt()
            val initial: Boolean = buf.readBoolean()
            val parameter = TileDataManager.getParameter<T, E>(id)
            if (parameter != null) {
                try {
                    parameter.setValue(initial, parameter.serializer.read(buf))
                    return null
                } catch (e: Exception) {
                    // NO OP
                }
            }
            return initial
        }

        fun <T: Any, E: BlockEntity> encode(message: TileDataParameterMessage<T, E>, buf: PacketByteBuf) {
            buf.writeInt(message.parameter!!.id)
            buf.writeBoolean(message.initial)
            message.parameter.serializer.write(buf, message.parameter.valueProducer.apply(message.tile))
        }

//        fun <T: Any?, E: BlockEntity?> handle(message: TileDataParameterMessage<T?, E?>?, ctx: Supplier<NetworkEvent.Context>) {
//            ctx.get().setPacketHandled(true)
//        }
    }

}