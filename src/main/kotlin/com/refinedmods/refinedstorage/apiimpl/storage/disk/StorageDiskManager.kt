package com.refinedmods.refinedstorage.apiimpl.storage.disk

import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskFactory
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskManager
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskProvider
import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.extensions.Constants
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.world.PersistentState
import org.jetbrains.annotations.Nullable
import java.util.*


class StorageDiskManager(name: String, private val world: ServerWorld) : PersistentState(name), IStorageDiskManager {
    private val disks: MutableMap<UUID, IStorageDisk<*>> = mutableMapOf()

    override operator fun get(id: UUID): IStorageDisk<*>? {
        return disks[id]
    }

    @Nullable
    override fun getByStack(disk: ItemStack): IStorageDisk<*>? {
        val provider = disk.item
        if (provider !is IStorageDiskProvider) {
            return null
        }
        return if (!provider.isValid(disk)) {
            null
        } else get(provider.getId(disk))
    }

    override fun getAll(): Map<UUID, IStorageDisk<*>> = disks

    override operator fun set(id: UUID, disk: IStorageDisk<*>) {
        require(!disks.containsKey(id)) { "Disks already contains id '$id'" }
        disks[id] = disk
    }

    override fun remove(id: UUID) {
        disks.remove(id)
    }

    override fun markForSaving() {
        markDirty()
    }

    override fun fromTag(tag: CompoundTag) {
        if (tag.contains(NBT_DISKS)) {
            val disksTag: ListTag = tag.getList(NBT_DISKS, Constants.NBT.COMPOUND_TAG)
            for (i in disksTag.indices) {
                val diskTag: CompoundTag = disksTag.getCompound(i)
                val id: UUID = diskTag.getUuid(NBT_DISK_ID)
                val data: CompoundTag = diskTag.getCompound(NBT_DISK_DATA)
                val type: String = diskTag.getString(NBT_DISK_TYPE)
                val factory: IStorageDiskFactory<*>? = API.storageDiskRegistry[Identifier(type)]
                if (factory != null) {
                    disks[id] = factory.createFromNbt(world, data)
                }
            }
        }
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        val disks = ListTag()
        for ((key, value) in this.disks) {
            val diskTag = CompoundTag()
            diskTag.putUuid(NBT_DISK_ID, key)
            diskTag.put(NBT_DISK_DATA, value.writeToNbt())
            diskTag.putString(NBT_DISK_TYPE, value.factoryId.toString())
            disks.add(diskTag)
        }
        tag.put(NBT_DISKS, disks)
        return tag
    }

    companion object {
        const val NAME = "refinedstorage_disks"
        private const val NBT_DISKS = "Disks"
        private const val NBT_DISK_ID = "Id"
        private const val NBT_DISK_TYPE = "Type"
        private const val NBT_DISK_DATA = "Data"
    }

}