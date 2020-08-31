package com.refinedmods.refinedstorage.block

//import com.refinedmods.refinedstorage.container.StorageMonitorContainer
//import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerProvider
//import com.refinedmods.refinedstorage.tile.StorageMonitorTile
//import com.refinedmods.refinedstorage.util.NetworkUtils
//import com.refinedmods.refinedstorage.util.WorldUtils
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.util.BlockUtils
import com.thinkslynk.fabric.annotations.registry.RegisterBlock
import com.thinkslynk.fabric.annotations.registry.RegisterBlockItem
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.state.StateManager
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

@RegisterBlock(RS.ID, StorageMonitorBlock.ID)
@RegisterBlockItem(RS.ID, StorageMonitorBlock.ID, "CURED_STORAGE")
class StorageMonitorBlock:
        NetworkNodeBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES)
//        BlockEntityProvider
{
    companion object {
        const val ID = "storage_monitor"
    }
    override val direction: BlockDirection
        get() = BlockDirection.HORIZONTAL

    init {
        defaultState = defaultState.with(CONNECTED, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(CONNECTED)
    }

//    override fun createBlockEntity(world: BlockView?): BlockEntity?
//            = NoOpBlockEntity()
    // TODO BlockEntities
//            = StorageMonitorTile()

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        // TODO Port gui
//        if (!world.isClient) {
//            val held: ItemStack = player.inventory.getCurrentItem()
//            return if (player.isCrouching()) {
//                NetworkUtils.attemptModify(world, pos, hit.getFace(), player) {
//                    NetworkHooks.openGui(
//                            player as ServerPlayerEntity,
//                            PositionalTileContainerProvider<StorageMonitorTile>(
//                                    TranslationTextComponent("gui.refinedstorage.storage_monitor"),
//                                    { tile: StorageMonitorTile?, windowId: Int, inventory: PlayerInventory?, p: PlayerEntity? -> StorageMonitorContainer(tile, player, windowId) },
//                                    pos
//                            ),
//                            pos
//                    )
//                }
//            } else {
//                val storageMonitor = (world.getBlockEntity(pos) as StorageMonitorTile?)!!.node
//                if (!held.isEmpty) {
//                    storageMonitor.deposit(player, held)
//                } else {
//                    storageMonitor.depositAll(player)
//                }
//            }
//        }
        return ActionResult.SUCCESS
    }

    // TODO Replace on block clicked... maybe this info is in on use?
//    fun onBlockClicked(state: BlockState?, world: World, pos: BlockPos?, player: PlayerEntity?) {
//        super.onBlockClicked(state, world, pos, player)
//        if (!world.isClient) {
//            val result: RayTraceResult = WorldUtils.rayTracePlayer(world, player) as? BlockRayTraceResult ?: return
//            (world.getBlockEntity(pos) as StorageMonitorTile?)!!.node.extract(player, (result as BlockRayTraceResult).getFace())
//        }
//    }

}