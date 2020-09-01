package com.refinedmods.refinedstorage.network.disk

import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk
import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.network.NetworkHandler
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.PacketConsumer
import net.fabricmc.fabric.api.network.PacketContext
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.world.ServerWorld
import java.util.*

class StorageDiskSizeRequestMessage: PacketConsumer {
    override fun accept(context: PacketContext, buffer: PacketByteBuf) {
        val id: UUID = buffer.readUuid()

        context.taskQueue.execute {
            val disk: IStorageDisk<*>? = API.getStorageDiskManager(context.player.world as ServerWorld).get(id)

            if (disk != null) {
                val passedData: PacketByteBuf = PacketByteBuf(Unpooled.buffer())
                passedData.writeUuid(id)
                passedData.writeInt(disk.getStored())
                passedData.writeInt(disk.capacity)
                ServerSidePacketRegistry.INSTANCE.sendToPlayer(context.player, NetworkHandler.STORAGE_DISK_SIZE_RESPONSE_MESSAGE_ID, passedData)
            }
        }
    }

}

//class StorageDiskSizeRequestMessage(private val id: UUID) {
//    companion object {
//        fun decode(buf: PacketByteBuf): StorageDiskSizeRequestMessage {
//            return StorageDiskSizeRequestMessage(buf.readUuid())
//        }
//
//        fun encode(message: StorageDiskSizeRequestMessage, buf: PacketByteBuf) {
//            buf.writeUuid(message.id)
//        }
//
////        fun handle(message: StorageDiskSizeRequestMessage, ctx: Supplier<NetworkEvent.Context>) {
////            ctx.get().enqueueWork({
////                val disk = instance().getStorageDiskManager(ctx.get().getSender().getServerWorld())!![message.id]
////                if (disk != null) {
////                    RS.NETWORK_HANDLER.sendTo(ctx.get().getSender(), StorageDiskSizeResponseMessage(message.id, disk.getStored(), disk.capacity))
////                }
////            })
////            ctx.get().setPacketHandled(true)
////        }
//    }
//}