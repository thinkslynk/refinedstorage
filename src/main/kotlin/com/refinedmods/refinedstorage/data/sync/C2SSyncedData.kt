package com.refinedmods.refinedstorage.data.sync

import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.minecraft.entity.data.TrackedDataHandler
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

/**
 * Watches underlying data for changes from the client, and
 * sync's changes to the server. The client is the source of truth.
 */
class C2SSyncedData<T: SimpleObservable>(
    identifier: Identifier,
    isClient: Boolean,
    internalData: T,
    serializer: TrackedDataHandler<T>,
    player: PlayerEntity,
    onChanged: ((T)->Unit)? = null
) : BiSyncedData<T>(identifier, isClient, internalData, serializer, player, onChanged) {

    override fun send() {
        if(!isClient) return
        val byteBuffer = byteBuffers.buffer()
        try {
            val buf = PacketByteBuf(byteBuffer)
            this.serializer.write(buf, internalData)
            ClientSidePacketRegistry.INSTANCE.sendToServer(identifier, buf)
        } finally { byteBuffer.release() }
    }

    override fun registerClient() {}
    override fun unregisterClient() {}
}