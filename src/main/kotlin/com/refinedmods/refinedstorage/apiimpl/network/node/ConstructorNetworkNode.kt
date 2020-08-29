package com.refinedmods.refinedstorage.apiimpl.network.node

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler
import com.refinedmods.refinedstorage.inventory.item.UpgradeItemHandler
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeFluidInventoryListener
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener
import com.refinedmods.refinedstorage.tile.ConstructorTile
import com.refinedmods.refinedstorage.tile.config.IComparable
import com.refinedmods.refinedstorage.tile.config.IType
import com.refinedmods.refinedstorage.util.StackUtils
import com.refinedmods.refinedstorage.util.WorldUtils
import net.minecraft.block.dispenser.ItemDispenserBehavior
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.FireworkRocketEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.PositionImpl
import net.minecraft.util.math.Vec3d.ZERO
import net.minecraft.world.World
import reborncore.common.fluid.container.FluidInstance

class ConstructorNetworkNode(world: World, pos: BlockPos):
        NetworkNode(world, pos),
        IComparable,
        IType
{
    override val itemFilters: BaseItemHandler = BaseItemHandler(1).addListener(NetworkNodeInventoryListener(this))
    override val fluidFilters: FluidInventory = FluidInventory(1)
            .addListener(NetworkNodeFluidInventoryListener(this))
    val upgrades: UpgradeItemHandler = UpgradeItemHandler(4)
    // TODO private val upgrades: UpgradeItemHandler = UpgradeItemHandler(4, UpgradeItem.Type.SPEED, UpgradeItem.Type.CRAFTING, UpgradeItem.Type.STACK)
            .addListener(NetworkNodeInventoryListener(this)) as UpgradeItemHandler
    override var compare: Int = IComparer.COMPARE_NBT
        set(compare) {
            field = compare
            markDirty()
        }
    override var type: Int = IType.ITEMS
         get(): Int{
            return if (world.isClient) ConstructorTile.TYPE.value!! else field
         }
        set(v) {
            field = v
            markDirty()
        }

    var isDrop = false
    override val energyUsage: Int
//        get() = RS.SERVER_CONFIG.getConstructor().getUsage() + upgrades.getEnergyUsage() // TODO add once server config is merged in
        get() = 0

    override fun update() {
        super.update()
        if (canUpdate() && ticks % upgrades.getSpeed(BASE_SPEED, 4) == 0 && !world.isAir(pos)) {
            if (type == IType.ITEMS && !itemFilters.getStack(0).isEmpty) {
                val stack: ItemStack = itemFilters.getStack(0)
                if (isDrop) {
                    extractAndDropItem(stack)
                } else if (stack.item === Items.FIREWORK_ROCKET) {
                    extractAndSpawnFireworks(stack)
                } else if (stack.item is BlockItem) {
                    extractAndPlaceBlock(stack)
                }
            } else if (type == IType.FLUIDS && !fluidFilters.getFluid(0).isEmpty()) {
                extractAndPlaceFluid(fluidFilters.getFluid(0))
            }
        }
    }

    private fun extractAndPlaceFluid(stack: FluidInstance) {
        // TODO Deal with fluid later
//        val front: BlockPos = pos.offset(direction)
//        if (network!!.extractFluid(stack, FluidAttributes.BUCKET_VOLUME, compare, Action.SIMULATE).getAmount() < FluidAttributes.BUCKET_VOLUME) {
//            if (upgrades.hasUpgrade(UpgradeItem.Type.CRAFTING)) {
//                network!!.craftingManager.request(this, stack, FluidAttributes.BUCKET_VOLUME)
//            }
//        } else {
//            FluidUtil.tryPlaceFluid(WorldUtils.getFakePlayer(world as ServerWorld, owner), world, Hand.MAIN_HAND, front, NetworkFluidHandler(StackUtils.copy(stack, FluidAttributes.BUCKET_VOLUME)), stack)
//        }
    }

    private fun extractAndPlaceBlock(stack: ItemStack) {
        val took: ItemStack = network!!.extractItem(stack, 1, compare, Action.SIMULATE)
        if (!took.isEmpty) {
            val ctx: ItemPlacementContext = ConstructorBlockItemUseContext(
                    world,
                    WorldUtils.getFakePlayer(world as ServerWorld, owner),
                    Hand.MAIN_HAND,
                    took,
                    BlockHitResult(ZERO, direction, pos, false)
            )

            val result: ActionResult = took.useOnBlock(ctx)
            if (result.isAccepted) {
                network!!.extractItem(stack, 1, Action.PERFORM)
            }
        }

        // TODO Upgrades
//        else if (upgrades.hasUpgrade(UpgradeItem.Type.CRAFTING)) {
//            val craft: ItemStack = itemFilters.getStackInSlot(0)
//            network!!.craftingManager.request(this, craft, 1)
//        }
    }

    private fun extractAndDropItem(stack: ItemStack) {
        val took: ItemStack = network!!.extractItem(stack, upgrades.stackInteractCount, Action.PERFORM)
        if (!took.isEmpty) {
            ItemDispenserBehavior.spawnItem(
                    world, took, 6, direction,
                    PositionImpl(dispensePositionX, dispensePositionY, dispensePositionZ)
            )
        }
        // TODO Upgrades
//        else if (upgrades.hasUpgrade(UpgradeItem.Type.CRAFTING)) {
//            network!!.craftingManager.request(this, stack, 1)
//        }
    }

    private fun extractAndSpawnFireworks(stack: ItemStack) {
        val took: ItemStack? = network!!.extractItem(stack, 1, Action.PERFORM)
        if (took?.isEmpty == false) {
            world.spawnEntity(FireworkRocketEntity(world, dispensePositionX, dispensePositionY, dispensePositionZ, took))
        }
    }

    // @Volatile: From BlockDispenser#getDispensePosition
    private val dispensePositionX: Double
        get() = pos.x.toDouble() + 0.5 + 0.8 * direction!!.offsetX.toDouble()

    // @Volatile: From BlockDispenser#getDispensePosition
    private val dispensePositionY: Double
        get() = pos.y.toDouble() + (if (direction == Direction.DOWN) 0.45 else 0.5) + 0.8 * direction!!.offsetY.toDouble()

    // @Volatile: From BlockDispenser#getDispensePosition
    private val dispensePositionZ: Double
        get() = pos.z.toDouble() + 0.5 + 0.8 * direction!!.offsetZ.toDouble()

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
        tag.putInt(NBT_TYPE, type)
        tag.putBoolean(NBT_DROP, isDrop)
        StackUtils.writeItems(itemFilters, 0, tag)
        tag.put(NBT_FLUID_FILTERS, fluidFilters.writeToNbt())
        return tag
    }

    override fun readConfiguration(tag: CompoundTag) {
        super.readConfiguration(tag)
        if (tag.contains(NBT_COMPARE)) {
            compare = tag.getInt(NBT_COMPARE)
        }
        if (tag.contains(NBT_TYPE)) {
            type = tag.getInt(NBT_TYPE)
        }
        if (tag.contains(NBT_DROP)) {
            isDrop = tag.getBoolean(NBT_DROP)
        }
        StackUtils.readItems(itemFilters, 0, tag)
        if (tag.contains(NBT_FLUID_FILTERS)) {
            fluidFilters.readFromNbt(tag.getCompound(NBT_FLUID_FILTERS))
        }
    }
    override val drops: Inventory
        get() = upgrades

//    private inner class NetworkFluidHandler(resource: FluidInstance) : IFluidHandler {
//        private val resource: FluidInstance
//        val tanks: Int
//            get() {
//                throw RuntimeException("Cannot be called")
//            }
//
//        fun getFluidInTank(tank: Int): FluidInstance {
//            throw RuntimeException("Cannot be called")
//        }
//
//        fun getTankCapacity(tank: Int): Int {
//            throw RuntimeException("Cannot be called")
//        }
//
//        fun isFluidValid(tank: Int, stack: FluidInstance): Boolean {
//            throw RuntimeException("Cannot be called")
//        }
//
//        fun fill(resource: FluidInstance?, action: FluidAction?): Int {
//            throw RuntimeException("Cannot be called")
//        }
//
//        fun drain(resource: FluidInstance, action: FluidAction): FluidInstance {
//            return network!!.extractFluid(resource, resource.getAmount(), if (action === FluidAction.SIMULATE) Action.SIMULATE else Action.PERFORM)
//        }
//
//        fun drain(maxDrain: Int, action: FluidAction): FluidInstance {
//            return network!!.extractFluid(resource, resource.getAmount(), if (action === FluidAction.SIMULATE) Action.SIMULATE else Action.PERFORM)
//        }
//
//        init {
//            this.resource = resource
//        }
//    }

    private class ConstructorBlockItemUseContext(world: World?, player: PlayerEntity?, hand: Hand?, stack: ItemStack?, rayTraceResult: BlockHitResult?):
            ItemPlacementContext(world, player, hand, stack, rayTraceResult)
    companion object {
        @kotlin.jvm.JvmField
        val ID: Identifier = Identifier(RS.ID, "constructor")
        private const val NBT_COMPARE = "Compare"
        private const val NBT_TYPE = "Type"
        private const val NBT_DROP = "Drop"
        private const val NBT_FLUID_FILTERS = "FluidFilters"
        private const val BASE_SPEED = 20
    }
}