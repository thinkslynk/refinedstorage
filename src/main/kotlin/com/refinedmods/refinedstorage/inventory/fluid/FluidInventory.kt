package com.refinedmods.refinedstorage.inventory.fluid

import com.refinedmods.refinedstorage.inventory.listener.InventoryListener
import net.minecraft.nbt.CompoundTag
import reborncore.common.fluid.container.FluidInstance
import java.util.*
import java.util.function.Consumer

open class FluidInventory @JvmOverloads constructor(size: Int, val maxAmount: Int = Int.MAX_VALUE)
{
    private val listeners: MutableList<InventoryListener<FluidInventory>> = ArrayList()
    private val fluids: Array<FluidInstance> = Array(size) { FluidInstance.EMPTY }
    var isEmpty = true
        private set

    fun addListener(listener: InventoryListener<FluidInventory>): FluidInventory {
        listeners.add(listener)
        return this
    }

    fun getSlots(): Int {
        return fluids.size
    }

    fun getFluids(): Array<FluidInstance> {
        return fluids
    }

    fun getFluid(slot: Int): FluidInstance {
        return fluids[slot]
    }

    fun setFluid(slot: Int, stack: FluidInstance) {
        require(stack.amount.rawValue <= maxAmount) { "Fluid size is invalid (given: " + stack.amount.rawValue.toString() + ", max size: " + maxAmount.toString() + ")" }
        fluids[slot] = stack
        onChanged(slot)
    }

    fun onChanged(slot: Int) {
        listeners.forEach(Consumer { l: InventoryListener<FluidInventory> -> l.onChanged(this, slot, false) })
        updateEmptyState()
    }

    fun writeToNbt(): CompoundTag {
        val tag = CompoundTag()
        for (i in 0 until getSlots()) {
            val stack: FluidInstance = getFluid(i)
            if (!stack.isEmpty) {
                tag.put(String.format(NBT_SLOT, i), stack.write())
            }
        }
        return tag
    }

    fun readFromNbt(tag: CompoundTag) {
        for (i in 0 until getSlots()) {
            val key = String.format(NBT_SLOT, i)
            if (tag.contains(key)) {
                val fluidInstance = FluidInstance()
                fluidInstance.read(tag.getCompound(key))
                fluids[i] = fluidInstance
            }
        }
        updateEmptyState()
    }

    private fun updateEmptyState() {
        isEmpty = true
        for (fluid in fluids) {
            if (!fluid.isEmpty()) {
                isEmpty = false
                return
            }
        }
    }

    companion object {
        private const val NBT_SLOT = "Slot_%d"
    }

}