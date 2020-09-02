package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.container.slot.legacy.LegacyDisabledSlot
import com.refinedmods.refinedstorage.container.slot.legacy.LegacyFilterSlot
import com.refinedmods.refinedstorage.container.transfer.TransferManager
import com.refinedmods.refinedstorage.tile.BaseTile
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import com.refinedmods.refinedstorage.tile.data.TileDataWatcher
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import reborncore.common.fluid.container.FluidInstance
import java.util.*

abstract class BaseContainer(
        type: ScreenHandlerType<*>?,
        context: ScreenHandlerContext,
        val player: PlayerEntity,
        windowId: Int
) : ScreenHandler(type, windowId) {

    private var listener: TileDataWatcher? = null
    protected val transferManager by lazy { TransferManager(this) }

    lateinit var tile: BaseTile
    init {
        context.run { world: World, blockPos: BlockPos ->
            tile = world.getBlockEntity(blockPos) as BaseTile
        }

        if (player is ServerPlayerEntity) {
            listener = TileDataWatcher(player, tile.dataManager)
        }
    }

//    private val fluidSlots: MutableList<FluidFilterSlot> = ArrayList()
    private val fluids: MutableList<FluidInstance> = ArrayList()
    protected fun addPlayerInventory(xInventory: Int, yInventory: Int) {
        val disabledSlotNumber = disabledSlotNumber
        var id = 9
        for (y in 0..2) {
            for (x in 0..8) {
                if (id == disabledSlotNumber) {
                    addSlot(LegacyDisabledSlot(player.inventory, id, xInventory + x * 18, yInventory + y * 18))
                } else {
                    addSlot(Slot(player.inventory, id, xInventory + x * 18, yInventory + y * 18))
                }
                id++
            }
        }
        id = 0
        for (i in 0..8) {
            val x = xInventory + i * 18
            val y = yInventory + 4 + 3 * 18
            if (id == disabledSlotNumber) {
                addSlot(LegacyDisabledSlot(player.inventory, id, x, y))
            } else {
                addSlot(Slot(player.inventory, id, x, y))
            }
            id++
        }
    }

    override fun canUse(player: PlayerEntity): Boolean { return true } // TODO Is this correct?

//    fun getFluidSlots(): List<FluidFilterSlot> {
//        return fluidSlots
//    }

    override fun onSlotClick(id: Int, dragType: Int, clickType: SlotActionType, playerEntity: PlayerEntity): ItemStack {
        val slot: Slot? = if (id >= 0) getSlot(id) else null
        val disabledSlotNumber = disabledSlotNumber

        // Prevent swapping disabled held item with the number keys (dragType is the slot we're swapping with)
        if (disabledSlotNumber != -1 && clickType == SlotActionType.SWAP && dragType == disabledSlotNumber) {
            return ItemStack.EMPTY
        }
        if (slot is FilterSlot) {
            if (slot.isSizeAllowed) {
                if (clickType == SlotActionType.QUICK_MOVE) {
                    slot.setStack(ItemStack.EMPTY)
                } else if (!player.inventory.getStack(id).isEmpty) {
                    slot.setStack(player.inventory.getStack(id).copy())
                }
            } else if (player.inventory.getStack(id).isEmpty) {
                slot.setStack(ItemStack.EMPTY)
            } else if (slot.canInsert(player.inventory.getStack(id))) {
                slot.setStack(player.inventory.getStack(id).copy())
            }
            return player.inventory.getStack(id)
        }
        // TODO Fluid
//        else if (slot is FluidFilterSlot) {
//            if ((slot as FluidFilterSlot?)!!.isSizeAllowed) {
//                if (clickType == SlotActionType.QUICK_MOVE) {
//                    (slot as FluidFilterSlot?)!!.onContainerClicked(ItemStack.EMPTY)
//                } else if (!player.inventory.getStack(id).isEmpty) {
//                    (slot as FluidFilterSlot?)!!.onContainerClicked(player.inventory.getStack(id))
//                }
//            } else if (player.inventory.getStack(id).isEmpty) {
//                (slot as FluidFilterSlot?)!!.onContainerClicked(ItemStack.EMPTY)
//            } else {
//                (slot as FluidFilterSlot?)!!.onContainerClicked(player.inventory.getStack(id))
//            }
//            return player.inventory.getStack(id)
//        }
    else if (slot is LegacyFilterSlot) {
            if (player.inventory.getStack(id).isEmpty) {
                slot.setStack(ItemStack.EMPTY)
            } else if (slot.canInsert(player.inventory.getStack(id))) {
                slot.setStack(player.inventory.getStack(id).copy())
            }
            return player.inventory.getStack(id)
        } else if (slot is LegacyDisabledSlot) {
            return ItemStack.EMPTY
        }
        return super.onSlotClick(id, dragType, clickType, player)
    }

    open fun transferStackInSlot(player: PlayerEntity?, slotIndex: Int): ItemStack? {
        return transferManager.transfer(slotIndex)
    }

    fun canInteractWith(player: PlayerEntity?): Boolean {
        return isTileStillThere
    }

    // @Volatile: Logic from LockableLootBlockEntity#isUsableByPlayer
    private val isTileStillThere: Boolean
        get() = tile.world!!.getBlockEntity(tile.pos) == tile

    open fun canMergeSlot(stack: ItemStack?, slot: Slot): Boolean {
        return if (slot is FilterSlot || slot is LegacyFilterSlot //|| slot is FluidFilterSlot // TODO Fluid
        ) {
            false
        } else super.canInsertIntoSlot(stack, slot)
    }

    protected open val disabledSlotNumber: Int
        get() = -1

    protected override fun addSlot(slot: Slot): Slot {
        // TODO Fluid
//        if (slot is FluidFilterSlot) {
//            fluids.add(FluidInstance.EMPTY)
//            fluidSlots.add(slot as FluidFilterSlot)
//        }
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
            // TODO Fluid
//            for (i in fluidSlots.indices) {
//                val slot = fluidSlots[i]
//                val cached: FluidInstance = fluids[i]
//                val actual: FluidInstance = slot.fluidInventory.getFluid(slot.id)
//                if (!instance().comparer.isEqual(cached, actual, IComparer.COMPARE_QUANTITY or IComparer.COMPARE_NBT)) {
//                    fluids[i] = actual.copy()
//                    // TODO Fluid message
////                    RS.NETWORK_HANDLER.sendTo(player as ServerPlayerEntity, FluidFilterSlotUpdateMessage(slot.id, actual))
//                }
//            }
        }
    }

    override fun close(player: PlayerEntity) {
        super.close(player)
        listener?.onClosed()
    }
}