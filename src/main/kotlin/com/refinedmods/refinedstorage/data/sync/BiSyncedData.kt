package com.refinedmods.refinedstorage.data.sync

import io.netty.buffer.PooledByteBufAllocator
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.PacketContext
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
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
open class BiSyncedData<T>(
    override val identifier: Identifier,
    override val isClient: Boolean,
    protected var internalData: T,
    protected val player: PlayerEntity,
    var onChanged: ((T)->Unit)? = null
) : Syncable<T> where T: Trackable<T>, T: SimpleObservable {
    // TODO If thread safe, consider storing it companion object
    protected val byteBuffers: PooledByteBufAllocator =  PooledByteBufAllocator.DEFAULT

    private val observer = object: SimpleObserver {
        override fun onUpdate() {
            // Notify our lister on this side, if we have anyone listening
            onChanged?.invoke(internalData)

            // Send data to server
            send()
        }
    }

    init {
        internalData.observers.add(observer)
    }

    override var data: T
        get() = internalData
        set(value) {
            // Store what we had
            val old = internalData

            // Set new value
            internalData = value

            // Unregister for changes to the old object
            old.observers.remove(observer)

            // Register for changes to the new object
            internalData.observers.add(observer)

            // Send to server if the new object is actually different, otherwise save the bandwidth
            if(old != value) send()

            // Notify our lister on this side, if we have anyone listening
            onChanged?.invoke(internalData)
        }

    override fun send() {
        val byteBuffer = byteBuffers.buffer()
        try {
            val buf = PacketByteBuf(byteBuffer)
            internalData.getSerializer().write(buf, internalData)
            when(isClient) {
                true -> getClientRegistry().sendToServer(identifier, buf)
                false -> getServerRegistry().sendToPlayer(player, identifier, buf)
            }
        } finally { byteBuffer.release() }
    }

    override fun accept(ctx: PacketContext, buf: PacketByteBuf) {
        internalData = internalData.getSerializer().read(buf)
        ctx.taskQueue.execute {
            onChanged?.invoke(internalData)
        }
    }

    // These are only here so that they can be mocked
    protected fun getClientRegistry(): ClientSidePacketRegistry = ClientSidePacketRegistry.INSTANCE
    protected fun getServerRegistry(): ServerSidePacketRegistry = ServerSidePacketRegistry.INSTANCE

    override fun registerClient() {
        if(isClient) getClientRegistry().register(identifier, this)
    }
    override fun registerServer() {
        if(!isClient) getServerRegistry().register(identifier, this)
    }

    override fun unregisterClient() {
        if(isClient) getClientRegistry().unregister(identifier)
    }
    override fun unregisterServer() {
        if(!isClient) getServerRegistry().unregister(identifier)
    }
}