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
 * Watches underlying data for changes, from either the
 * client or server, and ensures both parties are in sync.
 * NOTE: Since there is no single source of truth, the final
 * synced value may be an unexpected value. Consider using
 * `S2CSyncedData` or `C2SSyncedData` instead.
 */
open class BiSyncedData<T: SimpleObservable>(
    override val identifier: Identifier,
    override val isClient: Boolean,
    protected var internalData: T,
    override val serializer: TrackedDataHandler<T>,
    protected val player: PlayerEntity,
    var onChanged: ((T)->Unit)? = null
) : Syncable<T>, SimpleObserver {
    // TODO If thread safe, consider storing it companion object
    protected val byteBuffers =  PooledByteBufAllocator.DEFAULT

    override var data: T
        get() = internalData
        set(value) {
            val old = internalData
            internalData = value
            if(old != value) send()
        }

    override fun send() {
        val byteBuffer = byteBuffers.buffer()
        try {
            val buf = PacketByteBuf(byteBuffer)
            this.serializer.write(buf, internalData)
            when(isClient) {
                true -> ClientSidePacketRegistry.INSTANCE.sendToServer(identifier, buf)
                false -> ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, identifier, buf)
            }
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
    override fun registerServer() {
        if(!isClient) ServerSidePacketRegistry.INSTANCE.register(identifier, this)
    }

    override fun unregisterClient() {
        if(isClient) ClientSidePacketRegistry.INSTANCE.unregister(identifier)
    }
    override fun unregisterServer() {
        if(!isClient) ServerSidePacketRegistry.INSTANCE.unregister(identifier)
    }

    override fun onUpdate() {
        send()
    }
}