package com.refinedmods.refinedstorage.data.sync

import io.netty.buffer.PooledByteBufAllocator
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.PacketContext
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.entity.data.TrackedDataHandler
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

/**
 * Watches underlying data for changes from the server, and
 * sync's changes to the player(s). The server is the source of truth.
 */
open class S2CSyncedData<T: SimpleObservable>(
    override val identifier: Identifier,
    override val isClient: Boolean,
    private var internalData: T,
    override val serializer: TrackedDataHandler<T>,
    private val players: Collection<PlayerEntity> = emptyList(),
    var onChanged: ((T)->Unit)? = null
) : Syncable<T> {
    // TODO If thread safe, consider storing it companion object
    private val byteBuffers: PooledByteBufAllocator =  PooledByteBufAllocator.DEFAULT

    override var data: T
        get() = internalData
        set(value) {
            val old = internalData
            internalData = value
            if(old != value) send()
        }

    override fun send() {
        if(isClient) return
        val byteBuffer = byteBuffers.buffer()
        try {
            val buf = PacketByteBuf(byteBuffer)
            this.serializer.write(buf, internalData)
            players.forEach { ServerSidePacketRegistry.INSTANCE.sendToPlayer(it, identifier, buf) }
        } finally { byteBuffer.release() }
    }

    override fun accept(ctx: PacketContext, buf: PacketByteBuf) {
        internalData = serializer.read(buf)
        ctx.taskQueue.execute {
            onChanged?.invoke(internalData)
        }
    }

    override fun registerClient() {
        if(isClient) ClientSidePacketRegistry.INSTANCE.register(identifier, this)
    }

    override fun unregisterClient() {
        if(isClient) ClientSidePacketRegistry.INSTANCE.unregister(identifier)
    }
}