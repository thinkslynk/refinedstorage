package com.refinedmods.refinedstorage.data.sync

import net.minecraft.entity.data.TrackedDataHandler

@FunctionalInterface
interface Trackable<T> {
    fun getSerializer(): TrackedDataHandler<T>
}