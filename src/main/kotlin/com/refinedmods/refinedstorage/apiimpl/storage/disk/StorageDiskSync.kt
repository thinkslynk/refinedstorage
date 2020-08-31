package com.refinedmods.refinedstorage.apiimpl.storage.disk

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskSync
import com.refinedmods.refinedstorage.api.storage.disk.StorageDiskSyncData
import com.refinedmods.refinedstorage.network.NetworkHandler
import com.refinedmods.refinedstorage.network.disk.StorageDiskSizeRequestMessage
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.minecraft.network.PacketByteBuf
import java.util.*

class StorageDiskSync : IStorageDiskSync {
    private val data: MutableMap<UUID, StorageDiskSyncData> = HashMap()
    private val syncTime: MutableMap<UUID, Long> = HashMap()

    override fun getData(id: UUID): StorageDiskSyncData? {
        return data[id]
    }

    fun setData(id: UUID, data: StorageDiskSyncData) {
        this.data[id] = data
    }

    override fun sendRequest(id: UUID) {
        val lastSync = syncTime.getOrDefault(id, 0L)
        if (System.currentTimeMillis() - lastSync > THROTTLE_MS) {
            val passedData: PacketByteBuf = PacketByteBuf(Unpooled.buffer())
            passedData.writeUuid(id)
            ClientSidePacketRegistry.INSTANCE.sendToServer(NetworkHandler.STORAGE_DISK_SIZE_REQUEST_MESSAGE_ID, passedData)
            syncTime[id] = System.currentTimeMillis()
        }
    }

    companion object {
        private const val THROTTLE_MS = 500
    }
}