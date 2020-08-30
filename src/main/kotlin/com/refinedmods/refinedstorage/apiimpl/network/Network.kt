package com.refinedmods.refinedstorage.apiimpl.network

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingManager
import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.network.INetworkNodeGraph
import com.refinedmods.refinedstorage.api.network.INetworkNodeGraphListener
import com.refinedmods.refinedstorage.api.network.NetworkType
import com.refinedmods.refinedstorage.api.network.grid.handler.IFluidGridHandler
import com.refinedmods.refinedstorage.api.network.grid.handler.IItemGridHandler
import com.refinedmods.refinedstorage.api.network.item.INetworkItemManager
import com.refinedmods.refinedstorage.api.network.security.ISecurityManager
import com.refinedmods.refinedstorage.api.storage.AccessType
import com.refinedmods.refinedstorage.api.storage.IStorage
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache
import com.refinedmods.refinedstorage.api.storage.externalstorage.IExternalStorage
import com.refinedmods.refinedstorage.api.storage.tracker.IStorageTracker
import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.apiimpl.network.node.RootNetworkNode
import com.refinedmods.refinedstorage.block.ControllerBlock
import com.refinedmods.refinedstorage.config.ServerConfig
import com.refinedmods.refinedstorage.energy.BaseEnergyStorage
import com.refinedmods.refinedstorage.tile.config.IRedstoneConfigurable
import com.refinedmods.refinedstorage.tile.config.RedstoneMode
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.apache.logging.log4j.LogManager
import reborncore.common.fluid.container.FluidInstance
import team.reborn.energy.EnergyStorage
import java.util.function.Predicate

class Network(override val world: World,
//    override val itemGridHandler: IItemGridHandler = ItemGridHandler(this)
//    override val fluidGridHandler: IFluidGridHandler = FluidGridHandler(this)
//    override val networkItemManager: INetworkItemManager = NetworkItemManager(this)
//    private val nodeGraph: INetworkNodeGraph = NetworkNodeGraph(this)
//    private val craftingManager: ICraftingManager = CraftingManager(this)
//    private val securityManager: ISecurityManager = SecurityManager(this)
//    private val itemStorage: IStorageCache<ItemStack> = ItemStorageCache(this)
//    private val itemStorageTracker: ItemStorageTracker = ItemStorageTracker(Runnable { markDirty() })
//    private val fluidStorage: IStorageCache<FluidInstance> = FluidStorageCache(this)
//    private val fluidStorageTracker: FluidStorageTracker = FluidStorageTracker(Runnable { markDirty() })
//    private val energy: BaseEnergyStorage = BaseEnergyStorage(RS.SERVER_CONFIG.getController().getCapacity(), RS.SERVER_CONFIG.getController().getMaxTransfer(), 0)
//              internal var root: RootNetworkNode,
              override val position: BlockPos,
              override val type: NetworkType
) : INetwork, IRedstoneConfigurable {
    private var lastEnergyType: ControllerBlock.EnergyType = ControllerBlock.EnergyType.OFF
    override var energyUsage = 0.0
    override var redstoneMode: RedstoneMode = RedstoneMode.IGNORE
         set(mode) {
            field = mode
            markDirty()
        }
    var redstonePowered = false
    var amILoaded = false
    var throttlingDisabled = true // Will be enabled after first update
    var couldRun = false
    var ticksSinceUpdateChanged = 0
    var ticks = 0

    val root = RootNetworkNode(this, world, position)
    override val energyStorage = BaseEnergyStorage(ServerConfig.controllerCapacity, ServerConfig.controllerMaxTransfer, 0.0)


    override fun canRun(): Boolean {
        return amILoaded && energyStorage.energy >= energyUsage && redstoneMode.isEnabled(redstonePowered)
    }

    override fun update() {
        if (!world.isClient) {
            RS.log.info("energy: " + energyStorage.energy)
            if (ticks == 0) {
                redstonePowered = world.isReceivingRedstonePower(position)
            }
            ++ticks
            amILoaded = world.getBlockState(position) != null
            updateEnergyUsage()
            if (canRun()) {
//                craftingManager.update()
//                if (!craftingManager.getTasks().isEmpty()) {
//                    markDirty()
//                }
            }
            if (type == NetworkType.NORMAL) {
                // TODO energy
                if (!ServerConfig.controllerUseEnergy) {
                    energyStorage.setStored(energyStorage.maxStoredPower)
                } else {
                    energyStorage.extractEnergyBypassCanExtract(energyUsage, false)
                }
            } else if (type == NetworkType.CREATIVE) {
//                 TODO energy
                energyStorage.setStored(energyStorage.maxStoredPower)
            }
            val canRun = canRun()
            if (couldRun != canRun) {
                ++ticksSinceUpdateChanged
                if ((if (canRun) ticksSinceUpdateChanged > THROTTLE_INACTIVE_TO_ACTIVE else ticksSinceUpdateChanged > THROTTLE_ACTIVE_TO_INACTIVE) || throttlingDisabled) {
                    ticksSinceUpdateChanged = 0
                    couldRun = canRun
                    throttlingDisabled = false
                    LOGGER.debug("Network at position {} changed running state to {}, causing an invalidation of the node graph", position, couldRun)
//                    nodeGraph.invalidate(Action.PERFORM, world, position)
//                    securityManager.invalidate()
                }
            } else {
                ticksSinceUpdateChanged = 0
            }
            val energyType: ControllerBlock.EnergyType = energyType
            if (lastEnergyType != energyType) {
                RS.log.info("energyType replaced to $energyType")
                lastEnergyType = energyType
                val state: BlockState = world.getBlockState(position)
                if (state.block is ControllerBlock) {
                    world.setBlockState(position, state.with(ControllerBlock.ENERGY_TYPE, energyType))
                }
            }
        }
    }

    override fun onRemoved() {
//        for (task in craftingManager.getTasks()) {
//            task.onCancelled()
//        }
//        nodeGraph.disconnectAll()
    }

    override fun insertItem(stack: ItemStack, size: Int, action: Action): ItemStack {
        var size = size
        if (stack.isEmpty()) {
            return stack
        }
//        if (itemStorage.getStorages().isEmpty()) {
//            return ItemHandlerHelper.copyStackWithSize(stack, size)
//        }
//        var remainder: ItemStack? = stack
//        var inserted = 0
//        var insertedExternally = 0
//        for (storage in itemStorage.getStorages()) {
//            if (storage.getAccessType() === AccessType.EXTRACT) {
//                continue
//            }
//            val storedPre: Int = storage.getStored()
//            remainder = storage.insert(remainder, size, action)
//            if (action === Action.PERFORM) {
//                inserted += storage.getCacheDelta(storedPre, size, remainder)
//            }
//            if (remainder.isEmpty()) {
//                // The external storage is responsible for sending changes, we don't need to anymore
//                if (storage is IExternalStorage<*> && action === Action.PERFORM) {
//                    (storage as IExternalStorage<*>).update(this)
//                    insertedExternally += size
//                }
//                break
//            } else {
//                // The external storage is responsible for sending changes, we don't need to anymore
//                if (size != remainder.getCount() && storage is IExternalStorage<*> && action === Action.PERFORM) {
//                    (storage as IExternalStorage<*>).update(this)
//                    insertedExternally += size - remainder.getCount()
//                }
//                size = remainder.getCount()
//            }
//        }
//        if (action === Action.PERFORM && inserted - insertedExternally > 0) {
//            itemStorage.add(stack, inserted - insertedExternally, false, false)
//        }
//        return remainder
        return stack
    }

    override fun extractItem(stack: ItemStack, size: Int, flags: Int, action: Action, filter: Predicate<IStorage<ItemStack>>): ItemStack {
        if (stack.isEmpty()) {
            return stack
        }
        var received = 0
        var extractedExternally = 0
        var newStack: ItemStack = ItemStack.EMPTY
//        for (storage in itemStorage.getStorages()) {
//            var took: ItemStack = ItemStack.EMPTY
//            if (filter!!.test(storage) && storage.getAccessType() !== AccessType.INSERT) {
//                took = storage.extract(stack, size - received, flags, action)
//            }
//            if (!took.isEmpty()) {
//                // The external storage is responsible for sending changes, we don't need to anymore
//                if (storage is IExternalStorage<*> && action === Action.PERFORM) {
//                    (storage as IExternalStorage<*>).update(this)
//                    extractedExternally += took.getCount()
//                }
//                if (newStack.isEmpty()) {
//                    newStack = took
//                } else {
//                    newStack.grow(took.getCount())
//                }
//                received += took.getCount()
//            }
//            if (size == received) {
//                break
//            }
//        }
//        if (newStack.getCount() - extractedExternally > 0 && action === Action.PERFORM) {
//            itemStorage.remove(newStack, newStack.getCount() - extractedExternally, false)
//        }
        return newStack
    }

    override fun insertFluid(stack: FluidInstance, size: Int, action: Action): FluidInstance {
        var size = size
        if (stack.isEmpty()) {
            return stack
        }
//        if (fluidStorage.getStorages().isEmpty()) {
//            return StackUtils.copy(stack, size)
//        }
//        var remainder: FluidInstance? = stack
//        var inserted = 0
//        var insertedExternally = 0
//        for (storage in fluidStorage.getStorages()) {
//            if (storage.getAccessType() === AccessType.EXTRACT) {
//                continue
//            }
//            val storedPre: Int = storage.getStored()
//            remainder = storage.insert(remainder, size, action)
//            if (action === Action.PERFORM) {
//                inserted += storage.getCacheDelta(storedPre, size, remainder)
//            }
//            if (remainder.isEmpty()) {
//                // The external storage is responsible for sending changes, we don't need to anymore
//                if (storage is IExternalStorage<*> && action === Action.PERFORM) {
//                    (storage as IExternalStorage<*>).update(this)
//                    insertedExternally += size
//                }
//                break
//            } else {
//                // The external storage is responsible for sending changes, we don't need to anymore
//                if (size != remainder.getAmount() && storage is IExternalStorage<*> && action === Action.PERFORM) {
//                    (storage as IExternalStorage<*>).update(this)
//                    insertedExternally += size - remainder.getAmount()
//                }
//                size = remainder.getAmount()
//            }
//        }
//        if (action === Action.PERFORM && inserted - insertedExternally > 0) {
//            fluidStorage.add(stack, inserted - insertedExternally, false, false)
//        }
//        return remainder
        return stack
    }

    override fun extractFluid(stack: FluidInstance, size: Int, flags: Int, action: Action, filter: Predicate<IStorage<FluidInstance>>): FluidInstance {
        if (stack.isEmpty()) {
            return stack
        }
        var received = 0
        var extractedExternally = 0
        var newStack: FluidInstance = FluidInstance.EMPTY
//        for (storage in fluidStorage.getStorages()) {
//            var took: FluidInstance = FluidInstance.EMPTY
//            if (filter!!.test(storage) && storage.getAccessType() !== AccessType.INSERT) {
//                took = storage.extract(stack, size - received, flags, action)
//            }
//            if (!took.isEmpty()) {
//                // The external storage is responsible for sending changes, we don't need to anymore
//                if (storage is IExternalStorage<*> && action === Action.PERFORM) {
//                    (storage as IExternalStorage<*>).update(this)
//                    extractedExternally += took.getAmount()
//                }
//                if (newStack.isEmpty()) {
//                    newStack = took
//                } else {
//                    newStack.grow(took.getAmount())
//                }
//                received += took.getAmount()
//            }
//            if (size == received) {
//                break
//            }
//        }
//        if (newStack.getAmount() - extractedExternally > 0 && action === Action.PERFORM) {
//            fluidStorage.remove(newStack, newStack.getAmount() - extractedExternally, false)
//        }
        return newStack
    }

    override fun readFromNbt(tag: CompoundTag): INetwork {
        if (tag.contains(NBT_ENERGY)) {
            energyStorage.setStored(tag.getDouble(NBT_ENERGY))
        }
        redstoneMode = RedstoneMode.read(tag)
//        craftingManager.readFromNbt(tag)
//        if (tag.contains(NBT_ITEM_STORAGE_TRACKER)) {
//            itemStorageTracker.readFromNbt(tag.getList(NBT_ITEM_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND))
//        }
//        if (tag.contains(NBT_FLUID_STORAGE_TRACKER)) {
//            fluidStorageTracker.readFromNbt(tag.getList(NBT_FLUID_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND))
//        }
        return this
    }

    override fun writeToNbt(tag: CompoundTag): CompoundTag {
        tag.putDouble(NBT_ENERGY, energyStorage.energy)
        redstoneMode.write(tag)
//        craftingManager.writeToNbt(tag)
//        tag.put(NBT_ITEM_STORAGE_TRACKER, itemStorageTracker.serializeNbt())
//        tag.put(NBT_FLUID_STORAGE_TRACKER, fluidStorageTracker.serializeNbt())
        return tag
    }

    override fun markDirty() {
        API.getNetworkManager(world as ServerWorld).markForSaving()
    }


    val energyType: ControllerBlock.EnergyType
        get() = if (!redstoneMode.isEnabled(redstonePowered)) {
            RS.log.info("block isn't running because of redstone")
            ControllerBlock.EnergyType.OFF
        } else getEnergyType(energyStorage.energy, energyStorage.maxStoredPower)

    private fun updateEnergyUsage() {
        if (!redstoneMode.isEnabled(redstonePowered)) {
            energyUsage = 0.0
            return
        }
        var usage: Double = ServerConfig.controllerBaseUsage
//        for (node in nodeGraph.all()!!) {
//            if (node!!.isActive) {
//                usage += node.energyUsage
//            }
//        }
        energyUsage = usage
    }

    companion object {
        private const val THROTTLE_INACTIVE_TO_ACTIVE = 20
        private const val THROTTLE_ACTIVE_TO_INACTIVE = 4
        private const val NBT_ENERGY = "Energy"
        private const val NBT_ITEM_STORAGE_TRACKER = "ItemStorageTracker"
        private const val NBT_FLUID_STORAGE_TRACKER = "FluidStorageTracker"
        private val LOGGER = LogManager.getLogger(Network::class.java)
        @JvmStatic
        fun getEnergyScaled(stored: Double, capacity: Double, scale: Int): Int {
            return (stored / capacity * scale.toDouble()).toInt()
        }

        fun getEnergyType(stored: Double, capacity: Double): ControllerBlock.EnergyType {
            val energy = getEnergyScaled(stored, capacity, 100)
            RS.log.info("energy percent: $energy%")
            return when {
                energy <= 0 -> {
                    ControllerBlock.EnergyType.OFF
                }
                energy <= 10 -> {
                    ControllerBlock.EnergyType.NEARLY_OFF
                }
                energy <= 20 -> {
                    ControllerBlock.EnergyType.NEARLY_ON
                }
                else -> ControllerBlock.EnergyType.ON
            }
        }
    }

    init {
//        nodeGraph.addListener(INetworkNodeGraphListener {
//            val tile: BlockEntity? = world.getBlockEntity(position)
//            if (tile is ControllerTile) {
//                (tile as ControllerTile).getDataManager().sendParameterToWatchers(ControllerTile.NODES)
//            }
//        })
    }
}