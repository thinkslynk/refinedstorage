package com.refinedmods.refinedstorage.gui.screenhandlers

import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot
import com.refinedmods.refinedstorage.container.slot.legacy.LegacyDisabledSlot
import com.refinedmods.refinedstorage.container.slot.legacy.LegacyFilterSlot
import com.refinedmods.refinedstorage.data.BaseBlockEntityData
import com.refinedmods.refinedstorage.extensions.getCustomLogger
import java.util.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.server.network.ServerPlayerEntity
import reborncore.common.fluid.container.FluidInstance

abstract class BaseScreenHandler(
    windowId: Int,
    val player: PlayerEntity,
    private val entityData: BaseBlockEntityData? = null,
    type: ScreenHandlerType<*>
) : ScreenHandler(type, windowId) {

    val playerInventory: PlayerInventory = player.inventory
    val fluidSlots: MutableList<FluidFilterSlot> = mutableListOf()
    private val fluids: MutableList<FluidInstance> = ArrayList()

    companion object{
        protected val log = getCustomLogger(BaseScreenHandler::class)
    }

    protected fun addPlayerInventory(xInventory: Int, yInventory: Int) {
        val disabledSlotNumber = disabledSlotNumber
        var id = 9
        repeat(3) { y ->
            repeat(9) { x ->
                when (id) {
                    disabledSlotNumber -> addSlot(LegacyDisabledSlot(playerInventory, id, xInventory + x * 18, yInventory + y * 18))
                    else -> addSlot(Slot(playerInventory, id, xInventory + x * 18, yInventory + y * 18))
                }
                id++
            }
        }

        repeat(9) { i ->
            val x = xInventory + i * 18
            val y = yInventory + 4 + 3 * 18
            when (i) {
                disabledSlotNumber -> addSlot(LegacyDisabledSlot(playerInventory, i, x, y))
                else -> addSlot(Slot(playerInventory, i, x, y))
            }
        }
    }

    override fun canUse(player: PlayerEntity): Boolean = true

    override fun onSlotClick(id: Int, dragType: Int, clickType: SlotActionType, playerEntity: PlayerEntity): ItemStack {
        val slot: Slot? = if (id >= 0) getSlot(id) else null
        val disabledSlotNumber = disabledSlotNumber

        // Prevent swapping disabled held item with the number keys (dragType is the slot we're swapping with)
        if (disabledSlotNumber != -1 && clickType == SlotActionType.SWAP && dragType == disabledSlotNumber) {
            return ItemStack.EMPTY
        }

        when (slot) {
            // Filter slot
            is FilterSlot -> {
                when {
                    slot.isSizeAllowed -> {
                        when {
                            clickType == SlotActionType.QUICK_MOVE -> slot.setStack(ItemStack.EMPTY)
                            !playerInventory.cursorStack.isEmpty -> slot.setStack(playerInventory.cursorStack.copy())
                        }
                    }

                    slot.canInsert(playerInventory.cursorStack) -> slot.setStack(playerInventory.cursorStack.copy())
                    playerInventory.getStack(id).isEmpty -> slot.setStack(ItemStack.EMPTY)
                }

                return playerInventory.getStack(id)
            }

            // Filter slots
            is FluidFilterSlot -> {
                when {
                    slot.isSizeAllowed -> {
                        when {
                            clickType == SlotActionType.QUICK_MOVE -> slot.onContainerClicked(ItemStack.EMPTY)
                            !playerInventory.cursorStack.isEmpty -> slot.onContainerClicked(playerInventory.cursorStack)
                        }
                    }
                    playerInventory.cursorStack.isEmpty -> (slot as FluidFilterSlot?)!!.onContainerClicked(ItemStack.EMPTY)
                    else -> slot.onContainerClicked(playerInventory.cursorStack)
                }
                return playerInventory.cursorStack
            }

            // Legacy filter slots
            is LegacyFilterSlot -> {
                when {
                    playerInventory.cursorStack.isEmpty -> slot.setStack(ItemStack.EMPTY)
                    slot.canInsert(playerInventory.cursorStack) -> slot.setStack(playerInventory.cursorStack.copy())
                }

                return playerInventory.cursorStack
            }

            // Legacy disable slots
            is LegacyDisabledSlot -> return ItemStack.EMPTY

            else -> return super.onSlotClick(id, dragType, clickType, player)
        }

    }

    private val isTileStillThere: Boolean
        get() {
            val entity = entityData?.let{ player.world.getBlockEntity(it.blockPos) } ?: return false
            return !entity.isRemoved
        }

    open fun canMergeSlot(stack: ItemStack?, slot: Slot): Boolean =
        if (slot is FilterSlot || slot is LegacyFilterSlot || slot is FluidFilterSlot) {
            false
        } else super.canInsertIntoSlot(stack, slot)

    protected open val disabledSlotNumber: Int = -1

    override fun addSlot(slot: Slot): Slot {
        if (slot is FluidFilterSlot) {
            fluids.add(FluidInstance.EMPTY)
            fluidSlots.add(slot)
        }

        return super.addSlot(slot)
    }

    override fun sendContentUpdates() {
        super.sendContentUpdates()

        if (player is ServerPlayerEntity) {
            for (i in fluidSlots.indices) {
                val slot = fluidSlots[i]
                val cached: FluidInstance = fluids[i]
                val actual: FluidInstance = slot.fluidInventory.getFluid(slot.id)
                if (!API.comparer.isEqual(cached, actual, IComparer.COMPARE_QUANTITY or IComparer.COMPARE_NBT)) {
                    fluids[i] = actual.copy()
                    // TODO Fluid message
//                    RS.NETWORK_HANDLER.sendTo(player, FluidFilterSlotUpdateMessage(slot.id, actual))
                }
            }
        }
    }
}