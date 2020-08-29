package com.refinedmods.refinedstorage.container.transfer

import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import reborncore.api.items.InventoryUtils
import java.util.*
import java.util.function.Function
import java.util.function.Supplier

class TransferManager(
        private val container: ScreenHandler
) {
    private val fromToMap: MutableMap<IInventoryWrapper, MutableList<IInventoryWrapper>> = HashMap()

    private var notFoundHandler: Function<Int, ItemStack>? = null
    fun clearTransfers() {
        fromToMap.clear()
    }

    fun setNotFoundHandler(handler: Function<Int, ItemStack>?) {
        notFoundHandler = handler
    }

    fun addTransfer(from: Inventory, to: Inventory) {
        addTransfer(InventoryInventoryWrapper(from), InventoryInventoryWrapper(to))
    }

    fun addFilterTransfer(from: Inventory, itemTo: Inventory, fluidTo: FluidInventory, typeGetter: Supplier<Int>) {
        addTransfer(InventoryInventoryWrapper(from), FilterInventoryWrapper(itemTo, fluidTo, typeGetter))
    }
//
//    fun addItemFilterTransfer(from: Inventory, to: Inventory) {
//        addTransfer(InventoryInventoryWrapper(from), ItemFilterInventoryWrapper(to))
//    }
//
//    fun addFluidFilterTransfer(from: Inventory, to: FluidInventory) {
//        addTransfer(InventoryInventoryWrapper(from), FluidFilterInventoryWrapper(to))
//    }
//
    fun addBiTransfer(from: Inventory, to: Inventory) {
        addTransfer(from, to)
        addTransfer(to, from)
    }
//
    private fun addTransfer(from: IInventoryWrapper, to: IInventoryWrapper) {
        val toList = fromToMap.computeIfAbsent(from) {LinkedList() }
        toList.add(to)
    }

    fun transfer(index: Int): ItemStack {
        val slot: Slot = container.getSlot(index)
        val key: IInventoryWrapper = InventoryInventoryWrapper(slot.inventory)
//        key = if (slot is SlotItemHandler) {
//            ItemHandlerInventoryWrapper((slot as SlotItemHandler).getItemHandler())
//        } else {
//            InventoryInventoryWrapper(slot.inventory)
//        }
        val toList: List<IInventoryWrapper>? = fromToMap[key]
        if (toList != null) {
            val initial: ItemStack = slot.stack.copy()
            var remainder: ItemStack = slot.stack
            for (to in toList) {
                val result = to.insert(remainder)
                if (result.type == InsertionResultType.STOP) {
                    break
                } else if (result.type == InsertionResultType.CONTINUE_IF_POSSIBLE) {
                    remainder = result.value!!
                    if (remainder.isEmpty) {
                        break
                    }
                }
            }
            slot.stack = remainder
            slot.markDirty()
            if (instance().comparer.isEqual(remainder, initial) && notFoundHandler != null) {
                return notFoundHandler!!.apply(index)
            }
        } else if (notFoundHandler != null) {
            return notFoundHandler!!.apply(index)
        }
        return ItemStack.EMPTY
    }

}