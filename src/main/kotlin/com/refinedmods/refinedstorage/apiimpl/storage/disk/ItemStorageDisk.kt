package com.refinedmods.refinedstorage.apiimpl.storage.disk

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import com.refinedmods.refinedstorage.api.storage.AccessType
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskContainerContext
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskListener
import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.apiimpl.storage.disk.factory.ItemStorageDiskFactory
import com.refinedmods.refinedstorage.extensions.ItemHandlerHelper
import com.refinedmods.refinedstorage.util.StackUtils.serializeStackToNbt
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier

class ItemStorageDisk(world: ServerWorld, capacity: Int) : IStorageDisk<ItemStack> {
    private val world: ServerWorld?
    override val capacity: Int
    val rawStacks: Multimap<Item, ItemStack> =
        ArrayListMultimap.create()

    private var listener: IStorageDiskListener? = null
    private var context: IStorageDiskContainerContext? = null // TODO not null

    override fun writeToNbt(): CompoundTag {
        val tag = CompoundTag()
        val list = ListTag()
        for (stack in rawStacks.values()) {
            list.add(serializeStackToNbt(stack!!))
        }
        tag.putInt(NBT_VERSION, VERSION)
        tag.put(NBT_ITEMS, list)
        tag.putInt(NBT_CAPACITY, capacity)
        return tag
    }

    override val factoryId: Identifier
        get() = ItemStorageDiskFactory.ID

    override fun getStacks(): Collection<ItemStack> {
        return rawStacks.values()
    }

    override fun insert(stack: ItemStack, size: Int, action: Action): ItemStack {
        if (stack.isEmpty) {
            return stack
        }
        for (otherStack in rawStacks[stack.item]) {
            if (API.comparer.isEqualNoQuantity(otherStack, stack)) {
                return if (capacity != -1 && getStored() + size > capacity) {
                    val remainingSpace = capacity - getStored()
                    if (remainingSpace <= 0) {
                        return ItemHandlerHelper.copyStackWithSize(stack, size)
                    }
                    if (action === Action.PERFORM) {
                        otherStack.increment(remainingSpace)
                        onChanged()
                    }
                    ItemHandlerHelper.copyStackWithSize(otherStack, size - remainingSpace)
                } else {
                    if (action === Action.PERFORM) {
                        otherStack.increment(size)
                        onChanged()
                    }
                    ItemStack.EMPTY
                }
            }
        }
        return if (capacity != -1 && getStored() + size > capacity) {
            val remainingSpace = capacity - getStored()
            if (remainingSpace <= 0) {
                return ItemHandlerHelper.copyStackWithSize(stack, size)
            }
            if (action === Action.PERFORM) {
                rawStacks.put(stack.item, ItemHandlerHelper.copyStackWithSize(stack, remainingSpace))
                onChanged()
            }
            ItemHandlerHelper.copyStackWithSize(stack, size - remainingSpace)
        } else {
            if (action === Action.PERFORM) {
                rawStacks.put(stack.item, ItemHandlerHelper.copyStackWithSize(stack, size))
                onChanged()
            }
            ItemStack.EMPTY
        }
    }


    override fun extract(stack: ItemStack, size: Int, flags: Int, action: Action): ItemStack {
        if (stack.isEmpty) {
            return stack
        }

        for (otherStack in rawStacks[stack.item]) {
            if (API.comparer.isEqual(otherStack, stack, flags)) {
                val resultSize = size.coerceAtMost(otherStack.count)
                if (action == Action.PERFORM) {
                    if (otherStack.count - resultSize == 0) {
                        rawStacks.remove(otherStack.item, otherStack)
                    } else {
                        otherStack.decrement(resultSize)
                    }
                    onChanged()
                }
                return ItemHandlerHelper.copyStackWithSize(otherStack, resultSize)
            }
        }
        return ItemStack.EMPTY
    }

    override fun getStored(): Int {
        return rawStacks.values().stream().mapToInt { obj: ItemStack -> obj.count }.sum()
    }

    override fun getPriority(): Int {
        return 0
    }

    override fun getAccessType(): AccessType {
        return context!!.getAccessType()!!
    }

    override fun setSettings(
        listener: IStorageDiskListener?,
        context: IStorageDiskContainerContext
    ) {
        this.listener = listener
        this.context = context
    }

    override fun getCacheDelta(storedPreInsertion: Int, size: Int, remainder: ItemStack?): Int {
        return when {
            getAccessType() == AccessType.INSERT -> 0
            remainder == null -> size
            else -> size - remainder.count
        }
    }

    private fun onChanged() {
        if (listener != null) {
            listener!!.onChanged()
        }
        if (world != null) {
            API.getStorageDiskManager(world).markForSaving()
        }
    }

    companion object {
        const val NBT_VERSION = "Version"
        const val NBT_CAPACITY = "Capacity"
        const val NBT_ITEMS = "Items"
        const val VERSION = 1
    }

    init {
        this.world = world
        this.capacity = capacity
    }
}


