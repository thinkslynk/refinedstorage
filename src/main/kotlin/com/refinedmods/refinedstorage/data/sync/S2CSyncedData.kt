package com.refinedmods.refinedstorage.data.sync

import com.refinedmods.refinedstorage.data.sync.Syncable.Companion.byteBuffers
import io.netty.buffer.PooledByteBufAllocator
import java.lang.ref.WeakReference
import net.fabricmc.fabric.api.network.PacketContext
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
    private val players: Collection<PlayerEntity> = emptyList()
) : Syncable<T>, SimpleObserver where T: Trackable<T>, T:SimpleObservable{
    override val observers: HashSet<WeakReference<SimpleObserver>> = hashSetOf()

    init {
        internalData.observers.add(getReference())
    }

    override fun onUpdate() {
        // Notify our lister on this side, if we have anyone listening
        this.notifyObservers()

        // Send data to server
        send()
    }

    private fun getReference(): WeakReference<SimpleObserver>{
        return WeakReference(this)
    }

    override var data: T
        get() = internalData
        set(value) {
            // Store what we had
            val old = internalData

            // Set new value
            internalData = value

            // Register for changes to the new object
            internalData.observers.add(getReference())

            // Send to server if the new object is actually different, otherwise save the bandwidth
            if(old != value) send()

            // Notify our lister on this side, if we have anyone listening
            notifyObservers()
        }

    override fun send() {
        if(isClient) return
        val byteBuffer = byteBuffers.buffer()
        try {
            val buf = PacketByteBuf(byteBuffer)
            internalData.getSerializer().write(buf, internalData)
            players.forEach { getServerRegistry().sendToPlayer(it, identifier, buf) }
        } finally { byteBuffer.release() }
    }

    override fun accept(ctx: PacketContext, buf: PacketByteBuf) {
        internalData = internalData.getSerializer().read(buf)
        if(observers.isNotEmpty()) {
            notifyObservers()
        }
    }

    override fun register() {
        if(isClient) getClientRegistry().register(identifier, this)
    }

    override fun unregister() {
        if(isClient) getClientRegistry().unregister(identifier)
    }

}