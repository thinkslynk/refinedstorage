package com.refinedmods.refinedstorage.inventory.item

import com.refinedmods.refinedstorage.inventory.listener.InventoryListener
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import reborncore.api.items.InventoryBase
import reborncore.api.items.InventoryUtils
import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate

open class BaseItemHandler(size: Int): InventoryBase(size) {
    private val listeners: MutableList<InventoryListener<BaseItemHandler>> = ArrayList()
    private val validators: MutableList<Predicate<ItemStack>> = ArrayList()

    private var empty = true
    override fun isEmpty(): Boolean {
        return empty
    }

    private var reading = false
    fun addValidator(validator: Predicate<ItemStack>): BaseItemHandler {
        validators.add(validator)
        return this
    }

    fun addListener(listener: InventoryListener<BaseItemHandler>): BaseItemHandler {
        listeners.add(listener)
        return this
    }

    open fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        if (validators.isNotEmpty()) {
            for (validator in validators) {
                if (validator.test(stack)) {
                    return InventoryUtils.insertItemStacked(this, stack, simulate)
                }
            }
            return stack
        }

        return InventoryUtils.insertItemStacked(this, stack, simulate)
    }


    override fun setStack(id: Int, itemStack: ItemStack?) {
        super.setStack(id, itemStack)
        onChanged(id)
    }

    fun onChanged(slot: Int) {
        empty = stacks.stream().allMatch { obj: ItemStack -> obj.isEmpty }
        listeners.forEach(Consumer { l: InventoryListener<BaseItemHandler> -> l.onChanged(this, slot, reading) })
    }

    override fun deserializeNBT(tag: CompoundTag) {
        super.deserializeNBT(tag)
        empty = stacks.stream().allMatch { obj: ItemStack -> obj.isEmpty }
    }

    fun setReading(reading: Boolean) {
        this.reading = reading
    }
}