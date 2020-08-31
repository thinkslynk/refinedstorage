package com.refinedmods.refinedstorage.network.tiledata

import com.refinedmods.refinedstorage.tile.data.TileDataManager
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.fabricmc.fabric.api.network.PacketConsumer
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.network.PacketByteBuf

class TileDataParameterUpdateMessage : PacketConsumer {
    override fun accept(context: PacketContext, buffer: PacketByteBuf) {
        val id: Int = buffer.readInt()
        val parameter = TileDataManager.getParameter(id)
        var value: Any? = null

        if (parameter != null) {
            try {
                value = parameter.serializer.read(buffer)
            } catch (e: Exception) {

            }
        }
    }
}

class TileDataParameterUpdateMessageOld<T: Any, E: BlockEntity>(
        private val parameter: TileDataParameter<T, E>,
        private val value: T
) {
    fun encode(buf: PacketByteBuf){
        val parameter = this.parameter
        buf.writeInt(parameter.id)
        parameter.serializer.write(buf, this.value)
    }

    companion object {
        fun decode(buf: PacketByteBuf): TileDataParameter<*,*>? {
            val id: Int = buf.readInt()
            val parameter = TileDataManager.getParameter(id)
            var value: Any? = null
            if (parameter != null) {
                try {
                    value = parameter.serializer.read(buf)
                    return null
                } catch (e: Exception) {
                    // NO OP
                }
            }
            return parameter
        }

        fun encode(message: TileDataParameterUpdateMessageOld<*,*>, buf: PacketByteBuf) {
             message.encode(buf)
        }



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
    }
}