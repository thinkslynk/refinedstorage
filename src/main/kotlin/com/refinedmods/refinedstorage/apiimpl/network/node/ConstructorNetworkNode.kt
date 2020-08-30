package com.refinedmods.refinedstorage.apiimpl.network.node

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.config.ServerConfig
import com.refinedmods.refinedstorage.extensions.*
import com.refinedmods.refinedstorage.tile.ConstructorTile
import com.refinedmods.refinedstorage.tile.config.IComparable
import com.refinedmods.refinedstorage.tile.config.IType
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
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import reborncore.common.util.Tank

class ConstructorNetworkNode(world: World?, pos: BlockPos?) : NetworkNode(world!!, pos!!), IComparable,
    IType {

    override val itemFilters: Inventory get() =TODO("BaseItemHandler(1).addListener(NetworkNodeInventoryListener(this))")
    override val fluidFilters: Tank get() = TODO("""FluidInventory(1)
        .addListener(NetworkNodeFluidInventoryListener(this))""")
    private val upgrades: Inventory get() = TODO("Implement UpgradeItemHandler (and make this a normal property)")
        /*UpgradeItemHandler(4, UpgradeItem.Type.SPEED, UpgradeItem.Type.CRAFTING, UpgradeItem.Type.STACK)
            .addListener(NetworkNodeInventoryListener(this)) as UpgradeItemHandler*/
    override var compare = IComparer.COMPARE_NBT
        set(value) {
            field = value
            markDirty()
        }
    override var type = IType.ITEMS
        get() {
            return if (world.isClient) ConstructorTile.TYPE.value else field
        }
        set(value) {
            field = value
            markDirty()
        }
    var isDrop = false
    override val energyUsage: Double
        get() = ServerConfig.constructorUsage/* + upgrades.getEnergyUsage()*/

    override fun update() {
        super.update()
        if (canUpdate()/* && ticks % upgrades.getSpeed(BASE_SPEED, 4) == 0*/) {
            when {
                type == IType.ITEMS && !itemFilters.getStackInSlot(0).isEmpty -> {
                    val stack: ItemStack = itemFilters.getStackInSlot(0)
                    when {
                        isDrop -> extractAndDropItem(stack)
                        stack.item === Items.FIREWORK_ROCKET -> extractAndSpawnFireworks(stack)
                        stack.item is BlockItem -> extractAndPlaceBlock(stack)
                    }
                }
                type == IType.FLUIDS && !fluidFilters.isEmpty -> {
                    // todo: fluids // extractAndPlaceFluid(fluidFilters.fluid))
                }
            }
        }
    }

    /*
        private fun extractAndPlaceFluid(stack: FluidStack) {
            val front = pos.offset(direction)
            if (network!!.extractFluid(
                    stack,
                    FluidAttributes.BUCKET_VOLUME,
                    compare,
                    Action.SIMULATE
                ).amount < FluidAttributes.BUCKET_VOLUME
            ) {
                if (upgrades.hasUpgrade(UpgradeItem.Type.CRAFTING)) {
                    network.getCraftingManager().request(this, stack, FluidAttributes.BUCKET_VOLUME)
                }
            } else {
                FluidUtil.tryPlaceFluid(
                    WorldUtils.getFakePlayer(
                        world as ServerWorld,
                        owner
                    ),
                    world,
                    Hand.MAIN_HAND,
                    front,
                    NetworkFluidHandler(
                        StackUtils.copy(stack, FluidAttributes.BUCKET_VOLUME)
                    ),
                    stack
                )
            }
        }
    */
    private fun extractAndPlaceBlock(stack: ItemStack) {
        val took =
            network!!.extractItem(stack, 1, compare, Action.SIMULATE)
        if (!took.isEmpty) {
            val ctx: ItemPlacementContext =
                ConstructorBlockItemUseContext(
                    world,
                    WorldUtils.getFakePlayer(world as ServerWorld, owner),
                    Hand.MAIN_HAND,
                    took,
                    BlockRayTraceResult(Vec3d.ZERO, direction, pos, false)
                )
            /* TODO: place block
            val result: ActionResultType = ForgeHooks.onPlaceItemIntoWorld(ctx)
            if (result === ActionResultType.SUCCESS) {
                network!!.extractItem(stack, 1, Action.PERFORM)
            }
             */
        } /* TODO: UpgradeItem
        else if (upgrades.hasUpgrade(UpgradeItem.Type.CRAFTING)) {
            val craft: ItemStack = itemFilters.getStackInSlot(0)
            network.getCraftingManager().request(this, craft, 1)
        }*/
    }

    private fun extractAndDropItem(stack: ItemStack) {
        val took = network!!.extractItem(
            stack,
            0, // TODO upgrades.getStackInteractCount(),
            Action.PERFORM
        )
        if (!took.isEmpty) {
            ItemDispenserBehavior.spawnItem(
                world,
                took,
                6,
                direction,
                Vec3d(dispensePositionX, dispensePositionY, dispensePositionZ)
            )
        }/* TODO:Crafting and UpgradeItem
         else if (upgrades.hasUpgrade(UpgradeItem.Type.CRAFTING)) {
            network.getCraftingManager().request(this, stack, 1)
        }*/
    }

    private fun extractAndSpawnFireworks(stack: ItemStack) {
        val took = network!!.extractItem(stack, 1, Action.PERFORM)
        if (!took.isEmpty) {
            world.spawnEntity(
                FireworkRocketEntity(
                    world,
                    dispensePositionX,
                    dispensePositionY,
                    dispensePositionZ,
                    took
                )
            )
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


    override fun read(tag: CompoundNBT) {
        super.read(tag)
        TODO("StackUtils.readItems(upgrades, 1, tag)")
    }

    override val id: ResourceLocation
        get() = ID

    override fun write(tag: CompoundNBT): CompoundNBT {
        super.write(tag)
        TODO("StackUtils.writeItems(upgrades, 1, tag)")
        return tag
    }

    override fun writeConfiguration(tag: CompoundNBT): CompoundNBT {
        super.writeConfiguration(tag)
        tag.putInt(NBT_COMPARE, compare)
        tag.putInt(NBT_TYPE, type)
        tag.putBoolean(NBT_DROP, isDrop)
        TODO("StackUtils.writeItems(itemFilters, 0, tag)")
        tag.put(NBT_FLUID_FILTERS, fluidFilters.write(CompoundTag()))
        return tag
    }

    override fun readConfiguration(tag: CompoundNBT) {
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
        TODO("StackUtils.readItems(itemFilters, 0, tag)")
        if (tag.contains(NBT_FLUID_FILTERS)) {
            fluidFilters.read(tag.getCompound(NBT_FLUID_FILTERS))
        }
    }

    override val drops: Inventory
        get() = upgrades

/*
    override fun getItemFilters(): IItemHandlerModifiable {
        return itemFilters
    }

    override fun getFluidFilters(): FluidInventory {
        return fluidFilters
    }

    private inner class NetworkFluidHandler(resource: FluidStack) : IFluidHandler {
        private val resource: FluidStack
        val tanks: Int
            get() {
                throw RuntimeException("Cannot be called")
            }

        @javax.annotation.Nonnull
        fun getFluidInTank(tank: Int): FluidStack {
            throw RuntimeException("Cannot be called")
        }

        fun getTankCapacity(tank: Int): Int {
            throw RuntimeException("Cannot be called")
        }

        fun isFluidValid(tank: Int, @javax.annotation.Nonnull stack: FluidStack?): Boolean {
            throw RuntimeException("Cannot be called")
        }

        fun fill(resource: FluidStack?, action: FluidAction?): Int {
            throw RuntimeException("Cannot be called")
        }

        @javax.annotation.Nonnull
        fun drain(resource: FluidStack, action: FluidAction): FluidStack {
            return network!!.extractFluid(
                resource,
                resource.getAmount(),
                if (action === FluidAction.SIMULATE) Action.SIMULATE else Action.PERFORM
            )
        }

        @javax.annotation.Nonnull
        fun drain(maxDrain: Int, action: FluidAction): FluidStack {
            return network!!.extractFluid(
                resource,
                resource.getAmount(),
                if (action === FluidAction.SIMULATE) Action.SIMULATE else Action.PERFORM
            )
        }

        init {
            this.resource = resource
        }
    }*/

    private class ConstructorBlockItemUseContext(
        world: World?,
        player: PlayerEntity?,
        hand: Hand?,
        stack: ItemStack?,
        rayTraceResult: BlockRayTraceResult
    ) :
        ItemPlacementContext(world, player, hand, stack, rayTraceResult)

    companion object {
        val ID: ResourceLocation = ResourceLocation(RS.ID, "constructor")
        private const val NBT_COMPARE = "Compare"
        private const val NBT_TYPE = "Type"
        private const val NBT_DROP = "Drop"
        private const val NBT_FLUID_FILTERS = "FluidFilters"
        private const val BASE_SPEED = 20
    }
}