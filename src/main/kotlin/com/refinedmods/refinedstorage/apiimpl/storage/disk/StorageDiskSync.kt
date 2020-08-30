package com.refinedmods.refinedstorage.apiimpl.storage.disk

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskSync
import com.refinedmods.refinedstorage.api.storage.disk.StorageDiskSyncData
import com.refinedmods.refinedstorage.network.disk.StorageDiskSizeRequestMessage
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
            RS.NETWORK_HANDLER.sendToServer(StorageDiskSizeRequestMessage(id))
            syncTime[id] = System.currentTimeMillis()
        }
    }

    companion object {
        private const val THROTTLE_MS = 500
    }
}