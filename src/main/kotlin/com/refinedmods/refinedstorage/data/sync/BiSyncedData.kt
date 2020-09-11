package com.refinedmods.refinedstorage.data.sync

import com.refinedmods.refinedstorage.data.sync.Syncable.Companion.byteBuffers
import com.refinedmods.refinedstorage.extensions.getCustomLogger
import java.lang.ref.WeakReference
import net.fabricmc.fabric.api.network.PacketContext
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
    protected val player: PlayerEntity
) : Syncable<T>, SimpleObserver where T: Trackable<T>, T: SimpleObservable {
    override val observers: HashSet<WeakReference<SimpleObserver>> = hashSetOf()

    companion object{
        protected val log = getCustomLogger(BiSyncedData::class)
    }

    init {
        internalData.observers.add(getReference())
    }

    override fun onUpdate() {
        // Notify our lister on this side, if we have anyone listening
        this.notifyObservers()

        // Send data to server
        send()
    }

    private fun getReference(): WeakReference<SimpleObserver> {
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
        val byteBuffer = byteBuffers.buffer()
//        try {
            val buf = PacketByteBuf(byteBuffer)
            internalData.getSerializer().write(buf, internalData)
            when(isClient) {
                true -> getClientRegistry().sendToServer(identifier, buf)
                false -> getServerRegistry().sendToPlayer(player, identifier, buf)
            }
//        } finally { byteBuffer.release() }
    }

    override fun accept(ctx: PacketContext, buf: PacketByteBuf) {
        internalData = internalData.getSerializer().read(buf)
        if (observers.isNotEmpty()) {
            ctx.taskQueue.execute {
                notifyObservers()
            }
        }
    }

    override fun register() {
        when(isClient) {
            true -> getClientRegistry().register(identifier, this)
            false -> getServerRegistry().register(identifier, this)
        }
    }

    override fun unregister() {
        when(isClient) {
            true -> getClientRegistry().unregister(identifier)
            false -> getServerRegistry().unregister(identifier)
        }
    }
}