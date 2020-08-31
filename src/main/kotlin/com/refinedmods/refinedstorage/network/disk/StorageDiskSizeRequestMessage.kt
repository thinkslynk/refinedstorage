package com.refinedmods.refinedstorage.network.disk

import net.minecraft.network.PacketByteBuf
import java.util.*

class StorageDiskSizeRequestMessage(private val id: UUID) {
    companion object {
        fun decode(buf: PacketByteBuf): StorageDiskSizeRequestMessage {
            return StorageDiskSizeRequestMessage(buf.readUuid())
        }

        fun encode(message: StorageDiskSizeRequestMessage, buf: PacketByteBuf) {
            buf.writeUuid(message.id)
        }

//        fun handle(message: StorageDiskSizeRequestMessage, ctx: Supplier<NetworkEvent.Context>) {
//            ctx.get().enqueueWork({
//                val disk = instance().getStorageDiskManager(ctx.get().getSender().getServerWorld())!![message.id]
//                if (disk != null) {
//                    RS.NETWORK_HANDLER.sendTo(ctx.get().getSender(), StorageDiskSizeResponseMessage(message.id, disk.getStored(), disk.capacity))
//                }
//            })
//            ctx.get().setPacketHandled(true)
//        }
    }
}