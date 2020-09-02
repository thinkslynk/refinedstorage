package com.refinedmods.refinedstorage.apiimpl.network.node.diskdrive

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.storage.AccessType
import com.refinedmods.refinedstorage.api.storage.IStorage
import com.refinedmods.refinedstorage.api.storage.IStorageProvider
import com.refinedmods.refinedstorage.api.storage.cache.InvalidateCause
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDisk
import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskContainerContext
import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.apiimpl.network.node.ConnectivityStateChangeCause
import com.refinedmods.refinedstorage.apiimpl.network.node.DiskState
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode
import com.refinedmods.refinedstorage.apiimpl.storage.cache.FluidStorageCache
import com.refinedmods.refinedstorage.apiimpl.storage.cache.ItemStorageCache
import com.refinedmods.refinedstorage.extensions.CompoundNBT
import com.refinedmods.refinedstorage.extensions.ResourceLocation
import com.refinedmods.refinedstorage.extensions.getStackInSlot
import com.refinedmods.refinedstorage.extensions.isRemote
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler
import com.refinedmods.refinedstorage.inventory.item.validator.StorageDiskItemValidator
import com.refinedmods.refinedstorage.inventory.listener.InventoryListener
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeFluidInventoryListener
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener
import com.refinedmods.refinedstorage.tile.DiskDriveTile
import com.refinedmods.refinedstorage.tile.config.*
import com.refinedmods.refinedstorage.util.AccessTypeUtils.readAccessType
import com.refinedmods.refinedstorage.util.AccessTypeUtils.writeAccessType
import com.refinedmods.refinedstorage.util.StackUtils
import com.refinedmods.refinedstorage.util.StackUtils.readItems
import com.refinedmods.refinedstorage.util.StackUtils.writeItems
import com.refinedmods.refinedstorage.util.WorldUtils.updateBlock
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.apache.logging.log4j.LogManager
import reborncore.common.fluid.container.FluidInstance

class DiskDriveNetworkNode(world: World, pos: BlockPos?) : NetworkNode(world, pos!!), IStorageProvider, IComparable, IWhitelistBlacklist, IPrioritizable, IType, IAccessType, IStorageDiskContainerContext {
    private var ticksSinceBlockUpdateRequested = 0
    private var blockUpdateRequested = false
    override val itemFilters = BaseItemHandler(9).addListener(NetworkNodeInventoryListener(this))
    override val fluidFilters = FluidInventory(9).addListener(NetworkNodeFluidInventoryListener(this))
    val itemDisks = arrayOfNulls<IStorageDisk<ItemStack>?>(8)
    val fluidDisks = arrayOfNulls<IStorageDisk<FluidInstance>?>(8)
    private val disks = BaseItemHandler(8)
            .addValidator(StorageDiskItemValidator())
            .addListener(NetworkNodeInventoryListener(this))
            .addListener(InventoryListener<BaseItemHandler> { handler: BaseItemHandler, slot: Int, reading: Boolean ->
                if (!world.isClient) {
                    StackUtils.createStorages(
                            world as ServerWorld,
                            handler.getStack(slot),
                            slot,
                            itemDisks,
                            fluidDisks,
                            { s -> ItemDriveWrapperStorageDisk(this@DiskDriveNetworkNode, s) }
                    ) { s -> FluidDriveWrapperStorageDisk(this@DiskDriveNetworkNode, s) }
                    if (network != null) {
                        network.getItemStorageCache().invalidate(InvalidateCause.DISK_INVENTORY_CHANGED)
                        network.getFluidStorageCache().invalidate(InvalidateCause.DISK_INVENTORY_CHANGED)
                    }
                    if (!reading) {
                        updateBlock(world, pos!!)
                    }
                }
            })
    override var accessType: AccessType = AccessType.INSERT_EXTRACT
        set(value) {
            field = value
            if (network != null) {
                network.getFluidStorageCache().invalidate(InvalidateCause.DEVICE_CONFIGURATION_CHANGED)
                network.getItemStorageCache().invalidate(InvalidateCause.DEVICE_CONFIGURATION_CHANGED)
            }
            markDirty()
        }

    override fun getAccessType(): AccessType {
        return accessType
    }

    override var priority: Int = 0
        set(priority) {
            field = priority
            markDirty()

            network?.getItemStorageCache().sort()
            network?.getFluidStorageCache().sort()
        }
    override var compare: Int = IComparer.COMPARE_NBT
        set(compare) {
            field = compare
            markDirty()
        }
    private var mode = IWhitelistBlacklist.BLACKLIST
    private override var type = IType.ITEMS

    override val energyUsage: Double
        get() {
            var usage: Double = RS.CONFIG.serverConfig.diskDrive.usage
            for (storage in itemDisks) {
                if (storage != null) {
                    usage += RS.CONFIG.serverConfig.diskDrive.diskUsage
                }
            }
            for (storage in fluidDisks) {
                if (storage != null) {
                    usage += RS.CONFIG.serverConfig.diskDrive.diskUsage
                }
            }
            return usage
        }

    override fun update() {
        super.update()
        if (blockUpdateRequested) {
            ++ticksSinceBlockUpdateRequested
            if (ticksSinceBlockUpdateRequested > DISK_STATE_UPDATE_THROTTLE) {
                updateBlock(world, pos)
                blockUpdateRequested = false
                ticksSinceBlockUpdateRequested = 0
            }
        } else {
            ticksSinceBlockUpdateRequested = 0
        }
    }

    fun requestBlockUpdate() {
        blockUpdateRequested = true
    }

    override fun onConnectedStateChange(network: INetwork, state: Boolean, cause: ConnectivityStateChangeCause?) {
        super.onConnectedStateChange(network, state, cause)
        LOGGER.debug("Connectivity state of disk drive at {} changed to {} due to {}", pos, state, cause)
        network.nodeGraph.runActionWhenPossible(ItemStorageCache.INVALIDATE.apply(InvalidateCause.CONNECTED_STATE_CHANGED))
        network.nodeGraph.runActionWhenPossible(FluidStorageCache.INVALIDATE.apply(InvalidateCause.CONNECTED_STATE_CHANGED))
        updateBlock(world, pos)
    }

    override fun addItemStorages(storages: List<IStorage<ItemStack>>) {
        for (storage in itemDisks) {
            if (storage != null) {
                storages.add(storage)
            }
        }
    }

    override fun addFluidStorages(storages: List<IStorage<FluidInstance>>) {
        for (storage in fluidDisks) {
            if (storage != null) {
                storages.add(storage)
            }
        }
    }

    override fun read(tag: CompoundTag) {
        super.read(tag)
        readItems(disks, 0, tag)
    }

    override val id: ResourceLocation
        get() = ID

    override fun write(tag: CompoundTag): CompoundTag {
        super.write(tag)
        writeItems(disks, 0, tag)
        return tag
    }

    override fun writeConfiguration(tag: CompoundTag): CompoundTag {
        super.writeConfiguration(tag)
        writeItems(itemFilters, 1, tag)
        tag.put(NBT_FLUID_FILTERS, fluidFilters.writeToNbt())
        tag.putInt(NBT_PRIORITY, priority)
        tag.putInt(NBT_COMPARE, compare)
        tag.putInt(NBT_MODE, mode)
        tag.putInt(NBT_TYPE, type)
        writeAccessType(tag, accessType)
        return tag
    }

    override fun readConfiguration(tag: CompoundTag) {
        super.readConfiguration(tag)
        readItems(itemFilters, 1, tag)
        if (tag.contains(NBT_FLUID_FILTERS)) {
            fluidFilters.readFromNbt(tag.getCompound(NBT_FLUID_FILTERS))
        }
        if (tag.contains(NBT_PRIORITY)) {
            priority = tag.getInt(NBT_PRIORITY)
        }
        if (tag.contains(NBT_COMPARE)) {
            compare = tag.getInt(NBT_COMPARE)
        }
        if (tag.contains(NBT_MODE)) {
            mode = tag.getInt(NBT_MODE)
        }
        if (tag.contains(NBT_TYPE)) {
            type = tag.getInt(NBT_TYPE)
        }
        accessType = readAccessType(tag)
    }

    override var whitelistBlacklistMode: Int
        get() = mode
        set(mode) {
            this.mode = mode
            markDirty()
        }

    val diskState: Array<Any?>
        get() {
            val diskStates: Array<DiskState?> = arrayOfNulls<DiskState>(8)
            for (i in 0..7) {
                var state: DiskState = DiskState.NONE
                if (itemDisks[i] != null || fluidDisks[i] != null) {
                    state = if (!canUpdate()) {
                        DiskState.DISCONNECTED
                    } else {
                        DiskState.get(
                                if (itemDisks[i] != null) itemDisks[i]!!.getStored() else fluidDisks[i]!!.getStored(),
                                if (itemDisks[i] != null) itemDisks[i]!!.capacity else fluidDisks[i]!!.capacity
                        )
                    }
                }
                diskStates[i] = state
            }
            return diskStates
        }

    fun getDisks(): IItemHandler {
        return disks
    }

    override fun getType(): Int {
        return if (world.isRemote) DiskDriveTile.TYPE.value else type
    }

    override fun setType(type: Int) {
        this.type = type
        markDirty()
    }

    override fun getItemFilters(): IItemHandlerModifiable {
        return itemFilters
    }

    override val drops: IItemHandler
        get() = disks

    companion object {
        val ID: Identifier = Identifier(RS.ID, "disk_drive")
        private const val NBT_PRIORITY = "Priority"
        private const val NBT_COMPARE = "Compare"
        private const val NBT_MODE = "Mode"
        private const val NBT_TYPE = "Type"
        private const val NBT_FLUID_FILTERS = "FluidFilters"
        private const val DISK_STATE_UPDATE_THROTTLE = 30
        private val LOGGER = LogManager.getLogger(DiskDriveNetworkNode::class.java)
    }
}