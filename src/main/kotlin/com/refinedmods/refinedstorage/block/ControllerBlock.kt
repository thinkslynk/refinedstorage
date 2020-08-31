package com.refinedmods.refinedstorage.block

//import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
//import com.refinedmods.refinedstorage.apiimpl.network.Network
//import com.refinedmods.refinedstorage.tile.ControllerTile
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.network.NetworkType
import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.apiimpl.network.Network
import com.refinedmods.refinedstorage.tile.CreativeControllerTile
import com.refinedmods.refinedstorage.tile.NormalControllerTile
import com.refinedmods.refinedstorage.util.BlockUtils
import com.thinkslynk.fabric.annotations.registry.RegisterBlock
import com.thinkslynk.fabric.annotations.registry.RegisterBlockItem
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.StringIdentifiable
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World
import team.reborn.energy.Energy
import team.reborn.energy.EnergySide

@RegisterBlock(RS.ID, ControllerBlock.ID)
@RegisterBlockItem(RS.ID, ControllerBlock.ID, "CURED_STORAGE")
open class ControllerBlock(val type: NetworkType = NetworkType.NORMAL):
        BaseBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES),
        BlockEntityProvider
{
    enum class EnergyType(val string: String): StringIdentifiable {
        OFF("off"),
        NEARLY_OFF("nearly_off"),
        NEARLY_ON("nearly_on"),
        ON("on");
        override fun asString(): String {
            return string
        }
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(ENERGY_TYPE)
    }

    override fun createBlockEntity(world: BlockView): BlockEntity {
        return when (type){
            NetworkType.NORMAL -> NormalControllerTile()
            NetworkType.CREATIVE -> CreativeControllerTile()
        }
    }


    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
        super.onPlaced(world, pos, state, placer, itemStack)
        if (!world.isClient) {
            if (Energy.valid(itemStack)){
                val tile: BlockEntity? = world.getBlockEntity(pos)
                if (tile != null) {
                    if (Energy.valid(tile)) {
                        Energy.of(itemStack).into(Energy.of(tile)).move()
                    }
                }
            }

            RS.log.info("blockEntity is a valid energy object:" + Energy.valid(world.getBlockEntity(pos)))
        }
    }

    override fun neighborUpdate(state: BlockState, world: World, pos: BlockPos, block: Block, fromPos: BlockPos, notify: Boolean) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify)
        if (!world.isClient) {
            val network = API
                .getNetworkManager(world as ServerWorld)
                    .getNetwork(pos)

            if (network is Network) {
                RS.log.info("power: " + network.energyStorage.getStored(EnergySide.UNKNOWN))

                network.redstonePowered = world.isReceivingRedstonePower(pos)
            }
        }
    }

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        // TODO Figure out how to port the GUI
//        return if (!world.isClient) {
//            NetworkUtils.attemptModify(world, pos, hit.side, player) {
//                NetworkHooks.openGui(
//                        player as ServerPlayerEntity?,
//                        object : INamedContainerProvider() {
//                            val displayName: Text
//                                get() {
//                                    return@attemptModify TranslationTextComponent("gui.refinedstorage." + (if (type == NetworkType.CREATIVE) "creative_" else "") + "controller")
//                                }
//
//                            fun createMenu(i: Int, playerInventory: PlayerInventory?, player: PlayerEntity?): Container {
//                                return@attemptModify ControllerContainer(world.getBlockEntity(pos) as ControllerTile?, player, i)
//                            }
//                        },
//                        pos
//                )
//            }
//        } else ActionResult.SUCCESS

        return ActionResult.SUCCESS
    }

    companion object {
        const val ID = "controller"
        const val CREATIVE_ID = "creative_controller"
        @JvmField
        val ENERGY_TYPE: EnumProperty<EnergyType> = EnumProperty.of("energy_type", EnergyType::class.java)
    }

    init {
        defaultState = stateManager.defaultState.with(ENERGY_TYPE, EnergyType.OFF)
    }

}

@RegisterBlock(RS.ID, ControllerBlock.CREATIVE_ID)
@RegisterBlockItem(RS.ID, ControllerBlock.CREATIVE_ID, "CURED_STORAGE")
class CreativeControllerBlock: ControllerBlock(NetworkType.CREATIVE)