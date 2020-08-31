package com.refinedmods.refinedstorage.apiimpl.storage.disk

import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskFactory
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskRegistry
import net.minecraft.util.Identifier

class StorageDiskRegistry : IStorageDiskRegistry {
    private val factories: MutableMap<Identifier, IStorageDiskFactory<*>> = mutableMapOf()

    override fun add(id: Identifier, factory: IStorageDiskFactory<*>) {
        factories[id] = factory
    }

    override operator fun get(id: Identifier): IStorageDiskFactory<*>? {
        return factories[id]
    }
}