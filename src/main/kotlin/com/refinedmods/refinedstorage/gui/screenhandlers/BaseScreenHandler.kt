@file:Suppress("DuplicatedCode")

package com.refinedmods.refinedstorage.gui.screenhandlers

import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot
import com.refinedmods.refinedstorage.container.slot.legacy.LegacyDisabledSlot
import com.refinedmods.refinedstorage.container.slot.legacy.LegacyFilterSlot
import com.refinedmods.refinedstorage.container.transfer.TransferManager
import com.refinedmods.refinedstorage.tile.BaseTile
import com.refinedmods.refinedstorage.tile.data.TileDataWatcher
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import java.util.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import reborncore.common.fluid.container.FluidInstance

abstract class BaseScreenHandler(
        type: ScreenHandlerType<*>,
        context: ScreenHandlerContext,
        val player: PlayerEntity,
        windowId: Int
) : SyncedGuiDescription(type, windowId, player.inventory) {

    private var listener: TileDataWatcher? = null
    protected val transferManager by lazy { TransferManager(this) }

    lateinit var tile: BaseTile
    init {
        this.world
        context.run { world: World, blockPos: BlockPos ->
            tile = world.getBlockEntity(blockPos) as BaseTile
        }

        if (player is ServerPlayerEntity) {
            listener = TileDataWatcher(player, tile.dataManager)
        }
    }

    val fluidSlots: MutableList<FluidFilterSlot> = mutableListOf()
    private val fluids: MutableList<FluidInstance> = ArrayList()

    protected fun addPlayerInventory(xInventory: Int, yInventory: Int) {
        val disabledSlotNumber = disabledSlotNumber
        var id = 9
        (0..2).forEach { y ->
            (0..8).forEach { x ->
                when (id) {
                    disabledSlotNumber -> addSlot(LegacyDisabledSlot(player.inventory, id, xInventory + x * 18, yInventory + y * 18))
                    else -> addSlot(Slot(player.inventory, id, xInventory + x * 18, yInventory + y * 18))
                }
                id++
            }
        }

        (0..8).forEach { i ->
            val x = xInventory + i * 18
            val y = yInventory + 4 + 3 * 18
            when (i) {
                disabledSlotNumber -> addSlot(LegacyDisabledSlot(player.inventory, i, x, y))
                else -> addSlot(Slot(player.inventory, i, x, y))
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
                            !player.inventory.getStack(id).isEmpty -> slot.setStack(player.inventory.getStack(id).copy())
                        }
                    }

                    player.inventory.getStack(id).isEmpty -> slot.setStack(ItemStack.EMPTY)
                    slot.canInsert(player.inventory.getStack(id)) -> slot.setStack(player.inventory.getStack(id).copy())
                }

                return player.inventory.getStack(id)
            }

            // Filter slots
            is FluidFilterSlot -> {
                when {
                    slot.isSizeAllowed -> {
                        when {
                            clickType == SlotActionType.QUICK_MOVE -> slot.onContainerClicked(ItemStack.EMPTY)
                            !player.inventory.getStack(id).isEmpty -> slot.onContainerClicked(player.inventory.getStack(id))
                        }
                    }
                    player.inventory.getStack(id).isEmpty -> (slot as FluidFilterSlot?)!!.onContainerClicked(ItemStack.EMPTY)
                    else -> slot.onContainerClicked(player.inventory.getStack(id))
                }
                return player.inventory.getStack(id)
            }

            // Legacy filter slots
            is LegacyFilterSlot -> {
                when {
                    player.inventory.getStack(id).isEmpty -> slot.setStack(ItemStack.EMPTY)
                    slot.canInsert(player.inventory.getStack(id)) -> slot.setStack(player.inventory.getStack(id).copy())
                }

                return player.inventory.getStack(id)
            }

            // Legacy disable slots
            is LegacyDisabledSlot -> return ItemStack.EMPTY

            else -> return super.onSlotClick(id, dragType, clickType, player)
        }

    }

    open fun transferStackInSlot(player: PlayerEntity, slotIndex: Int): ItemStack = transferManager.transfer(slotIndex)

    fun canInteractWith(player: PlayerEntity): Boolean = isTileStillThere

    private val isTileStillThere: Boolean = tile.world!!.getBlockEntity(tile.pos) == tile

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

        // Prevent sending changes about a tile that doesn't exist anymore.
        // This prevents crashes when sending network node data (network node would crash because it no longer exists and we're querying it from the various tile data parameters).
        if (isTileStillThere) {
            listener?.detectAndSendChanges()
        }

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

    override fun close(player: PlayerEntity) {
        super.close(player)
        listener?.onClosed()
    }
}