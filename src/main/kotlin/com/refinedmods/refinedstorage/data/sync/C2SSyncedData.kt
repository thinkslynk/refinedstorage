package com.refinedmods.refinedstorage.data.sync

import com.refinedmods.refinedstorage.data.sync.Syncable.Companion.byteBuffers
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

/**
 * Watches underlying data for changes from the client, and
 * sync's changes to the server. The client is the source of truth.
 */
class C2SSyncedData<T>(
    identifier: Identifier,
    isClient: Boolean,
    internalData: T,
    player: PlayerEntity
) : BiSyncedData<T>(identifier, isClient, internalData, player)
        where T: Trackable<T>, T:SimpleObservable {

    override fun send() {
        if(!isClient) return
        val byteBuffer = byteBuffers.buffer()
        val buf = PacketByteBuf(byteBuffer)
        internalData.getSerializer().write(buf, internalData)
        getClientRegistry().sendToServer(identifier, buf)
    }

    override fun register() {
        if(!isClient) getServerRegistry().register(identifier, this)
    }

    override fun unregister() {
        if(!isClient) getServerRegistry().unregister(identifier)
    }
}