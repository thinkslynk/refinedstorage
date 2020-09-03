package com.refinedmods.refinedstorage.data.sync

import io.netty.buffer.PooledByteBufAllocator
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.PacketContext
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

/**
 * Watches underlying data for changes from the server, and
 * sync's changes to the player(s). The server is the source of truth.
 */
open class S2CSyncedData<T>(
    override val identifier: Identifier,
    override val isClient: Boolean,
    private var internalData: T,
    private val players: Collection<PlayerEntity> = emptyList(),
    var onChanged: ((T)->Unit)? = null
) : Syncable<T> where T: Trackable<T>, T:SimpleObservable{
    // TODO If thread safe, consider storing it companion object
    private val byteBuffers: PooledByteBufAllocator =  PooledByteBufAllocator.DEFAULT

    private val observer = object: SimpleObserver {
        override fun onUpdate() {
            send()
        }
    }

    init {
        internalData.observers.add(observer)
    }

    override var data: T
        get() = internalData
        set(value) {
            val old = internalData
            internalData = value
            internalData.observers.add(observer)
            if(old != value) send()
        }

    override fun send() {
        if(isClient) return
        val byteBuffer = byteBuffers.buffer()
        try {
            val buf = PacketByteBuf(byteBuffer)
            internalData.getSerializer().write(buf, internalData)
            players.forEach { ServerSidePacketRegistry.INSTANCE.sendToPlayer(it, identifier, buf) }
        } finally { byteBuffer.release() }
    }

    override fun accept(ctx: PacketContext, buf: PacketByteBuf) {
        internalData = internalData.getSerializer().read(buf)
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