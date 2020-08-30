package com.refinedmods.refinedstorage.network.tiledata

import com.refinedmods.refinedstorage.tile.data.TileDataManager
import net.fabricmc.fabric.api.network.PacketConsumer
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.client.MinecraftClient
import net.minecraft.network.PacketByteBuf
import net.minecraft.particle.ParticleTypes


class TileDataParameterMessage : PacketConsumer {
    override fun accept(context: PacketContext, buffer: PacketByteBuf) {
        val id: Int = buffer.readInt()
        val initial: Boolean = buffer.readBoolean()
        val parameter = TileDataManager.getParameter(id)
        if (parameter != null) {
            try {
                parameter.setValue(initial, parameter.serializer.read(buffer))
            } catch (e: Exception) {
                // NO OP
            }
        }
    }

}

//class TileDataParameterMessage<T: Any?, E: BlockEntity?>(
//        private val tile: E?,
//        private val parameter: TileDataParameter<T?, E?>?,
//        private val initial: Boolean
//) {
//
//    companion object {
//        fun <T: Any?, E: BlockEntity?> decode(buf: PacketByteBuf): TileDataParameterMessage<T?, E?> {
//            val id: Int = buf.readInt()
//            val initial: Boolean = buf.readBoolean()
//            val parameter = TileDataManager.getParameter<T, E>(id)
//            if (parameter != null) {
//                try {
//                    parameter.setValue(initial, parameter.serializer.read(buf))
//                } catch (e: Exception) {
//                    // NO OP
//                }
//            }
//            return TileDataParameterMessage<T?, E?>(null, null, initial)
//        }
//
//        fun <T: Any?, E: BlockEntity?> encode(message: TileDataParameterMessage<T?, E?>, buf: PacketByteBuf) {
//            buf.writeInt(message.parameter!!.id)
//            buf.writeBoolean(message.initial)
//            message.parameter.serializer.write(buf, message.parameter.valueProducer.apply(message.tile))
//        }
//
////        fun <T: Any?, E: BlockEntity?> handle(message: TileDataParameterMessage<T?, E?>?, ctx: Supplier<NetworkEvent.Context>) {
////            ctx.get().setPacketHandled(true)
////        }
//    }
//
//}