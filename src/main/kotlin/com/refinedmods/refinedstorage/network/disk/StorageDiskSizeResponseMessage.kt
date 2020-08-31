package com.refinedmods.refinedstorage.network.disk

import com.refinedmods.refinedstorage.api.storage.disk.StorageDiskSyncData
import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.apiimpl.API.instance
import com.refinedmods.refinedstorage.apiimpl.storage.disk.StorageDiskSync
import net.fabricmc.fabric.api.network.PacketConsumer
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf
import java.util.*

class StorageDiskSizeResponseMessage : PacketConsumer {
    override fun accept(context: PacketContext, buffer: PacketByteBuf) {
        val id: UUID = buffer.readUuid()
        val stored: Int = buffer.readInt()
        val capacity: Int = buffer.readInt()

        context.taskQueue.execute {
            (API.storageDiskSync as StorageDiskSync).setData(id, StorageDiskSyncData(stored, capacity))
        }
    }

}

//class StorageDiskSizeResponseMessage(private val id: UUID, private val stored: Int, private val capacity: Int) {
//    companion object {
//        fun encode(message: StorageDiskSizeResponseMessage, buf: PacketByteBuf) {
//            buf.writeUuid(message.id)
//            buf.writeInt(message.stored)
//            buf.writeInt(message.capacity)
//        }
//
//        fun decode(buf: PacketByteBuf): StorageDiskSizeResponseMessage {
//            return StorageDiskSizeResponseMessage(buf.readUuid(), buf.readInt(), buf.readInt())
//        }
//
////        fun handle(message: StorageDiskSizeResponseMessage, ctx: Supplier<NetworkEvent.Context>) {
////            ctx.get().enqueueWork({ (instance().getStorageDiskSync() as StorageDiskSync?).setData(message.id, StorageDiskSyncData(message.stored, message.capacity)) })
////            ctx.get().setPacketHandled(true)
////        }
//    }
//}