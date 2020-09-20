package com.refinedmods.refinedstorage.apiimpl.network.node

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler
import com.refinedmods.refinedstorage.inventory.item.UpgradeItemHandler
import com.refinedmods.refinedstorage.inventory.listener.InventoryListener
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeFluidInventoryListener
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener
import com.refinedmods.refinedstorage.tile.DestructorTile
import com.refinedmods.refinedstorage.tile.config.IComparable
import com.refinedmods.refinedstorage.tile.config.IType
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist
import com.refinedmods.refinedstorage.util.StackUtils
import com.refinedmods.refinedstorage.util.WorldUtils
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.world.World
import net.minecraft.world.chunk.WorldChunk
import java.util.*

class DestructorNetworkNode(world: World, pos: BlockPos) : NetworkNode(world, pos), IComparable, IWhitelistBlacklist, IType {
    override val itemFilters: BaseItemHandler = BaseItemHandler(9).addListener(NetworkNodeInventoryListener(this))
    override val fluidFilters: FluidInventory = FluidInventory(9).addListener(NetworkNodeFluidInventoryListener(this))
    val upgrades: UpgradeItemHandler = UpgradeItemHandler(4)
            // TODO UpgradeItemHandler(4, UpgradeItem.Type.SPEED, UpgradeItem.Type.SILK_TOUCH, UpgradeItem.Type.FORTUNE_1, UpgradeItem.Type.FORTUNE_2, UpgradeItem.Type.FORTUNE_3)
            .addListener(NetworkNodeInventoryListener(this))
            .addListener(object : InventoryListener<BaseItemHandler> {
                override fun onChanged(handler: BaseItemHandler, slot: Int, reading: Boolean) {
                    tool = createTool()
                }
            }) as UpgradeItemHandler
    override val drops: Inventory = upgrades
    override var compare: Int = IComparer.COMPARE_NBT
        set(v) {
            field = v
            markDirty()
        }
    private var mode: Int = IWhitelistBlacklist.BLACKLIST
    override var type: Int = IType.ITEMS
        get(): Int {
            return if (world.isClient) DestructorTile.TYPE.value else field
        }
        set(type) {
            field = type
            markDirty()
        }
    var isPickupItem = false
    private var tool: ItemStack = createTool()
    override val energyUsage: Double
        get() = RS.CONFIG.serverConfig.destructor.usage + upgrades.energyUsage

    override fun update() {
        super.update()
        if (canUpdate() && ticks % upgrades.getSpeed(BASE_SPEED, 4) == 0 && !world.isAir(pos)) {
            if (type == IType.ITEMS) {
                if (isPickupItem) {
                    pickupItems()
                } else {
                    breakBlock()
                }
            } else if (type == IType.FLUIDS) {
                breakFluid()
            }
        }
    }

    private fun pickupItems() {
        network?.let{ network ->
            val front: BlockPos = pos.offset(direction)
            val droppedItems: List<Entity> = ArrayList()
            val chunk: WorldChunk = world.getWorldChunk(front)

            chunk.collectOtherEntities(null, Box(front), droppedItems, null)
            val entity = droppedItems
                .asSequence()
                .filter { it is ItemEntity }
                .map { it as ItemEntity }
                .firstOrNull {
                    val droppedItem: ItemStack = it.stack
                    IWhitelistBlacklist.acceptsItem(itemFilters, mode, compare, droppedItem) &&
                            network.insertItem(droppedItem, droppedItem.count, Action.SIMULATE).isEmpty
                }

            entity?.let {
                val copy = it.stack
                network.insertItemTracked(copy, copy.count)
                it.remove()
            }

        }
    }

    private fun breakBlock() {
        val front: BlockPos = pos.offset(direction)
        val frontBlockState: BlockState = world.getBlockState(front)
        val frontBlock: Block = frontBlockState.block

        val frontStack: ItemStack = frontBlock.getPickStack(world, front, frontBlockState)
        if (!frontStack.isEmpty &&
                IWhitelistBlacklist.acceptsItem(itemFilters, mode, compare, frontStack) &&
                frontBlockState.getHardness(world, front) != -1.0f) {
            val drops: List<ItemStack> = Block.getDroppedStacks(
                    frontBlockState,
                    world as ServerWorld, // Is this only called on a server?
                    front,
                    world.getBlockEntity(front),
                    WorldUtils.getFakePlayer(world, owner),
                    tool
            )
            drops.forEach { drop ->
                // how do we know network isn't null?
                if (!network!!.insertItem(drop, drop.count, Action.SIMULATE).isEmpty) {
                    return
                }
            }

            // TODO Events
//            val e: BlockEvent.BreakEvent = BreakEvent(world, front, frontBlockState, WorldUtils.getFakePlayer(world as ServerWorld, owner))
//            if (!MinecraftForge.EVENT_BUS.post(e)) {
//                frontBlock.onBlockHarvested(world, front, frontBlockState, WorldUtils.getFakePlayer(world as ServerWorld, owner))
//                world.removeBlock(front, false)
//                for (drop in drops) {
//                    // We check if the controller isn't null here because when a destructor faces a node and removes it
//                    // it will essentially remove this block itself from the network without knowing
//                    if (network == null) {
//                        InventoryHelper.spawnItemStack(world, front.getX(), front.getY(), front.getZ(), drop)
//                    } else {
//                        network!!.insertItemTracked(drop, drop.getCount())
//                    }
//                }
//            }
        }
    }

    private fun breakFluid() {
        // TODO Fluid
//        val front: BlockPos = pos.offset(direction)
//        val frontBlockState: BlockState = world.getBlockState(front)
//        val frontBlock: Block = frontBlockState.getBlock()
//        if (frontBlock is FlowableFluid) {
//            // @Volatile: Logic from FlowingFluidBlock#pickupFluid
//            if (frontBlockState.get(FlowableFluid.LEVEL) == 0) {
//                val fluid: Fluid = frontBlock.flowing
//                val stack = FluidInstance(fluid, FluidAttributes.BUCKET_VOLUME)
//                if (IWhitelistBlacklist.acceptsFluid(fluidFilters, mode, compare, stack) &&
//                        network!!.insertFluid(stack, stack.getAmount(), Action.SIMULATE).isEmpty()) {
//                    network!!.insertFluidTracked(stack, stack.getAmount())
//                    world.setBlockState(front, Blocks.AIR.getDefaultState(), 11)
//                }
//            }
//        } else if (frontBlock is IFluidBlock) {
//            val fluidBlock: IFluidBlock = frontBlock as IFluidBlock
//            if (fluidBlock.canDrain(world, front)) {
//                val simulatedDrain: FluidInstance = fluidBlock.drain(world, front, IFluidHandler.FluidAction.SIMULATE)
//                if (IWhitelistBlacklist.acceptsFluid(fluidFilters, mode, compare, simulatedDrain) &&
//                        network!!.insertFluid(simulatedDrain, simulatedDrain.getAmount(), Action.SIMULATE).isEmpty()) {
//                    val drained: FluidInstance = fluidBlock.drain(world, front, IFluidHandler.FluidAction.EXECUTE)
//                    network!!.insertFluidTracked(drained, drained.getAmount())
//                }
//            }
//        }
    }

    private fun createTool(): ItemStack {
        val tool = ItemStack(Items.DIAMOND_PICKAXE)
        // TODO Upgrades
//        if (upgrades.hasUpgrade(UpgradeItem.Type.SILK_TOUCH)) {
//            tool.addEnchantment(Enchantments.SILK_TOUCH, 1)
//        } else if (upgrades.hasUpgrade(UpgradeItem.Type.FORTUNE_3)) {
//            tool.addEnchantment(Enchantments.FORTUNE, 3)
//        } else if (upgrades.hasUpgrade(UpgradeItem.Type.FORTUNE_2)) {
//            tool.addEnchantment(Enchantments.FORTUNE, 2)
//        } else if (upgrades.hasUpgrade(UpgradeItem.Type.FORTUNE_1)) {
//            tool.addEnchantment(Enchantments.FORTUNE, 1)
//        }
        return tool
    }

    override var whitelistBlacklistMode: Int
        get() = mode
        set(mode) {
            this.mode = mode
            markDirty()
        }

    override fun read(tag: CompoundTag) {
        super.read(tag)
        StackUtils.readItems(upgrades, 1, tag)
    }

    override val id: Identifier
        get() = ID

    override fun write(tag: CompoundTag): CompoundTag {
        super.write(tag)
        StackUtils.writeItems(upgrades, 1, tag)
        return tag
    }

    override fun writeConfiguration(tag: CompoundTag): CompoundTag {
        super.writeConfiguration(tag)
        tag.putInt(NBT_COMPARE, compare)
        tag.putInt(NBT_MODE, mode)
        tag.putInt(NBT_TYPE, type)
        tag.putBoolean(NBT_PICKUP, isPickupItem)
        StackUtils.writeItems(itemFilters, 0, tag)
        tag.put(NBT_FLUID_FILTERS, fluidFilters.writeToNbt())
        return tag
    }

    override fun readConfiguration(tag: CompoundTag) {
        super.readConfiguration(tag)
        if (tag.contains(NBT_COMPARE)) {
            compare = tag.getInt(NBT_COMPARE)
        }
        if (tag.contains(NBT_MODE)) {
            mode = tag.getInt(NBT_MODE)
        }
        if (tag.contains(NBT_TYPE)) {
            type = tag.getInt(NBT_TYPE)
        }
        if (tag.contains(NBT_PICKUP)) {
            isPickupItem = tag.getBoolean(NBT_PICKUP)
        }
        StackUtils.readItems(itemFilters, 0, tag)
        if (tag.contains(NBT_FLUID_FILTERS)) {
            fluidFilters.readFromNbt(tag.getCompound(NBT_FLUID_FILTERS))
        }
    }

    companion object {
        @kotlin.jvm.JvmField
        val ID: Identifier = Identifier(RS.ID, "destructor")
        private const val NBT_COMPARE = "Compare"
        private const val NBT_MODE = "Mode"
        private const val NBT_TYPE = "Type"
        private const val NBT_PICKUP = "Pickup"
        private const val NBT_FLUID_FILTERS = "FluidFilters"
        private const val BASE_SPEED = 20
    }
}