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