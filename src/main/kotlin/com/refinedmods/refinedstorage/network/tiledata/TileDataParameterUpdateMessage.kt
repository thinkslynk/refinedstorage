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

//class TileDataParameterUpdateMessageOld<T: Any, E: BlockEntity>(
//        private val parameter: TileDataParameter<T, E>?,
//        private val value: T
//) {
//    companion object {
//        fun <T: Any, E: BlockEntity> decode(buf: PacketByteBuf): TileDataParameter<T, E>? {
//            val id: Int = buf.readInt()
//            val parameter = TileDataManager.getParameter<T, E>(id)
//            var value: T? = null
//            if (parameter != null) {
//                try {
//                    value = parameter.serializer.read(buf)
//                    return null
//                } catch (e: Exception) {
//                    // NO OP
//                }
//            }
//            return parameter
//        }
//
//        fun <T: Any, E: BlockEntity> encode(message: TileDataParameterUpdateMessageOld<T, E>, buf: PacketByteBuf) {
//            message.parameter?.let {
//                buf.writeInt(it.id)
//                it.serializer.write(buf, message.value)
//            }
//        }

//        // TODO Not sure what the equivalent is for NetworkEvents
////        fun <T, E: BlockEntity> handle(message: TileDataParameterUpdateMessage<T, E>, ctx: Supplier<NetworkEvent.Context>) {
////
////            ctx.get().enqueueWork({
////                val c: Container = ctx.get().getSender().openContainer
////                if (c is BaseContainer) {
////                    val consumer: BiConsumer<*, *>? = message.parameter.valueConsumer
////                    consumer?.accept((c as BaseContainer).tile, message.value)
////                }
////            })
////            ctx.get().setPacketHandled(true)
////        }
//    }
//}