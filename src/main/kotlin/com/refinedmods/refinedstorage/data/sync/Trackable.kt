package com.refinedmods.refinedstorage.data.sync

import net.minecraft.entity.data.TrackedDataHandler
import net.minecraft.network.PacketByteBuf

@FunctionalInterface
interface Trackable<T> {
    fun getSerializer(): TrackedDataHandler<T>

    fun load(buf: PacketByteBuf): T {
        return getSerializer().read(buf)
    }

    fun save(buf: PacketByteBuf, obj: T): PacketByteBuf {
        getSerializer().write(buf, obj)
        return buf
    }
}